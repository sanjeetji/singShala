package com.sensibol.lucidmusic.singstr.gui.app.sing.prepare.song

import com.sensibol.lucidmusic.singstr.domain.model.Genre

internal data class GenreView(
    val genre: Genre,
    val name: String,
    val thumbnailUrl: String
)

internal fun Genre.toGenreView(): GenreView =
    GenreView(this, name, thumbnail)