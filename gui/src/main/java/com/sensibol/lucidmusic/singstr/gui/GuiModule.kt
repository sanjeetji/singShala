package com.sensibol.lucidmusic.singstr.gui

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppVersionCode

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppBuildName

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppReleaseTimestamp
