package com.akhil.demotask.service

import com.akhil.demotask.DemoApp
import com.akhil.demotask.model.FileUploadResponse
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.kodein.di.android.BuildConfig
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.net.UnknownHostException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.*


interface APIService {

    @Multipart
    @POST("FMAPI/all-about-loans/file-upload-test.php")
    fun UploadFile(
        @Part communityimage: MultipartBody.Part?
    ): Call<FileUploadResponse>

    companion object {
        operator fun invoke(networkConnectionInterceptor: NetworkConnectionInterceptor): APIService {

            var okkHttpclientBuilder = OkHttpClient.Builder()

            okkHttpclientBuilder.addInterceptor(networkConnectionInterceptor)
            okkHttpclientBuilder.connectTimeout(30000, TimeUnit.SECONDS); // connect timeout
            okkHttpclientBuilder.readTimeout(30000, TimeUnit.SECONDS);
            okkHttpclientBuilder.writeTimeout(30000, TimeUnit.SECONDS);
            try {
                okkHttpclientBuilder.addInterceptor(Interceptor { chain ->
                    val original = chain.request()
                    var requestBuilder: Request.Builder = original.newBuilder()
                        .header("accept", "*/*")
                        .method(original.method, original.body)

                    val request = requestBuilder.build()
                    val response = chain.proceed(request)
                    response
                })
                okkHttpclientBuilder.addInterceptor(loggingInterceptor())
            } catch (e: UnknownHostException) {
                e.printStackTrace()
            }

            return Retrofit.Builder()
                .client(
                    okkHttpclientBuilder

                        .build()
                )
                .baseUrl(DemoApp.getBaseURL())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(APIService::class.java)


        }

        private fun loggingInterceptor(): HttpLoggingInterceptor {
            val logging = HttpLoggingInterceptor()
            logging.level = if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE
            return logging
        }
    }


}

