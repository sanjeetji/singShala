package com.sensibol.lucidmusic.singstr.network.service.cover

internal class GetPublishUrlRequestParams(
    private val attemptId: String,
    private val mediaContentLength: Long,
    private val mediaHash: String,
    private val metaContentLength: Long,
    private val metaHash: String,
    private val rawRecordingContentLength: Long,
    private val rawRecordingHash: String,
) {

    // TODO - find a better way of mapping from Kotlin object to Map<String, String>
    fun toQueryMap(): Map<String, String> = mapOf(
        "attempt_id" to attemptId,
        "media_content_length" to mediaContentLength.toString(),
        "media_hash" to mediaHash,
        "meta_content_length" to metaContentLength.toString(),
        "meta_hash" to metaHash,
        "raw_recording_content_length" to rawRecordingContentLength.toString(),
        "raw_recording_hash" to rawRecordingHash,
    )

}