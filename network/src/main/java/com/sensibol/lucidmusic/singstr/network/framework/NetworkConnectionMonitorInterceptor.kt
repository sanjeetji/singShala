package com.sensibol.lucidmusic.singstr.network.framework

import android.net.ConnectivityManager
import com.sensibol.lucidmusic.singstr.domain.WebServiceFailure
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

internal interface NetworkConnectionMonitor {
    val isNetworkConnected: Boolean
}

internal class NetworkConnectionMonitorInterceptor @Inject constructor(
    private val ncm: NetworkConnectionMonitor
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!ncm.isNetworkConnected) {
            throw WebServiceFailure.NoNetworkFailure()
        }
        return chain.proceed(chain.request())
    }
}

internal class AndroidNetworkConnectionMonitor(
    private val cm: ConnectivityManager
) : NetworkConnectionMonitor {
    override val isNetworkConnected: Boolean
        get() = cm.activeNetworkInfo?.isConnected ?: false
}

