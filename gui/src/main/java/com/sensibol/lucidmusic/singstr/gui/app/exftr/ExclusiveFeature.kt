package com.sensibol.lucidmusic.singstr.gui.app.exftr

import androidx.navigation.NavDirections

data class ExclusiveFeature(
    val title: String,
    val descriptionResId: Int,
    val ctaTitle: String,
    val bannerImageResId: Int,

    @Deprecated("To be replaced with ctaDeeplink")
    val navDirections: NavDirections,
)