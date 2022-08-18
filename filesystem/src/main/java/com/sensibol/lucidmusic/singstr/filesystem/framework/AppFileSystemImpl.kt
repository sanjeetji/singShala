package com.sensibol.lucidmusic.singstr.filesystem.framework

import android.content.Context
import com.sensibol.android.base.BuildConfig
import com.sensibol.android.base.sizeBytes
import com.sensibol.lucidmusic.singstr.domain.AppFileSystem
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AppFileSystemImpl @Inject constructor(
    @ApplicationContext private var context: Context
) : AppFileSystem {

    override fun getMediaFile(id: String, hash: String): File = File(contentDir, "d_${id}_${hash}")

    override fun getMetadataFile(id: String, hash: String): File = File(contentDir, "m_${id}_${hash}")

    override fun getUniqueMixFile(): File = File.createTempFile("mix", ".mp4", mixDir)

    override fun getVideDownloadFile(): File = File.createTempFile("mix", ".mp4", videoDir)


    override suspend fun getCacheSizeBytes(): Long {

        return context.cacheDir.sizeBytes() + contentDir.sizeBytes() + mixDir.sizeBytes()
    }

    override suspend fun deleteCache() {
        context.cacheDir.deleteRecursively()
        contentDir.deleteRecursively()
        mixDir.deleteRecursively()
    }

    private val filesDir: File
        get() = if (BuildConfig.DEBUG) {
            context.getExternalFilesDir(null) ?: context.filesDir
        } else {
            context.filesDir
        }

    private val cacheDir: File
        get() = if (BuildConfig.DEBUG) {
            context.externalCacheDir ?: context.cacheDir
        } else {
            context.cacheDir
        }

    private val contentDir: File get() = File(cacheDir, "content").also { if (!it.exists()) it.mkdirs() }

    private val mixDir: File get() = File(filesDir, "mixes").also { if (!it.exists()) it.mkdirs() }

    private val videoDir: File get() = File(cacheDir, "video").also { if (!it.exists()) it.mkdirs() }
}