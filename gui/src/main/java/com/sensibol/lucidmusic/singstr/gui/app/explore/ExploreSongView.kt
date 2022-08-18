package com.sensibol.lucidmusic.singstr.gui.app.explore

import com.sensibol.lucidmusic.singstr.domain.model.Cover
import com.sensibol.lucidmusic.singstr.domain.model.SongMini

data class ExploreSongView(
    val songMini: SongMini,
    val covers: List<Cover>,
)