package com.sensibol.lucidmusic.singstr.network.framework

import com.sensibol.lucidmusic.singstr.domain.webservice.OnTransferProgress
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*


internal class ProgressSource(
    source: Source,
    private val responseBody: ResponseBody,
    private val onTransferProgress: OnTransferProgress
) : ForwardingSource(source) {

    private var bytesTransferred = 0L

    override fun read(sink: Buffer, byteCount: Long): Long =
        super.read(sink, byteCount).also {
            bytesTransferred += it
            onTransferProgress(bytesTransferred, responseBody.contentLength())
        }

}

internal class ProgressResponseBody(
    private val responseBody: ResponseBody,
    private val onTransferProgress: OnTransferProgress
) : ResponseBody() {

    override fun contentType(): MediaType? = responseBody.contentType()

    override fun contentLength(): Long = responseBody.contentLength()

    override fun source(): BufferedSource = ProgressSource(responseBody.source(), responseBody, onTransferProgress).buffer()

}

