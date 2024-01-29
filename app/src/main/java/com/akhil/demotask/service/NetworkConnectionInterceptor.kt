package com.akhil.demotask.service

import android.content.Context
import com.akhil.demotask.DemoApp
import com.akhil.demotask.R
import com.akhil.demotask.util.NoInternetException
import com.akhil.demotask.util.isInternetAvailable
import okhttp3.Interceptor
import okhttp3.Response

class NetworkConnectionInterceptor(context: Context) : Interceptor {

    private val applicationContext = context.applicationContext

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isInternetAvailable(applicationContext))
            throw NoInternetException(DemoApp.getInstance().getString(R.string.connection_error))
        return chain.proceed(chain.request())
    }


}