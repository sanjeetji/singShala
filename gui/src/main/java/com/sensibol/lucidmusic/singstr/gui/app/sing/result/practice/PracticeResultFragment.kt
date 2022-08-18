package com.sensibol.lucidmusic.singstr.gui.app.sing.result.practice

import android.animation.ObjectAnimator
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.karaoke.Doorway
import com.sensibol.lucidmusic.singstr.domain.model.SingMode
import com.sensibol.lucidmusic.singstr.domain.model.SingScore
import com.sensibol.lucidmusic.singstr.domain.model.SongMini
import com.sensibol.lucidmusic.singstr.domain.model.names
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentPracticeResultBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import com.sensibol.lucidmusic.singstr.gui.isFakeSinging
import com.sensibol.lucidmusic.singstr.usecase.PrepareCreateCoverArgsUseCase
import com.sensibol.lucidmusic.singstr.usecase.PrepareSongPracticeArgsUseCase
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import kotlin.math.roundToInt


@AndroidEntryPoint
internal class PracticeResultFragment : BaseFragment() {

    override val layoutResId: Int get() = R.layout.fragment_practice_result
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding get() = FragmentPracticeResultBinding::inflate
    override val binding: FragmentPracticeResultBinding get() = super.binding as FragmentPracticeResultBinding

    private val args: PracticeResultFragmentArgs by navArgs()

    private val viewModel: PracticeResultViewModel by viewModels()

    private lateinit var selectedSingMode: SingMode

    override fun onInitView() {
        Timber.d("onInitView:")

        Analytics.logEvent(
            Analytics.Event.CompletedPracticeEvent(
                Analytics.Event.Param.GenreId("NA"),
                Analytics.Event.Param.ArtistId(args.songMini.artists.names),
                Analytics.Event.Param.SongId(args.songMini.title),
                Analytics.Event.Param.TotalScore(args.singScore.totalScore.roundToInt()),
                Analytics.Event.Param.TuneScore(args.singScore.tuneScore.roundToInt()),
                Analytics.Event.Param.TimeScore(args.singScore.timingScore.roundToInt()),

                )
        )
        binding.apply {

            tvFinalScore.visibility = View.GONE
            tvEarnedXps.visibility = View.GONE
            val totalScore = args.singScore.totalScore.roundToInt()
            tvFinalScore.text = totalScore.toString()
            tvEarnedXps.text = totalScore.toString()
            val scoreAnimator = ObjectAnimator.ofInt(pbFinalScore, "progress", totalScore)
            scoreAnimator.apply {
                duration = 700
                addListener(
                    doOnEnd {
                        tvFinalScore.visibility = View.VISIBLE
                        tvEarnedXps.visibility = View.VISIBLE
                    }
                )
            }
            scoreAnimator.start( )

            btnRecord.setOnClickListener {
                selectedSingMode = SingMode.RECORD
                if (isFakeSinging) {
                    fakeSinging()
                } else {
                    pbDownloadProgress.visibility = View.VISIBLE
                    viewModel.prepareCreateCoverArgs(args.songMini.id)
                }
            }
            btnPractice.setOnClickListener {
                selectedSingMode = SingMode.PRACTICE
                if (isFakeSinging) {
                    fakeSinging()
                } else {
                    pbDownloadProgress.visibility = View.VISIBLE
                    viewModel.prepareSongPracticeArgs(args.songMini.id)
                }
            }
            ibClose.setOnClickListener {
                findNavController().popBackStack(R.id.practiceResultFrag, true)
            }
        }
        viewModel.apply {
            observe(failure, ::handleFailure)
            observe(createCoverArgs, ::startRecordInterface)
            observe(songPracticeArgs, ::startPracticeInterface)
            observe(boostedSingScore, ::onCoverResult)
            observe(transferProgress) {
                (it * 100).roundToInt().let {
                    binding.pbDownloadProgress.progress = it
                    binding.pbDownloadProgress.visibility = if (it == 100) View.INVISIBLE else View.VISIBLE
                }
            }
            submitPracticeScore(args.singScore, args.songMini)
        }
    }

    private fun startRecordInterface(createCoverArgs: PrepareCreateCoverArgsUseCase.CreateCoverArgs) {
        Timber.d("startRecordInterface: $createCoverArgs")

        this.createCoverArgs = createCoverArgs
        Timber.d("starting singing interface for contentPaths: $createCoverArgs")
        val intentBuilder = createCoverArgs.let {
            Doorway.getRecordIntentBuilder()
                .setMediaPath(it.mediaFile.absolutePath)
                .setMetadataPath(it.metadataFile.absolutePath)
                .setMixMp4Path(it.mixFile.absolutePath)
                .setCameraFacing(Doorway.CameraFacing.FRONT)
                .setSongId(args.songMini.id)
        }
        getSingResult.launch(intentBuilder.build(context))
    }


    private fun startPracticeInterface(practiceArgs: PrepareSongPracticeArgsUseCase.PracticeArgs) {
        Timber.d("startPracticeInterface: $practiceArgs")

        Timber.d("starting singing interface for contentPaths: $practiceArgs")
        val intentBuilder = practiceArgs.let {
            Doorway.getPracticeIntentBuilder()
                .setMediaPath(it.mediaFile.absolutePath)
                .setMetadataPath(it.metadataFile.absolutePath)
                .setSongId(args.songMini.id)
        }
        getSingResult.launch(intentBuilder.build(context))

    }

    private lateinit var createCoverArgs: PrepareCreateCoverArgsUseCase.CreateCoverArgs

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

                    val btpe = File(requireContext().filesDir, "btpe")
                    if (!btpe.exists()) {
                        FileWriter(btpe).use { it.write("btpe") }
                    }

                    val singScore = SingScore(
                        songId = args.songMini.id,
                        songDurationMS = songDurationMS,
                        totalRecDurationMS = totalRecDurationMS,
                        singableRecDurationMS = singableRecDurationMS,
                        totalScore = (tuneScore + timingScore) / 2,
                        tuneScore = tuneScore,
                        timingScore = timingScore,
                        reviewData = reviewData,
                        mixPath = when (selectedSingMode) {
                            SingMode.RECORD -> createCoverArgs.mixFile.absolutePath
                            else -> btpe.absolutePath
                        },
                        metaPath = btpe.absolutePath,
                        rawRecPath = File(requireContext().filesDir, "sensi-sdk/rm").absolutePath
                    )
                    Timber.d("getSingResult: $singScore")
                    viewModel.computeBoostedScore(singScore)
                    return@registerForActivityResult
                }
            }
        }
        var message = "Singing Cancelled"
//        if (null != skiResultCode) message += " Error code [$skiResultCode]"
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()

    }

    private fun onCoverResult(singScore: SingScore) {
        Timber.d("onCoverResult: $singScore")
        findNavController().navigate(
            when (selectedSingMode) {
                SingMode.PRACTICE -> PracticeResultFragmentDirections.toSelf(args.songMini, singScore)
                else -> PracticeResultFragmentDirections.toCoverResultFragment(args.songMini, singScore)
            }
        )
    }

    private val fakeSinging: () -> Unit = {
        val tempFile = File(requireContext().filesDir, "temp")
        if (!tempFile.exists()) {
            FileWriter(tempFile).use { it.write("temp") }
        }
        viewModel.computeBoostedScore(
            SingScore(
                songId = args.songMini.id,
                songDurationMS = -1,
                totalRecDurationMS = -1,
                singableRecDurationMS = -1,
                totalScore = 80f,
                tuneScore = 80f,
                timingScore = 80f,
                reviewData = "{}",
                mixPath = tempFile.absolutePath,
                metaPath = tempFile.absolutePath,
                rawRecPath = tempFile.absolutePath
            )
        )
    }

}