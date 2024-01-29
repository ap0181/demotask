package com.akhil.demotask.util

import android.content.Context
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View

import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class Constant {

    companion object {
        const val HTTP_UNAUTHORIZED = "HTTP_UNAUTHORIZED"
        const val HTTP_CREATED = "HTTP_CREATED"
        const val HTTP_BAD_REQUEST = "HTTP_BAD_REQUEST"
        const val HTTP_NOT_FOUND = "HTTP_NOT_FOUND"
    }
}



