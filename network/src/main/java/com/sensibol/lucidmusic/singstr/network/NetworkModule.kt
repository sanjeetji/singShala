package com.sensibol.lucidmusic.singstr.network

import android.content.Context
import android.net.ConnectivityManager
import com.sensibol.lucidmusic.singstr.domain.webservice.*
import com.sensibol.lucidmusic.singstr.network.framework.AndroidNetworkConnectionMonitor
import com.sensibol.lucidmusic.singstr.network.framework.NetworkConnectionMonitor
import com.sensibol.lucidmusic.singstr.network.framework.NetworkConnectionMonitorInterceptor
import com.sensibol.lucidmusic.singstr.network.service.content.AppContentWebService
import com.sensibol.lucidmusic.singstr.network.service.content.RetrofitContentWebService
import com.sensibol.lucidmusic.singstr.network.service.contest.AppContestWebService
import com.sensibol.lucidmusic.singstr.network.service.contest.RetrofitContestWebService
import com.sensibol.lucidmusic.singstr.network.service.cover.AppCoverWebService
import com.sensibol.lucidmusic.singstr.network.service.cover.RetrofitCoverWebService
import com.sensibol.lucidmusic.singstr.network.service.curriculum.AppCurriculumWebService
import com.sensibol.lucidmusic.singstr.network.service.curriculum.RetrofitCurriculumWebService
import com.sensibol.lucidmusic.singstr.network.service.feed.AppFeedWebService
import com.sensibol.lucidmusic.singstr.network.service.feed.NodeJSAppFeedWebService
import com.sensibol.lucidmusic.singstr.network.service.feed.NodeJSRetrofitFeedWebService
import com.sensibol.lucidmusic.singstr.network.service.feed.RetrofitFeedWebService
import com.sensibol.lucidmusic.singstr.network.service.file.AppFileTransferWebService
import com.sensibol.lucidmusic.singstr.network.service.file.OkFileDownloadWebService
import com.sensibol.lucidmusic.singstr.network.service.file.RetrofitFileTransferWebService
import com.sensibol.lucidmusic.singstr.network.service.learn.AppLearnWebService
import com.sensibol.lucidmusic.singstr.network.service.learn.NodeJSAppLearnWebService
import com.sensibol.lucidmusic.singstr.network.service.learn.NodeJSRetrofitLeanWebService
import com.sensibol.lucidmusic.singstr.network.service.learn.RetrofitLearnWebService
import com.sensibol.lucidmusic.singstr.network.service.sing.AppSingWebService
import com.sensibol.lucidmusic.singstr.network.service.sing.NodeJsAppSingWebService
import com.sensibol.lucidmusic.singstr.network.service.sing.NodeJsRetrofitSingWebService
import com.sensibol.lucidmusic.singstr.network.service.sing.RetrofitSingWebService
import com.sensibol.lucidmusic.singstr.network.service.streak.AppStreakWebService
import com.sensibol.lucidmusic.singstr.network.service.streak.RetrofitStreakWebService
import com.sensibol.lucidmusic.singstr.network.service.user.*
import com.sensibol.lucidmusic.singstr.network.service.user.AppUserWebService
import com.sensibol.lucidmusic.singstr.network.service.user.NodeJSAppUserWebService
import com.sensibol.lucidmusic.singstr.network.service.user.NodeJSRetrofitUserWebService
import com.sensibol.lucidmusic.singstr.network.service.user.RetrofitDownloadVideoWebService
import com.sensibol.lucidmusic.singstr.network.service.user.RetrofitUserWebService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @GoLanEndpoint
    @Provides
    @Singleton
    internal fun provideRetrofit(
        builder: Retrofit.Builder,
        okHttpClient: OkHttpClient,
    ): Retrofit {
        return builder
            .baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @NodeJSEndpoint
    @Provides
    @Singleton
    internal fun provideNodeJsRetrofit(
        builder: Retrofit.Builder,
        okHttpClient: OkHttpClient,
    ): Retrofit {
        return builder
            .baseUrl(BuildConfig.API_BASE_URL_NODE_JS)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @DownloadVideoEndpoint
    @Provides
    @Singleton
    internal fun provideDownloadVideoRetrofit(
        builder: Retrofit.Builder,
    ): Retrofit {
        return builder
            .baseUrl("http://192.168.43.135/retro/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    internal fun provideRetrofitBuilder(): Retrofit.Builder =
        Retrofit.Builder()

    @Provides
    @Singleton
    internal fun provideOkHttpClient(builder: OkHttpClient.Builder): OkHttpClient =
        builder.build()

    @Provides
    @Singleton
    internal fun provideOkHttpClientBuilder(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        networkConnectionMonitorInterceptor: NetworkConnectionMonitorInterceptor,
    ): OkHttpClient.Builder {
        return OkHttpClient.Builder().apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(httpLoggingInterceptor)
            }
            addNetworkInterceptor(networkConnectionMonitorInterceptor)
        }
    }

    @Provides
    @Singleton
    internal fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)


    @Provides
    @Singleton
    internal fun provideNetworkConnectionMonitor(@ApplicationContext context: Context): NetworkConnectionMonitor =
        AndroidNetworkConnectionMonitor(context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)

//    @Provides
//    internal fun provideChuckerInterceptor(@ApplicationContext context: Context): ChuckerConnectionMonitor =
//        AndroidChuckerConnectionMonitor(ChuckerInterceptor.Builder(context).build())

    @Provides
    @Singleton
    internal fun provideRetrofitUserWebService(@GoLanEndpoint retrofit: Retrofit): RetrofitUserWebService =
        retrofit.create(RetrofitUserWebService::class.java)

    @Provides
    @Singleton
    internal fun provideNodeJSRetrofitUserWebService(@NodeJSEndpoint retrofit: Retrofit): NodeJSRetrofitUserWebService =
        retrofit.create(NodeJSRetrofitUserWebService::class.java)

    @Provides
    @Singleton
    internal fun provideRetrofitCoverWebService(@GoLanEndpoint retrofit: Retrofit): RetrofitCoverWebService =
        retrofit.create(RetrofitCoverWebService::class.java)

    @Provides
    @Singleton
    internal fun provideRetrofitContentWebService(@GoLanEndpoint retrofit: Retrofit): RetrofitContentWebService =
        retrofit.create(RetrofitContentWebService::class.java)

    @Provides
    @Singleton
    internal fun provideOkFileDownloadWebService(): OkFileDownloadWebService =
        OkFileDownloadWebService()

    @Provides
    @Singleton
    internal fun provideRetrofitFileTransferWebService(@GoLanEndpoint retrofit: Retrofit): RetrofitFileTransferWebService =
        retrofit.create(RetrofitFileTransferWebService::class.java)

    @Provides
    @Singleton
    internal fun provideRetrofitSingWebService(@GoLanEndpoint retrofit: Retrofit): RetrofitSingWebService =
        retrofit.create(RetrofitSingWebService::class.java)

    @Provides
    @Singleton
    internal fun provideNodeJsRetrofitSingWebService(@NodeJSEndpoint retrofit: Retrofit): NodeJsRetrofitSingWebService =
        retrofit.create(NodeJsRetrofitSingWebService::class.java)

    @Provides
    @Singleton
    internal fun provideRetrofitLearnWebService(@GoLanEndpoint retrofit: Retrofit): RetrofitLearnWebService =
        retrofit.create(RetrofitLearnWebService::class.java)

    @Provides
    @Singleton
    internal fun provideNodeJSRetrofitLearnWebService(@NodeJSEndpoint retrofit: Retrofit): NodeJSRetrofitLeanWebService =
        retrofit.create(NodeJSRetrofitLeanWebService::class.java)

    @Provides
    @Singleton
    internal fun provideRetrofitFeedWebService(@GoLanEndpoint retrofit: Retrofit): RetrofitFeedWebService =
        retrofit.create(RetrofitFeedWebService::class.java)

    @Provides
    @Singleton
    internal fun provideRetrofitCurriculumWebService(@GoLanEndpoint retrofit: Retrofit): RetrofitCurriculumWebService =
        retrofit.create(RetrofitCurriculumWebService::class.java)

    @Provides
    @Singleton
    internal fun provideStreakWebService(@NodeJSEndpoint retrofit: Retrofit): RetrofitStreakWebService =
        retrofit.create(RetrofitStreakWebService::class.java)

    @Provides
    @Singleton
    internal fun providerContestWebService(@NodeJSEndpoint retrofit: Retrofit): RetrofitContestWebService =
        retrofit.create(RetrofitContestWebService::class.java)

    @Provides
    @Singleton
    internal fun providerNodeJSRetrofitFeedWebService(@NodeJSEndpoint retrofit: Retrofit): NodeJSRetrofitFeedWebService =
        retrofit.create(NodeJSRetrofitFeedWebService::class.java)

    @Provides
    @Singleton
    internal fun providerDownloadVideoRetrofitService(@DownloadVideoEndpoint retrofit: Retrofit): RetrofitDownloadVideoWebService =
        retrofit.create(RetrofitDownloadVideoWebService::class.java)


    @Module
    @InstallIn(SingletonComponent::class)
    internal interface AppWebService {

        @Binds
        @Singleton
        abstract fun bindContentWebService(service: AppContentWebService): ContentWebService

        @Binds
        @Singleton
        abstract fun bindUserWebService(service: AppUserWebService): UserWebService

        @Binds
        @Singleton
        abstract fun bindNodeJSUserWebService(service: NodeJSAppUserWebService): NodeJSUserWebService

        @Binds
        @Singleton
        abstract fun bindCoverWebService(service: AppCoverWebService): CoverWebService

        @Binds
        @Singleton
        abstract fun bindFileDownloadWebService(service: OkFileDownloadWebService): FileDownloadWebService

        @Binds
        @Singleton
        abstract fun bindFileTransferWebService(service: AppFileTransferWebService): FileTransferWebService

        @Binds
        @Singleton
        abstract fun bindSingWebService(service: AppSingWebService): SingWebService

        @Binds
        @Singleton
        abstract fun bindNodeJsSingWebService(service: NodeJsAppSingWebService): NodeJsSingWebService

        @Binds
        @Singleton
        abstract fun bindLearnWebService(service: AppLearnWebService): LearnWebService

        @Binds
        @Singleton
        abstract fun bindNodeJSLearnWebService(service: NodeJSAppLearnWebService): NodeJSLearnWebService

        @Binds
        @Singleton
        abstract fun bindFeedWebService(service: AppFeedWebService): FeedWebService

        @Binds
        @Singleton
        abstract fun bindCurriculumWebService(service: AppCurriculumWebService): CurriculumWebService

        @Binds
        @Singleton
        abstract fun bindStreakWebService(service: AppStreakWebService): StreakWebService

        @Binds
        @Singleton
        abstract fun bindContestWebService(service: AppContestWebService): ContestWebService

        @Binds
        @Singleton
        abstract fun bindNodeJSAppFeedWebService(service: NodeJSAppFeedWebService): NodeJsFeedWebService

        @Binds
        @Singleton
        abstract fun bindDownloadVideoWebService(service: AppDownloadVideoWebService): DownloadVideoWebService


    }

}
