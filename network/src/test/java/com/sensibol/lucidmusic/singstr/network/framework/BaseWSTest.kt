package com.sensibol.lucidmusic.singstr.network.framework

import com.sensibol.lucidmusic.singstr.network.networkModule
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import org.kodein.di.generic.with
import retrofit2.Retrofit


internal open class BaseWSTest {

    protected var isNetworkConnected: Boolean = true

    private val kodein = Kodein.lazy {
        import(networkModule)
        bind<NetworkConnectionMonitor>(overrides = true) with singleton {
            object : NetworkConnectionMonitor {
                override val isNetworkConnected = this@BaseWSTest.isNetworkConnected
            }
        }
        constant(tag = "apiBaseUrl", overrides = true) with server.url("/").toString()
    }

    protected lateinit var server: MockWebServer

    @Before
    fun startServer() {
        server = MockWebServer()
        server.start()
    }

    @After
    fun shutDownServer() {
        server.shutdown()
    }

    protected fun getService(): RetrofitWebService {
        val retrofit: Retrofit by kodein.instance<Retrofit>()
        return retrofit.create(RetrofitWebService::class.java)
    }

    protected object MockDispatcher : Dispatcher() {
        override fun dispatch(request: RecordedRequest) = when (request.path) {
            "/songs" -> resourceToMockResponse("/songs.json")
            else -> MockResponse().setResponseCode(404)
        }
    }

}

internal fun resourceToMockResponse(resPath: String): MockResponse =
    MockResponse().setBody(BaseWSTest::class.java.getResource(resPath)!!.readText())
