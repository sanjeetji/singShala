package com.sensibol.lucidmusic.singstr.network.service.user

import android.os.Environment
import android.support.v4.app.RemoteActionCompatParcelizer.write
import android.system.Os.write
import androidx.core.graphics.drawable.IconCompatParcelizer.write
import com.sensibol.lucidmusic.singstr.domain.webservice.DownloadVideoWebService
import okhttp3.ResponseBody
import org.apache.commons.io.IOUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files.write
import javax.inject.Inject

internal class AppDownloadVideoWebService @Inject constructor(
    private val webService: RetrofitDownloadVideoWebService
) : DownloadVideoWebService {

    override suspend fun getDownloadVideo(videoUrl: String, filePath: String, attemptId: String) {
        webService.downloadVide(videoUrl).enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    val file = File(filePath, "$attemptId.mp4")
                    val fileOutputStream = FileOutputStream(file)
                    IOUtils.write(response.body()!!.bytes(), fileOutputStream)
                } catch (ex: Exception) {

                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }

        })
    }

}