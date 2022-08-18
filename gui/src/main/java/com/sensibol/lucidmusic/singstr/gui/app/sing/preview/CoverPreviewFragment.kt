package com.sensibol.lucidmusic.singstr.gui.app.sing.preview

import android.app.Activity
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.util.MimeTypes
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.karaoke.Doorway
import com.sensibol.karaoke.Doorway.getRecordIntentBuilder
import com.sensibol.lucidmusic.singstr.domain.model.SingScore
import com.sensibol.lucidmusic.singstr.domain.model.names
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.SingstrViewModel
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentCoverPreviewBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import com.sensibol.lucidmusic.singstr.gui.isFakeSinging
import com.sensibol.lucidmusic.singstr.usecase.PrepareCreateCoverArgsUseCase
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import kotlin.math.roundToInt

@AndroidEntryPoint
class CoverPreviewFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_cover_preview
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentCoverPreviewBinding::inflate
    override val binding: FragmentCoverPreviewBinding get() = super.binding as FragmentCoverPreviewBinding

    private val viewModel: CoverPreviewViewModel by viewModels()
    private val singstrViewModel: SingstrViewModel by activityViewModels()

    private val args: CoverPreviewFragmentArgs by navArgs()
    private lateinit var exoPlayer: ExoPlayer
    private var mixPath:String=""

    override fun onInitView() {
        binding.apply {
            pbFinal.progress = args.singScore.timingScore.roundToInt()
            tvFinalScore.text = args.singScore.totalScore.roundToInt().toString()

            val tuneScore = args.singScore.tuneScore.roundToInt()
            val timingScore = args.singScore.timingScore.roundToInt()
            val averageScore = args.singScore.totalScore.roundToInt()
            val earnedXp = when (args.song.difficulty.toLowerCase()){
                "hard" -> {
                    averageScore * 3
                }
                "medium" ->{
                    averageScore * 2
                }
                else ->{
                    averageScore
                }
            }

//            tvFinalScore.text = averageScore.toString()
            tvMoreXp.text = "$earnedXp XP"

            ivClose.setOnClickListener {
                activity?.onBackPressed()
            }

            exoPlayer = SimpleExoPlayer.Builder(requireContext())
                .setTrackSelector(
                    DefaultTrackSelector(requireContext()).also {
                        it.parameters = it.buildUponParameters()
                            .setForceLowestBitrate(true)
                            .build()
                    }
                ).build()
            exoPlayer.seekTo(10)


            icPlayPause.setOnClickListener {
                if (exoPlayer.isPlaying) {
                    exoPlayer.pause()
                    icPlayPause.visibility = VISIBLE
                    icPlayPause.setImageResource(R.drawable.ic_pause)
                } else {
                    exoPlayer.play()
                    icPlayPause.visibility = VISIBLE
                    icPlayPause.setImageResource(R.drawable.ic_play)
                }
                hideViewAfterDelay(icPlayPause)
            }

            btnPublish.setOnClickListener {
                findNavController().navigate(
                    CoverPreviewFragmentDirections.toPublishCoverFragment(args.song, args.singScore, args.attepmtId,mixPath))
            }

            btnTryAgain.setOnClickListener {
                if (isFakeSinging) {
                    fakeSinging()
                } else {
                    viewModel.onCreateCoverSelected(args.song.id)
                    Analytics.logEvent(
                        Analytics.Event.TryAgainCoverEvent(
                            Analytics.Event.Param.SongId(args.song.title),
                            Analytics.Event.Param.GenreId("NA"),
                            Analytics.Event.Param.ArtistId(args.song.artists.names),
                            Analytics.Event.Param.TotalScore(args.singScore.totalScore.roundToInt()),
                            Analytics.Event.Param.TuneScore(args.singScore.tuneScore.roundToInt()),
                            Analytics.Event.Param.TimeScore(args.singScore.timingScore.roundToInt()),
                        )
                    )
                }
                if (exoPlayer.isPlaying) {
                    exoPlayer.stop()
                }
            }

            viewModel.apply {
                failure(failure, ::handleFailure)
                observe(createCoverArgs, ::startSingingInterface)
                observe(boostedSingScore, ::onCoverResult)

                args.mixPath.let {
                    if (it != null) {
                        setupVideoPlayer(it,true)
                    } else {
                        loadVideoUrl(args.attepmtId)
                        observe(coverUrl) { setupVideoPlayer(it,false) }
                    }
                }
            }
        }
    }

    private fun setupVideoPlayer(videoPath: String,file:Boolean) {
        mixPath=videoPath
        binding.apply {
            vvCoverPlayer.apply {
                if(file){
                    exoPlayer.setMediaItem(
                        MediaItem.Builder()
                            .setUri(Uri.fromFile(File(videoPath)))
                            .build()
                    )
                }else{
                    exoPlayer.setMediaItem(
                        MediaItem.Builder()
                            .setUri(videoPath)
                            .setMimeType(MimeTypes.APPLICATION_M3U8)
                            .build()
                    )
                }

                exoPlayer.prepare()
                player = exoPlayer
                /*videoSurfaceView?.setOnClickListener {
                    exoPlayer.pause()
                    showLessonDetailPage(recommendedLesson.id)
                }*/
            }
            vvCoverPlayer.setOnClickListener {
                if (exoPlayer.isPlaying) {
                    exoPlayer.pause()
                    icPlayPause.visibility = VISIBLE
                    icPlayPause.setImageResource(R.drawable.ic_pause)
                } else {
                    exoPlayer.play()
                    icPlayPause.visibility = VISIBLE
                    icPlayPause.setImageResource(R.drawable.ic_play)
                }
            }
        }
    }

    private fun hideViewAfterDelay(view: View) {
        val delay = 3000L // 3 seconds
        view.postDelayed({ view.visibility = View.GONE }, delay)
    }

    private lateinit var createCoverArgs: PrepareCreateCoverArgsUseCase.CreateCoverArgs
    private fun onCoverResult(singScore: SingScore) {
        Timber.d("onCoverResult: $singScore")
        // FIXME - Risky de-referencing?
        findNavController().navigate(CoverPreviewFragmentDirections.toCoverResultFragment(args.song, singScore))
    }

    private fun startSingingInterface(createCoverArgs: PrepareCreateCoverArgsUseCase.CreateCoverArgs) {
        Timber.d("startSingingInterface: $createCoverArgs")

        this.createCoverArgs = createCoverArgs
        Timber.d("starting singing interface for contentPaths: $createCoverArgs")
        val intentBuilder = createCoverArgs.let {
            getRecordIntentBuilder()
                .setMediaPath(it.mediaFile.absolutePath)
                .setMetadataPath(it.metadataFile.absolutePath)
                .setMixMp4Path(it.mixFile.absolutePath)
                .setSongId(args.song.id)
                .setCameraFacing(singstrViewModel.cameraFacing.value ?: Doorway.CameraFacing.FRONT)
        }
        getSingResult.launch(intentBuilder.build(context))
    }

    private val getSingResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        Timber.d("getSingResult: result")

        var skiResultCode: Int? = null
        if (Activity.RESULT_OK == it.resultCode) {
            it.data?.apply {
                skiResultCode = getIntExtra(Doorway.KEY_EXTRA_SKI_RESULT_CODE, Doorway.SKI_RESULT_CODE_UNKNOWN_ERROR)
                val isContentExpiryWarning = getBooleanExtra(Doorway.KEY_EXTRA_BOOLEAN_IS_CONTENT_EXPIRY_WARNING, false)
                Timber.d("onActivityResult: isContentExpiryWarning=$isContentExpiryWarning")

                if (Doorway.SKI_RESULT_CODE_SING_COMPLETE == skiResultCode) {
                    val songDurationMS = getIntExtra(Doorway.KEY_EXTRA_INT_SONG_DURATION_MILLISEC, -1)
                    val totalRecDurationMS = getIntExtra(Doorway.KEY_EXTRA_INT_TOTAL_RECORDING_DURATION_MILLISEC, -1)
                    val singableRecDurationMS = getIntExtra(Doorway.KEY_EXTRA_INT_SINGABLE_RECORDING_DURATION_MILLISEC, -1)

                    val tuneScore = getFloatExtra(Doorway.KEY_EXTRA_FLOAT_TUNE_SCORE, Float.NaN) * 100f
                    val timingScore = getFloatExtra(Doorway.KEY_EXTRA_FLOAT_TIMING_SCORE, Float.NaN) * 100f

                    val reviewData = getStringExtra(Doorway.KEY_EXTRA_STRING_REVIEW_DATA) ?: ""

                    // FIXME - send actual BTPE of user

                    val detailScoreFile = File(requireContext().filesDir, "btpe")
                    if (!detailScoreFile.exists()) {
                        FileWriter(detailScoreFile).use { it.write("btpe") }
                    }

                    val coverResult = SingScore(
                        songId = args.song.id,
                        songDurationMS = songDurationMS,
                        totalRecDurationMS = totalRecDurationMS,
                        singableRecDurationMS = singableRecDurationMS,
                        totalScore = (tuneScore + timingScore) / 2,
                        tuneScore = tuneScore,
                        timingScore = timingScore,
                        reviewData = reviewData,
                        mixPath = createCoverArgs.mixFile.absolutePath,
                        metaPath = detailScoreFile.absolutePath,
                        rawRecPath = File(requireContext().filesDir, "sensi-sdk/rm").absolutePath,
                    )
                    Timber.d("getSingResult: $coverResult")
                    viewModel.computeBoostedScore(coverResult)
                    return@registerForActivityResult
                }
            }
        }
        val message = "Singing Cancelled"
//        if (null != skiResultCode) message += " Error code [$skiResultCode]"
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()

    }


    private val fakeSinging: () -> Unit = {
        val tempFile = File(requireContext().filesDir, "temp")
        if (!tempFile.exists()) {
            FileWriter(tempFile).use { it.write("temp") }
        }
        onCoverResult(
            SingScore(
                songId = args.song.id,
                songDurationMS = 80,
                totalRecDurationMS = 80,
                singableRecDurationMS = 80,
                totalScore = 80f,
                tuneScore = 80f,
                timingScore = 80f,
                reviewData = "[{\"startTimeMilliSec\":6819,\"endTimeMilliSec\":8099,\"lyrics\":\"Tu \",\"score\":0.5,\"review\":1,\"componentScores\":{\"sur\":0,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":0,\"rhythm\":1,\"tfa\":0}},{\"startTimeMilliSec\":8099,\"endTimeMilliSec\":9139,\"lyrics\":\"safar \",\"score\":0.8900660872459412,\"review\":2,\"componentScores\":{\"sur\":0.7801321744918823,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":0,\"rhythm\":1,\"tfa\":0}},{\"startTimeMilliSec\":9139,\"endTimeMilliSec\":10400,\"lyrics\":\"mera \",\"score\":0.7656253576278687,\"review\":2,\"componentScores\":{\"sur\":0.5312507748603821,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":0,\"rhythm\":1,\"tfa\":0}},{\"startTimeMilliSec\":10400,\"endTimeMilliSec\":15080,\"lyrics\":\"Hai tu hi meri manzil \",\"score\":0.7851428985595703,\"review\":2,\"componentScores\":{\"sur\":0.8560001254081726,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":1,\"rhythm\":0.7142857313156128,\"tfa\":1}},{\"startTimeMilliSec\":15080,\"endTimeMilliSec\":18660,\"lyrics\":\"Tere bina guzara \",\"score\":0.6302607655525208,\"review\":2,\"componentScores\":{\"sur\":0.7605215311050415,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":0,\"rhythm\":0.5,\"tfa\":0}},{\"startTimeMilliSec\":18660,\"endTimeMilliSec\":22740,\"lyrics\":\"Ae dil hai mushkil \",\"score\":0.6397604942321777,\"review\":2,\"componentScores\":{\"sur\":0.9995253086090088,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":0.4296300709247589,\"rhythm\":0.27999573945999146,\"tfa\":0}},{\"startTimeMilliSec\":22740,\"endTimeMilliSec\":23619,\"lyrics\":\"Tu \",\"score\":1,\"review\":2,\"componentScores\":{\"sur\":1,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":0,\"rhythm\":1,\"tfa\":0}},{\"startTimeMilliSec\":23619,\"endTimeMilliSec\":24660,\"lyrics\":\"mera \",\"score\":1,\"review\":2,\"componentScores\":{\"sur\":1,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":0,\"rhythm\":1,\"tfa\":0}},{\"startTimeMilliSec\":24660,\"endTimeMilliSec\":25960,\"lyrics\":\"khuda \",\"score\":0.5,\"review\":1,\"componentScores\":{\"sur\":1,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":0,\"rhythm\":0,\"tfa\":0}},{\"startTimeMilliSec\":25960,\"endTimeMilliSec\":29220,\"lyrics\":\"Tu hi duaa mein \",\"score\":0.5782200694084167,\"review\":1,\"componentScores\":{\"sur\":0.8231068253517151,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":1,\"rhythm\":0.3333333432674408,\"tfa\":0}},{\"startTimeMilliSec\":29220,\"endTimeMilliSec\":30260,\"lyrics\":\"shaamil \",\"score\":0.9246572256088257,\"review\":2,\"componentScores\":{\"sur\":0.8493145108222961,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":0,\"rhythm\":1,\"tfa\":0.5}},{\"startTimeMilliSec\":30260,\"endTimeMilliSec\":34160,\"lyrics\":\"Tere bina guzara \",\"score\":0.6842862367630005,\"review\":2,\"componentScores\":{\"sur\":0.9285715222358704,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":1,\"rhythm\":0.4400009214878082,\"tfa\":0}},{\"startTimeMilliSec\":34160,\"endTimeMilliSec\":38540,\"lyrics\":\"Ae dil hai mushkil \",\"score\":0.625,\"review\":2,\"componentScores\":{\"sur\":1,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":1,\"rhythm\":0.25,\"tfa\":0}},{\"startTimeMilliSec\":38540,\"endTimeMilliSec\":39559,\"lyrics\":\"Mujhe \",\"score\":1,\"review\":2,\"componentScores\":{\"sur\":1,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":1,\"rhythm\":1,\"tfa\":0}},{\"startTimeMilliSec\":39559,\"endTimeMilliSec\":45579,\"lyrics\":\"Aazmati hai teri kami \",\"score\":0.7439441680908203,\"review\":2,\"componentScores\":{\"sur\":0.9340423345565796,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":0.6218903660774231,\"rhythm\":0.5538459420204163,\"tfa\":1}},{\"startTimeMilliSec\":45579,\"endTimeMilliSec\":47120,\"lyrics\":\"Meri \",\"score\":0.5153487324714661,\"review\":1,\"componentScores\":{\"sur\":0.5306974649429321,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":0,\"rhythm\":0.5,\"tfa\":0}},{\"startTimeMilliSec\":47120,\"endTimeMilliSec\":51379,\"lyrics\":\"Har kami ko hai tu \",\"score\":0.6716931462287903,\"review\":2,\"componentScores\":{\"sur\":0.9148148894309998,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":1,\"rhythm\":0.4285714328289032,\"tfa\":0.699999988079071}},{\"startTimeMilliSec\":51379,\"endTimeMilliSec\":53460,\"lyrics\":\"laazmi \",\"score\":0.5655162334442139,\"review\":1,\"componentScores\":{\"sur\":0.759600818157196,\"expression\":{\"ornament\":1,\"vibrato\":0,\"meend\":0},\"bonus\":1,\"rhythm\":0.3714316189289093,\"tfa\":0}},{\"startTimeMilliSec\":53460,\"endTimeMilliSec\":56899,\"lyrics\":\"Junoon hai mera \",\"score\":0.800000011920929,\"review\":2,\"componentScores\":{\"sur\":1,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":1,\"rhythm\":0.6000000238418579,\"tfa\":0}},{\"startTimeMilliSec\":56899,\"endTimeMilliSec\":61260,\"lyrics\":\"Banu main tere kaabil \",\"score\":0.883333683013916,\"review\":2,\"componentScores\":{\"sur\":0.9333340525627136,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":1,\"rhythm\":0.8333333134651184,\"tfa\":0.5}},{\"startTimeMilliSec\":61260,\"endTimeMilliSec\":65220,\"lyrics\":\"Tere bina guzara \",\"score\":0.7694137692451477,\"review\":2,\"componentScores\":{\"sur\":0.9832719564437866,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":1,\"rhythm\":0.5555555820465088,\"tfa\":0}},{\"startTimeMilliSec\":65220,\"endTimeMilliSec\":69760,\"lyrics\":\"Ae Dil Hai Mushkil \",\"score\":0.6764870882034302,\"review\":2,\"componentScores\":{\"sur\":0.8779565691947937,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":0,\"rhythm\":0.4750175476074219,\"tfa\":0}}]",
                mixPath = "/sdcard/Android/data/com.lucidmusic.singstr/files/mixes/mix7930625595263016582.mp4",
                metaPath = tempFile.absolutePath,
                rawRecPath = tempFile.absolutePath
            )
        )
    }
    override fun onDestroyView() {
        super.onDestroyView()
        if (this::exoPlayer.isInitialized) {
            exoPlayer.stop()
        }
    }

}