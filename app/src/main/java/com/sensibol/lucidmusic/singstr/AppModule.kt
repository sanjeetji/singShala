package com.sensibol.lucidmusic.singstr

import com.sensibol.lucidmusic.singstr.gui.AppBuildName
import com.sensibol.lucidmusic.singstr.gui.AppReleaseTimestamp
import com.sensibol.lucidmusic.singstr.gui.AppVersionCode
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object AppModule {

    @Provides
    @AppVersionCode
    internal fun provideAppVersionCode(): Int = BuildConfig.VERSION_CODE

    @Provides
    @AppBuildName
    internal fun provideAppBuildName(): String = BuildConfig.BUILD_NAME + "-" + BuildConfig.BUILD_TYPE


    @Provides
    @AppReleaseTimestamp
    internal fun provideAppReleaseTimestamp(): String = BuildConfig.RELEASE_TIMESTAMP
}
