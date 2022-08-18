package com.sensibol.lucidmusic.singstr.domain.model

data class NodeDraft(
    val attemptId: String,
    val totalScore: Int,
    val songId: String,
    val attemptTime: String,
    val songTitle: String,
    val coverUrl: String,
    val publicMediaUrl: String,
)
