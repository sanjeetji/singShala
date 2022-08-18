package com.sensibol.lucidmusic.singstr.domain.utils

import java.io.File
import java.util.*

object UniqueFixture {

    fun newLongId(): Long = UUID.randomUUID().leastSignificantBits

    fun newStringId(): String = UUID.randomUUID().toString()

    fun newTimestamp(): String = System.currentTimeMillis().toString()

    fun newUrl(
        resource: String = "resource",
        resourceId: String = newStringId(),
    ): String = "http://www.singstr.com/$resource/$resourceId"

    fun newFile(
        path: String = "/data/user/0/com.lucidmusic.singstr/files",
        fileName: String = newStringId()
    ): File = File(path, fileName)
}