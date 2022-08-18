package com.sensibol.lucidmusic.singstr.network.framework

import com.sensibol.lucidmusic.singstr.domain.webservice.OnTransferProgress
import okhttp3.RequestBody
import okio.*

internal class ProgressSink(
    sink: Sink,
    private val requestBody: RequestBody,
    private val onTransferProgress: OnTransferProgress
) : ForwardingSink(sink) {

    private var bytesWritten = 0L

    override fun write(source: Buffer, byteCount: Long) {
        super.write(source, byteCount)
        bytesWritten += byteCount

        onTransferProgress(bytesWritten, requestBody.contentLength())
    }
}

internal class ProgressRequestBody(
    private val requestBody: RequestBody,
    private val onTransferProgress: OnTransferProgress
) : RequestBody() {

    override fun contentType() = requestBody.contentType()

    @Throws(IOException::class)
    override fun contentLength() = requestBody.contentLength()

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        ProgressSink(sink, this, onTransferProgress)
            .buffer()
            .apply {
                requestBody.writeTo(this)
                flush()
            }
    }


}
