package com.sensibol.lucidmusic.singstr.domain.model

data class DraftsPage(
    val drafts: List<Draft>,
    val nextPageToken: String
)
