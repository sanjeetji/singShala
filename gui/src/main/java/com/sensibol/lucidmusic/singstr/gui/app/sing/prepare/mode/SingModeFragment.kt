package com.sensibol.lucidmusic.singstr.gui.app.sing.prepare.mode

import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.app.Activity.RESULT_OK
import android.content.res.Resources
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import com.sensibol.android.base.gui.AppToast
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.karaoke.*
import com.sensibol.karaoke.Doorway.*
import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.gui.*
import com.sensibol.lucidmusic.singstr.gui.app.SingstrViewModel
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.app.sing.prepare.PrepareSingHostFragment
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentSingModeBinding
import com.sensibol.lucidmusic.singstr.usecase.PrepareCreateCoverArgsUseCase
import com.sensibol.lucidmusic.singstr.usecase.PrepareSongPracticeArgsUseCase
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import kotlin.math.roundToInt

@AndroidEntryPoint
internal class SingModeFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_sing_mode
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentSingModeBinding::inflate
    override val binding: FragmentSingModeBinding get() = super.binding as FragmentSingModeBinding

    private val args: SingModeFragmentArgs by navArgs()

    private val viewModel: SingModeViewModel by viewModels()

    private val singstrViewModel: SingstrViewModel by activityViewModels()

    private var mediaPlayer: MediaPlayer? = null

    private lateinit var seekBarListener: SeekBarListener

    private lateinit var selectedSingMode: SingMode

    private var isUserPro = false

    private var mInterstitialAd: AdManagerInterstitialAd? = null

    override fun onInitViewModel() {
        viewModel.apply {
            loadSongMini(args.songId)
            loadSongPreviewUrl(args.songId)
            observe(songMini, ::onSongMiniLoaded)

            failure(failure, ::handleFailure)
            observe(previewUrl, ::onPreviewUrl)
            observe(createCoverArgs, ::startRecordInterface)
            observe(songPracticeArgs, ::startPracticeInterface)
            observe(transferProgress) {
                (it * 100).roundToInt().let {
                    binding.pbDownloadProgress.progress = it
                    binding.pbDownloadProgress.visibility = if (it == 100) INVISIBLE else VISIBLE
                }
            }
        }
        singstrViewModel.apply {
            failure(failure, ::handleFailure)
            observe(userSubscription, ::showUserSubscription)
            getUserSubscription()
        }
    }

    private fun onSongMiniLoaded(songMini: SongMini) {
        viewModel.apply {
            observe(boostedSingScore) { singScore ->
                (parentFragment?.parentFragment as PrepareSingHostFragment).onCoverComplete(selectedSingMode.argName, singScore, songMini)
            }
        }
        binding.apply {
            selectedSingMode = if (!songMini.isPracticable) {
                SingMode.RECORD
            } else {
                SingMode.values().find { it.argName.equals(args.singMode, ignoreCase = true) } ?: SingMode.RECORD
            }
            updateSelectedSingMode()

            tvTitle.text = songMini.title
            tvArtists.text = songMini.artists.names
            ivThumbnil.loadUrl(songMini.thumbnailUrl)

            tvChangeCamera.setOnClickListener {
                if (singstrViewModel.cameraFacing.value == CameraFacing.FRONT) {
                    (parentFragment?.parentFragment as PrepareSingHostFragment).switchCamera(CameraFacing.BACK)
                    tvChangeCamera.text = getText(R.string.front_camera)
                    Analytics.logEvent(
                        Analytics.Event.SwitchCameraEvent(
                            Analytics.Event.Param.CameraFacing("Front"),
                        )
                    )
                } else {
                    (parentFragment?.parentFragment as PrepareSingHostFragment).switchCamera(CameraFacing.FRONT)
                    tvChangeCamera.text = getText(R.string.rear_camera)
                    Analytics.logEvent(
                        Analytics.Event.SwitchCameraEvent(
                            Analytics.Event.Param.CameraFacing("Rear"),
                        )
                    )
                }
            }

            btnPractice.setOnClickListener {
                if (songMini.isPracticable) {
                    when (selectedSingMode) {
                        SingMode.RECORD, SingMode.DAILY_CHALLENGE -> {
                            selectedSingMode = SingMode.PRACTICE
                            updateSelectedSingMode()
                        }
                        SingMode.PRACTICE -> {
                            startBeatingAnimation(btnPractice, btnRecord, tvRecordLabel)
                            if (isUserPro) {
                                startPracticeMode(songMini)
                            } else {
                                loadInterstitialAd(songMini)
                            }
                        }
                    }
                } else {
                    AppToast.show(it.context, "Practice Mode Will Be Made Available Soon!")
                }
            }

            btnRecord.setOnClickListener {
                when (selectedSingMode) {
                    SingMode.RECORD, SingMode.DAILY_CHALLENGE -> {
                        startBeatingAnimation(btnRecord, btnPractice, tvPracticeLabel)

                        if (isFakeSinging) {
                            fakeSinging()
                        } else {
                            pbDownloadProgress.visibility = VISIBLE
                            viewModel.prepareCreateCoverArgs(songMini.id)
                        }
                        Analytics.logEvent(
                            Analytics.Event.CoverAttemptEvent(
                                Analytics.Event.Param.SongId(songMini.title),
                                Analytics.Event.Param.GenreId("NA"),
                                Analytics.Event.Param.ArtistId(songMini.artists.names)
                            )
                        )
                    }
                    SingMode.PRACTICE -> {
                        selectedSingMode = SingMode.RECORD
                        updateSelectedSingMode()
                    }
                }
            }
        }
    }

    override fun onInitView() {
        binding.apply {
            root.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

            seekBarListener = SeekBarListener()
            seekBar.setOnSeekBarChangeListener(seekBarListener)

            pbDownloadProgress.visibility = GONE
            pbDownloadProgress.max = 100
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(requireContext()) { initializationStatus ->
            Timber.d("CHECK this Ad AdMob Sdk Initialize $initializationStatus")
        }
    }

    private fun showUserSubscription(proSubscription: ProSubscription) {
        isUserPro = proSubscription.subscribed
    }

    private fun loadInterstitialAd(songMini: SongMini) {
        val adRequest = AdManagerAdRequest.Builder().build()

        AdManagerInterstitialAd.load(
            requireContext(),
            BuildConfig.APP_INTERSTITIAL_AD_KEY,
            adRequest,
            object : AdManagerInterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Timber.d(adError.message)
                    mInterstitialAd = null
                    startPracticeMode(songMini)
                    val error = "domain: ${adError.domain}, code: ${adError.code}, " +
                            "message: ${adError.message}"
                    Timber.d("Ad onAdFailedToLoad() with error $error")
                }

                override fun onAdLoaded(interstitialAd: AdManagerInterstitialAd) {
                    mInterstitialAd = interstitialAd
                    Timber.d("CHECK this Ad was loaded.")
                    showInterstitialAd(songMini)
                }
            }
        )
    }

    private fun showInterstitialAd(songMini: SongMini) {
        // Show the ad if it's ready. Otherwise log and start practice mode.
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    mInterstitialAd = null
                    Timber.d("Ad was dismissed.")
                    startPracticeMode(songMini)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                    mInterstitialAd = null
                    Timber.d("Ad failed to show.")
                }

                override fun onAdShowedFullScreenContent() {
                    Timber.d("Ad showed fullscreen content.")
                }
            }

            mInterstitialAd?.show(requireActivity())
        } else {
            Timber.d("Ad did not load")
            startPracticeMode(songMini)
        }
    }

    private fun startPracticeMode(songMini: SongMini) {
        if (isFakeSinging) {
            fakeSinging()
        } else {
            binding.pbDownloadProgress.visibility = VISIBLE
            viewModel.prepareSongPracticeArgs(songMini.id)
        }
        Analytics.logEvent(
            Analytics.Event.PracticeSessionEvent(
                Analytics.Event.Param.SongId(songMini.title),
                Analytics.Event.Param.GenreId("NA"),
                Analytics.Event.Param.ArtistId(songMini.artists.names)
            )
        )
    }

    private fun updateSelectedSingMode() {
        binding.apply {
            val constraintSet = ConstraintSet()
            constraintSet.clone(clRootContainer)

            val margin = (32 * Resources.getSystem().displayMetrics.density).toInt()

            when (selectedSingMode) {
                SingMode.RECORD, SingMode.DAILY_CHALLENGE -> {
                    btnRecord.isSelected = true
                    btnPractice.isSelected = false
                    constraintSet.connect(btnRecord.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, margin)
                    constraintSet.clear(btnPractice.id, ConstraintSet.START)
                }
                SingMode.PRACTICE -> {
                    btnRecord.isSelected = false
                    btnPractice.isSelected = true
                    constraintSet.clear(btnRecord.id, ConstraintSet.END)
                    constraintSet.connect(btnPractice.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, margin)
                }
            }

            constraintSet.applyTo(clRootContainer)
        }
    }

    private fun onPreviewUrl(previewUrl: String) {

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            // TODO - play preview URL here
            setDataSource(previewUrl)
            isLooping = true
            setOnPreparedListener { mediaPlayer ->
                // safe to access binding here. Note - MP is destroyed ASA view is destroyed
                binding.apply {
                    ivPlayPause.visibility = VISIBLE
                    ivThumbnil.setOnClickListener {
                        if (mediaPlayer.isPlaying) {
                            pausePreviewPlayback()
                        } else {
                            startPreviewPlayback()
                        }
                    }
                }
            }
            prepareAsync()
            Timber.d("mediaPlayer[$this] created")
        }
    }

    private fun startPreviewPlayback() {
        Timber.d("startPreviewPlayback: ")
        mediaPlayer?.let {
            it.start()
            binding.apply {
                ivPlayPause.setImageResource(R.drawable.ic_play)
                equalizerView.animateBars()
                equalizerView.visibility = VISIBLE
                seekBar.visibility = VISIBLE
                seekBar.max = it.duration
                seekBar.post(seekBarUpdaterAction)
            }
        }
    }

    private fun pausePreviewPlayback() {
        Timber.d("pausePreviewPlayback: ")
        mediaPlayer?.let {
            binding.apply {
                seekBar.removeCallbacks(seekBarUpdaterAction)
                it.pause()
                equalizerView.stopBars()
                equalizerView.visibility = GONE
                ivPlayPause.setImageResource(R.drawable.ic_pause)
                seekBar.removeCallbacks(seekBarUpdaterAction)
            }
        }
    }

    private lateinit var practiceArgs: PrepareSongPracticeArgsUseCase.PracticeArgs

    private fun startPracticeInterface(practiceArgs: PrepareSongPracticeArgsUseCase.PracticeArgs) {
        Timber.d("startPracticeInterface: $practiceArgs")

        this.practiceArgs = practiceArgs
        Timber.d("starting singing interface for contentPaths: $practiceArgs")
        val intentBuilder = practiceArgs.let {
            getPracticeIntentBuilder()
                .setMediaPath(it.mediaFile.absolutePath)
                .setMetadataPath(it.metadataFile.absolutePath)
                .setSongId(args.songId)
                .setCameraFacing(singstrViewModel.cameraFacing.value ?: CameraFacing.FRONT)
        }
        getSingResult.launch(intentBuilder.build(context))
        pausePreviewPlayback()
        enableModeButtons()
    }

    private lateinit var createCoverArgs: PrepareCreateCoverArgsUseCase.CreateCoverArgs

    private fun startRecordInterface(createCoverArgs: PrepareCreateCoverArgsUseCase.CreateCoverArgs) {
        Timber.d("startRecordInterface: $createCoverArgs")

        this.createCoverArgs = createCoverArgs
        Timber.d("starting singing interface for contentPaths: $createCoverArgs")
        val intentBuilder = createCoverArgs.let {
            getRecordIntentBuilder()
                .setMediaPath(it.mediaFile.absolutePath)
                .setMetadataPath(it.metadataFile.absolutePath)
                .setMixMp4Path(it.mixFile.absolutePath)
                .setSongId(args.songId)
                .setCameraFacing(singstrViewModel.cameraFacing.value ?: CameraFacing.FRONT)
        }
        getSingResult.launch(intentBuilder.build(context))
        pausePreviewPlayback()
        enableModeButtons()
    }

    private val getSingResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
        Timber.d("getSingResult: result")

        var skiResultCode: Int? = null
        if (RESULT_OK == it.resultCode) {
            it.data?.apply {
                skiResultCode = getIntExtra(KEY_EXTRA_SKI_RESULT_CODE, SKI_RESULT_CODE_UNKNOWN_ERROR)
                val isContentExpiryWarning = getBooleanExtra(KEY_EXTRA_BOOLEAN_IS_CONTENT_EXPIRY_WARNING, false)
                Timber.d("onActivityResult: isContentExpiryWarning=$isContentExpiryWarning")

                if (SKI_RESULT_CODE_SING_COMPLETE == skiResultCode) {
                    val songDurationMS = getIntExtra(KEY_EXTRA_INT_SONG_DURATION_MILLISEC, -1)
                    val totalRecDurationMS = getIntExtra(KEY_EXTRA_INT_TOTAL_RECORDING_DURATION_MILLISEC, -1)
                    val singableRecDurationMS = getIntExtra(KEY_EXTRA_INT_SINGABLE_RECORDING_DURATION_MILLISEC, -1)

                    val tuneScore = getFloatExtra(KEY_EXTRA_FLOAT_TUNE_SCORE, Float.NaN) * 100f
                    val timingScore = getFloatExtra(KEY_EXTRA_FLOAT_TIMING_SCORE, Float.NaN) * 100f

                    val reviewData = getStringExtra(KEY_EXTRA_STRING_REVIEW_DATA) ?: ""
                    Timber.d("reviewData: %s", reviewData)

                    val btpe = File(requireContext().filesDir, "btpe")
                    if (!btpe.exists()) {
                        FileWriter(btpe).use { it.write("btpe") }
                    }

                    val singScore = SingScore(
                        songId = args.songId,
                        songDurationMS = songDurationMS,
                        totalRecDurationMS = totalRecDurationMS,
                        singableRecDurationMS = singableRecDurationMS,
                        totalScore = (tuneScore + timingScore) / 2,
                        tuneScore = tuneScore,
                        timingScore = timingScore,
                        reviewData = reviewData,
                        mixPath = when (selectedSingMode) {
                            SingMode.RECORD, SingMode.DAILY_CHALLENGE -> createCoverArgs.mixFile.absolutePath
                            SingMode.PRACTICE -> btpe.absolutePath
                        },
                        metaPath = btpe.absolutePath,
                        rawRecPath = File(requireContext().filesDir, "sensi-sdk/rm").absolutePath
                    )
                    Timber.d("getSingResult: $singScore")
                    viewModel.computeBoostedScore(singScore)
                    return@registerForActivityResult
                }
                else
                    Timber.d("onActivityResult: skiResultCode=$skiResultCode")
            }
        }
        var message = "Something went wrong! please try again"
//        if (null != skiResultCode) message += " Error code [$skiResultCode]"
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()

    }

    override fun onDestroyView() {
        mediaPlayer?.release()
        Timber.d("mediaPlayer[$mediaPlayer] released")
        mediaPlayer = null
        super.onDestroyView()
    }

    private val fakeSinging: () -> Unit = {
        val tempFile = File(requireContext().filesDir, "temp")
        if (!tempFile.exists()) {
            FileWriter(tempFile).use { it.write("temp") }
        }
        viewModel.computeBoostedScore(
            SingScore(
                songId = args.songId,
                songDurationMS = 80,
                totalRecDurationMS = 80,
                singableRecDurationMS = 80,
                totalScore = 70f,
                tuneScore = 60f,
                timingScore = 80f,
                reviewData = "[{\"startTimeMilliSec\":6819,\"endTimeMilliSec\":8099,\"lyrics\":\"Tu \",\"score\":0.5,\"review\":1,\"componentScores\":{\"sur\":0,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":0,\"rhythm\":1,\"tfa\":0}},{\"startTimeMilliSec\":8099,\"endTimeMilliSec\":9139,\"lyrics\":\"safar \",\"score\":0.8900660872459412,\"review\":2,\"componentScores\":{\"sur\":0.7801321744918823,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":0,\"rhythm\":1,\"tfa\":0}},{\"startTimeMilliSec\":9139,\"endTimeMilliSec\":10400,\"lyrics\":\"mera \",\"score\":0.7656253576278687,\"review\":2,\"componentScores\":{\"sur\":0.5312507748603821,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":0,\"rhythm\":1,\"tfa\":0}},{\"startTimeMilliSec\":10400,\"endTimeMilliSec\":15080,\"lyrics\":\"Hai tu hi meri manzil \",\"score\":0.7851428985595703,\"review\":2,\"componentScores\":{\"sur\":0.8560001254081726,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":1,\"rhythm\":0.7142857313156128,\"tfa\":1}},{\"startTimeMilliSec\":15080,\"endTimeMilliSec\":18660,\"lyrics\":\"Tere bina guzara \",\"score\":0.6302607655525208,\"review\":2,\"componentScores\":{\"sur\":0.7605215311050415,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":0,\"rhythm\":0.5,\"tfa\":0}},{\"startTimeMilliSec\":18660,\"endTimeMilliSec\":22740,\"lyrics\":\"Ae dil hai mushkil \",\"score\":0.6397604942321777,\"review\":2,\"componentScores\":{\"sur\":0.9995253086090088,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":0.4296300709247589,\"rhythm\":0.27999573945999146,\"tfa\":0}},{\"startTimeMilliSec\":22740,\"endTimeMilliSec\":23619,\"lyrics\":\"Tu \",\"score\":1,\"review\":2,\"componentScores\":{\"sur\":1,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":0,\"rhythm\":1,\"tfa\":0}},{\"startTimeMilliSec\":23619,\"endTimeMilliSec\":24660,\"lyrics\":\"mera \",\"score\":1,\"review\":2,\"componentScores\":{\"sur\":1,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":0,\"rhythm\":1,\"tfa\":0}},{\"startTimeMilliSec\":24660,\"endTimeMilliSec\":25960,\"lyrics\":\"khuda \",\"score\":0.5,\"review\":1,\"componentScores\":{\"sur\":1,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":0,\"rhythm\":0,\"tfa\":0}},{\"startTimeMilliSec\":25960,\"endTimeMilliSec\":29220,\"lyrics\":\"Tu hi duaa mein \",\"score\":0.5782200694084167,\"review\":1,\"componentScores\":{\"sur\":0.8231068253517151,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":1,\"rhythm\":0.3333333432674408,\"tfa\":0}},{\"startTimeMilliSec\":29220,\"endTimeMilliSec\":30260,\"lyrics\":\"shaamil \",\"score\":0.9246572256088257,\"review\":2,\"componentScores\":{\"sur\":0.8493145108222961,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":0,\"rhythm\":1,\"tfa\":0.5}},{\"startTimeMilliSec\":30260,\"endTimeMilliSec\":34160,\"lyrics\":\"Tere bina guzara \",\"score\":0.6842862367630005,\"review\":2,\"componentScores\":{\"sur\":0.9285715222358704,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":1,\"rhythm\":0.4400009214878082,\"tfa\":0}},{\"startTimeMilliSec\":34160,\"endTimeMilliSec\":38540,\"lyrics\":\"Ae dil hai mushkil \",\"score\":0.625,\"review\":2,\"componentScores\":{\"sur\":1,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":1,\"rhythm\":0.25,\"tfa\":0}},{\"startTimeMilliSec\":38540,\"endTimeMilliSec\":39559,\"lyrics\":\"Mujhe \",\"score\":1,\"review\":2,\"componentScores\":{\"sur\":1,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":1,\"rhythm\":1,\"tfa\":0}},{\"startTimeMilliSec\":39559,\"endTimeMilliSec\":45579,\"lyrics\":\"Aazmati hai teri kami \",\"score\":0.7439441680908203,\"review\":2,\"componentScores\":{\"sur\":0.9340423345565796,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":0.6218903660774231,\"rhythm\":0.5538459420204163,\"tfa\":1}},{\"startTimeMilliSec\":45579,\"endTimeMilliSec\":47120,\"lyrics\":\"Meri \",\"score\":0.5153487324714661,\"review\":1,\"componentScores\":{\"sur\":0.5306974649429321,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":0,\"rhythm\":0.5,\"tfa\":0}},{\"startTimeMilliSec\":47120,\"endTimeMilliSec\":51379,\"lyrics\":\"Har kami ko hai tu \",\"score\":0.6716931462287903,\"review\":2,\"componentScores\":{\"sur\":0.9148148894309998,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":1,\"rhythm\":0.4285714328289032,\"tfa\":0.699999988079071}},{\"startTimeMilliSec\":51379,\"endTimeMilliSec\":53460,\"lyrics\":\"laazmi \",\"score\":0.5655162334442139,\"review\":1,\"componentScores\":{\"sur\":0.759600818157196,\"expression\":{\"ornament\":1,\"vibrato\":0,\"meend\":0},\"bonus\":1,\"rhythm\":0.3714316189289093,\"tfa\":0}},{\"startTimeMilliSec\":53460,\"endTimeMilliSec\":56899,\"lyrics\":\"Junoon hai mera \",\"score\":0.800000011920929,\"review\":2,\"componentScores\":{\"sur\":1,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":1,\"rhythm\":0.6000000238418579,\"tfa\":0}},{\"startTimeMilliSec\":56899,\"endTimeMilliSec\":61260,\"lyrics\":\"Banu main tere kaabil \",\"score\":0.883333683013916,\"review\":2,\"componentScores\":{\"sur\":0.9333340525627136,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":1,\"rhythm\":0.8333333134651184,\"tfa\":0.5}},{\"startTimeMilliSec\":61260,\"endTimeMilliSec\":65220,\"lyrics\":\"Tere bina guzara \",\"score\":0.7694137692451477,\"review\":2,\"componentScores\":{\"sur\":0.9832719564437866,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":1,\"rhythm\":0.5555555820465088,\"tfa\":0}},{\"startTimeMilliSec\":65220,\"endTimeMilliSec\":69760,\"lyrics\":\"Ae Dil Hai Mushkil \",\"score\":0.6764870882034302,\"review\":2,\"componentScores\":{\"sur\":0.8779565691947937,\"expression\":{\"ornament\":0,\"vibrato\":0,\"meend\":0},\"bonus\":0,\"rhythm\":0.4750175476074219,\"tfa\":0}}]",
                mixPath = "/sdcard/Android/data/com.lucidmusic.singstr/files/mixes/mix779765116050285213.mp4",
                metaPath = tempFile.absolutePath,
                rawRecPath = tempFile.absolutePath
            )
        )
    }

    private val seekBarUpdaterAction: Runnable = object : Runnable {
        override fun run() {
            if (null != mediaPlayer) {
                binding.seekBar.progress = mediaPlayer?.currentPosition ?: 0
                binding.seekBar.postDelayed(this, 50)
            }
        }
    }

    private fun startBeatingAnimation(selectedButton: View, otherButton: View, otherLabel: View) {
        selectedButton.apply {
            isClickable = false
        }
        otherButton.apply {
            animate().alpha(0.3f)
            isClickable = false
        }
        otherLabel.animate().alpha(0.3f)
        modeButtonAnimator = ObjectAnimator.ofPropertyValuesHolder(
            selectedButton,
            PropertyValuesHolder.ofFloat("scaleX", 1.1f),
            PropertyValuesHolder.ofFloat("scaleY", 1.1f)
        ).apply {
            duration = 1_000
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            start()
        }
    }

    private var modeButtonAnimator: ObjectAnimator? = null

    private fun enableModeButtons() {
        modeButtonAnimator?.cancel()
        binding.apply {
            btnRecord.apply {
                alpha = 1.0f
                isClickable = true
            }
            btnPractice.apply {
                alpha = 1.0f
                isClickable = true
            }
        }
    }

    inner class SeekBarListener : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            if (fromUser) mediaPlayer?.seekTo(progress)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            binding.equalizerView.stopBars()
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            binding.equalizerView.animateBars()
        }
    }
}