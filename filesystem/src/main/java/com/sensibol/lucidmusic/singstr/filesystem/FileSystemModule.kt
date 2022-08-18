package com.sensibol.lucidmusic.singstr.filesystem

import android.content.Context
import com.sensibol.lucidmusic.singstr.domain.AppFileSystem
import com.sensibol.lucidmusic.singstr.filesystem.framework.AppFileSystemImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object FileSystemModule {

    @Provides
    @Singleton
    internal fun provideAppFileSystem(@ApplicationContext context: Context): AppFileSystem {
        return AppFileSystemImpl(context)
    }

}