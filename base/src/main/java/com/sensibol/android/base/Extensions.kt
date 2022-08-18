package com.sensibol.android.base

import kotlinx.coroutines.*
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import kotlin.math.ln
import kotlin.math.pow

val Any.displayName: String
    get() = "${javaClass.simpleName}[${hashCode().toString(16)}]"

suspend fun File.md5(): String {
    val digest = MessageDigest.getInstance("MD5")

    withContext(Dispatchers.IO) {
        val byteArray = ByteArray(1024 * 8)

        val fis = FileInputStream(this@md5)
        var bytesRead = fis.read(byteArray)
        while (0 < bytesRead) {
            digest.update(byteArray, 0, bytesRead)
            bytesRead = fis.read(byteArray)
        }
        fis.close()
    }
    return digest.digest().joinToString("") { "%02x".format(it) }
}

val Long.pretty: String
    get() {
        if (this < 1000) return "" + this
        val exp = (ln(this.toDouble()) / ln(1000.0)).toInt()
        return String.format("%.1f %c", this / 1000.0.pow(exp.toDouble()), "kMGTPE"[exp - 1])
    }