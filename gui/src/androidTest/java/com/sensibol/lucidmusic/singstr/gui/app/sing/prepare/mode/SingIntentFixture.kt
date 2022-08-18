package com.sensibol.lucidmusic.singstr.gui.app.sing.prepare.mode

import android.content.Intent
import com.sensibol.karaoke.Doorway.*

object SingIntentFixture {

    fun getSingSuccessResultIntent(
        songDurationMS: Int = 90_000,
        totalRecDurationMS: Int = 90_000,
        singableRecDurationMS: Int = 60_000,
        tuneScore: Float = 80f,
        timingScore: Float = 60F,
    ): Intent =
        Intent()
            .putExtra(KEY_EXTRA_SKI_RESULT_CODE, SKI_RESULT_CODE_SING_COMPLETE)
            .putExtra(KEY_EXTRA_INT_SONG_DURATION_MILLISEC, songDurationMS)
            .putExtra(KEY_EXTRA_INT_TOTAL_RECORDING_DURATION_MILLISEC, totalRecDurationMS)
            .putExtra(KEY_EXTRA_INT_SINGABLE_RECORDING_DURATION_MILLISEC, singableRecDurationMS)
            .putExtra(KEY_EXTRA_FLOAT_TUNE_SCORE, tuneScore)
            .putExtra(KEY_EXTRA_FLOAT_TIMING_SCORE, timingScore)
}