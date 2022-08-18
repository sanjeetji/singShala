package com.sensibol.lucidmusic.singstr

import com.sensibol.lucidmusic.singstr.gui.AppBuildName
import com.sensibol.lucidmusic.singstr.gui.AppReleaseTimestamp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object AppModuleFixture {

    @Provides
    @AppBuildName
    internal fun provideAppBuildName(): String = "dummyAppBuildName"


    @Provides
    @AppReleaseTimestamp
    internal fun provideAppReleaseTimestamp(): String = "dummyAppReleaseTimestamp"
}
