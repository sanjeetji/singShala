    package com.sensibol.lucidmusic.singstr.gui.app.player

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.navArgs
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sensibol.android.base.gui.activity.BaseActivity
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.SingstrViewModel
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.databinding.ActivityVideoPlayerBinding
import com.sensibol.lucidmusic.singstr.gui.getTimeStringFromMilliSecond
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import timber.log.Timber
import kotlin.math.roundToInt

class VideoPlayerActivity : BaseActivity() {
    override val layoutResId: Int get() = R.layout.activity_video_player
    override val bindingInflater: (LayoutInflater) -> ViewBinding get() = ActivityVideoPlayerBinding::inflate
    override val binding: ActivityVideoPlayerBinding get() = super.binding as ActivityVideoPlayerBinding

    private lateinit var seekBarListener: SeekBarListener
    private val args: VideoPlayerActivityArgs by navArgs()
    lateinit var videoView:VideoView;

    var isProLessonProgressCheck = true
    var isProLesson = false
    var isUserPro = false
    var viewTimeinSeconds:Long=0
    var viewTime:Int = 0
    var totalDuration:Int = 0
    var percent:Int=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = this.let { ContextCompat.getColor(it, R.color.bg_page) }
        binding.apply {
            videoView = vvLesson
            val view: ActivityVideoPlayerBinding = this
            txvName.text = args.lesson?.title
            args.lesson?.let { ivLessonThumbnail.loadUrl(it.thumbnailUrl) }
            seekBarListener = SeekBarListener()
            seekBar.setOnSeekBarChangeListener(seekBarListener)
            vvLesson.setVideoPath(args.lesson?.videoUrl)
            vvLesson.setOnErrorListener { _, _, _ -> true }
            isProLesson = args.lesson?.subscriptionType == "Paid"
            ivPlayPause.setOnClickListener {
                onPlayPauseClick(view)
            }
            isUserPro=args.isUserPro
            Timber.d("Pro User :$isUserPro Pro Lesson: $isProLesson")
            optView.setOnClickListener {
                onPlayPauseClick(view)
            }

            vvLesson.setOnClickListener {
                onPlayPauseClick(view)
            }

            ivCollapse.setOnClickListener {
                finish()
            }

            ivBack.setOnClickListener {
                finish()
            }
            vvLesson.setOnCompletionListener{
                finish()
            }

            ivThreeDots.setOnClickListener {
                args.lesson?.let { it1 -> showBottomSheetDialog(it1) }
            }
        }
    }

    private fun onPlayPauseClick(activityVideoPlayerBinding: ActivityVideoPlayerBinding) {
        activityVideoPlayerBinding.seekBar.removeCallbacks(seekBarUpdaterAction)
        activityVideoPlayerBinding.seekBar.max = activityVideoPlayerBinding.vvLesson.duration
        activityVideoPlayerBinding.seekBar.post(seekBarUpdaterAction)
        if (activityVideoPlayerBinding.vvLesson.isPlaying) {
            if (!isUserPro && isProLesson && activityVideoPlayerBinding.vvLesson.currentPosition>5000 ){
                activityVideoPlayerBinding.vvLesson.pause()
                finish()
            }
            else{
                activityVideoPlayerBinding.vvLesson.pause()
                activityVideoPlayerBinding.optView.visibility = View.VISIBLE
                activityVideoPlayerBinding.ivPlayPause.setImageResource(R.drawable.ic_pause)
                hideAfterDelay(activityVideoPlayerBinding.optView, 6411L)
            }


        } else {
            if (!isUserPro && isProLesson && activityVideoPlayerBinding.vvLesson.currentPosition>5000 ){
                activityVideoPlayerBinding.vvLesson.pause()
                finish()
            }else{
                activityVideoPlayerBinding.ivLessonThumbnail.visibility = View.GONE
                activityVideoPlayerBinding.vvLesson.start()
                activityVideoPlayerBinding.optView.visibility = View.VISIBLE
                activityVideoPlayerBinding.ivPlayPause.setImageResource(R.drawable.ic_play)
                hideAfterDelay(activityVideoPlayerBinding.optView, 3000L)
            }



        }
    }

    private fun hideAfterDelay(view: View, delay: Long) {
        view.postDelayed({ view.visibility = View.GONE }, delay)
    }

    private val seekBarUpdaterAction: Runnable = object : Runnable {
        override fun run() {
            viewTimeinSeconds= (binding.vvLesson.currentPosition/1000).toLong()
            viewTime=binding.vvLesson.currentPosition
            totalDuration = binding.vvLesson.duration
            percent = ((viewTime.toDouble() / totalDuration) * 100).roundToInt()
            binding.seekBar.progress = binding.vvLesson.currentPosition
            binding.tvDuration.text = getTimeStringFromMilliSecond(binding.vvLesson.currentPosition.toLong())
            binding.seekBar.max = binding.vvLesson.duration
            binding.seekBar.postDelayed(this, 50)
        }
    }

    inner class SeekBarListener : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            if (!isUserPro && isProLesson&& binding.vvLesson.currentPosition>5000 ){
                binding.vvLesson.pause()
                finish()
            }else
            {
                if (fromUser) binding.vvLesson.seekTo(progress)
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
        }
    }

    override fun onPause() {
        super.onPause()
        binding.seekBar.removeCallbacks(seekBarUpdaterAction)
    }

    private fun showBottomSheetDialog(lesson: LessonMini) {
        with(BottomSheetDialog(binding.root.context, R.style.AppBottomSheetDialogTheme)) {
            setContentView(R.layout.dialog_report_captions_quality)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            findViewById<LinearLayoutCompat>(R.id.llcReport)?.setOnClickListener {
                openDialogForVideo(lesson)
                dismissWithAnimation = true
                dismiss()
            }
            findViewById<LinearLayoutCompat>(R.id.llcCaption)?.setOnClickListener {
                openBottomSheetForCaption()
                dismissWithAnimation = true
                dismiss()
            }
            findViewById<LinearLayoutCompat>(R.id.llcQuality)?.setOnClickListener {
                openBottomSheetForQuality()
                dismissWithAnimation = true
                dismiss()
            }
            behavior.peekHeight = 641
            show()
        }
    }

    private fun openDialogForVideo(lesson: LessonMini) {
        with(Dialog(binding.root.context)) {
            setContentView(R.layout.dialog_report_video)
            setCanceledOnTouchOutside(false)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            findViewById<TextView>(R.id.btnReport).setOnClickListener {
                this.dismiss()
            }
            show()
        }
    }

    private fun openBottomSheetForCaption() {
        with(BottomSheetDialog(binding.root.context, R.style.AppBottomSheetDialogTheme)) {
            setContentView(R.layout.bottomsheet_captions)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            behavior.peekHeight = 500
            show()
        }
    }

    private fun openBottomSheetForQuality() {
        with(BottomSheetDialog(binding.root.context, R.style.AppBottomSheetDialogTheme)) {
            setContentView(R.layout.bottomsheet_quality)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            behavior.peekHeight = 700
            findViewById<RadioButton>(R.id.rb1080p)?.setOnClickListener {
//                videoView.holder.setFormat(PixelFormat.YCbCr_420_SP)
                dismiss()
            }
            show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Analytics.logEvent(
            Analytics.Event.LessonCompleteEvent(
                Analytics.Event.Param.LessonId(args.lesson!!.title),
                Analytics.Event.Param.LessonOwner(args.lesson!!.teacher.name),
                Analytics.Event.Param.LessonCategory(args.lesson!!.type),
                Analytics.Event.Param.LessonCollection("NA"),
                Analytics.Event.Param.LessonStatus(args.lesson!!.subscriptionType),
                Analytics.Event.Param.ViewTime(viewTimeinSeconds.toInt()),
                Analytics.Event.Param.ViewPercent(percent),
            )
        )
    }
    companion object {
        const val resultPro = "ShowProDialogue"
    }

}