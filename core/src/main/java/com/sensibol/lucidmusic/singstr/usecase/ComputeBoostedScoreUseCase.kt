package com.sensibol.lucidmusic.singstr.usecase

import javax.inject.Inject
import kotlin.math.pow

class ComputeBoostedScoreUseCase @Inject constructor() {

    operator fun invoke(score: Float): Float = 100.0f * (score / 100.0f).pow(1 / leniency)

    companion object {
        private const val leniency: Float = 1.5f
    }
}