package com.sensibol.lucidmusic.singstr.domain

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class FileSystemModuleFixture {

    @Binds
    @Singleton
    internal abstract fun provideFakeFileSystem(appFileSystem: FakeFileSystem): AppFileSystem
}

internal class FakeFileSystem @Inject constructor(
    @ApplicationContext private var context: Context
) : AppFileSystem {

    override fun getMediaFile(id: String, hash: String): File {
        TODO("Not yet implemented")
    }

    override fun getMetadataFile(id: String, hash: String): File {
        TODO("Not yet implemented")
    }

    override fun getUniqueMixFile(): File = File.createTempFile("mix", ".mp4", mixDir)
    override fun getVideDownloadFile(): File {
        TODO("Not yet implemented")
    }

    override suspend fun getCacheSizeBytes(): Long {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCache() {
        TODO("Not yet implemented")
    }

    private val filesDir: File
        get() = context.filesDir

    private val contentDir: File get() = File(filesDir, "content").also { if (!it.exists()) it.mkdirs() }

    private val mixDir: File get() = File(filesDir, "mixes").also { if (!it.exists()) it.mkdirs() }

}