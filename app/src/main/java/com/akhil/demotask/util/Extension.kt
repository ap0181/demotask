package com.akhil.demotask.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.TextUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

fun isInternetAvailable(mContext: Context): Boolean {
    var result = false
    val connectivityManager =
        mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    connectivityManager?.let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            it.getNetworkCapabilities(connectivityManager.activeNetwork)?.apply {
                result = when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            }
        }
        else {
            return it.activeNetworkInfo != null && it.activeNetworkInfo!!.isConnected
        }
    }

    return result
}

fun createPartFromImageUrl(key: String, uri: String?): MultipartBody.Part? {
    var body: MultipartBody.Part? = null
    if (uri != null)
        if (!TextUtils.isEmpty(uri) && !uri.startsWith("http")) {
            val file = File(uri)
            val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            body = MultipartBody.Part.createFormData(key, file.name, reqFile)
        }
    return body
}