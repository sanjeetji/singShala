package com.sensibol.lucidmusic.singstr.domain.utils

import kotlin.random.Random

object RandomFixture {
    private val random: Random = Random(System.currentTimeMillis())

    fun randomFloat(): Float = random.nextFloat()

    fun randomInt(from: Int = Int.MIN_VALUE, until: Int = Int.MAX_VALUE): Int = random.nextInt(from, until)
}