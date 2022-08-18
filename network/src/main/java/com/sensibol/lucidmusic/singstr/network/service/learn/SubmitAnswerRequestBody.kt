package com.sensibol.lucidmusic.singstr.network.service.learn

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SubmitAnswerRequestBody(
    val attempt_type: String,
    val question_id: String,
    val answer_id: String
)

