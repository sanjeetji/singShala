package com.sensibol.lucidmusic.singstr.db

import android.content.Context
import com.sensibol.lucidmusic.singstr.db.framework.AppSharedPrefDb
import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val DB_NAME = "app_database"

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Provides
    @Singleton
    internal fun appDatabase(@ApplicationContext context: Context): AppDatabase =
        AppSharedPrefDb(context.applicationContext)


}