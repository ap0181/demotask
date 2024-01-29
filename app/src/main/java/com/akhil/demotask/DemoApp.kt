package com.akhil.demotask

import android.app.Application
import com.akhil.demotask.service.repository.FileUploadRepository
import com.akhil.demotask.service.APIService
import com.akhil.demotask.service.NetworkConnectionInterceptor
import com.akhil.demotask.service.viewmodel.FileUploadModelFactory
import com.akhil.demotask.service.viewmodel.FileUploadViewModel
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class DemoApp : Application(), KodeinAware {


    override val kodein: Kodein = Kodein.lazy {
        import(androidXModule(this@DemoApp))

        bind() from singleton { NetworkConnectionInterceptor(instance()) }
        bind() from singleton { APIService(instance()) }
        bind() from provider { FileUploadRepository(instance()) }
        bind() from provider { FileUploadModelFactory(instance()) }
    }

    companion object {
        private var instance: DemoApp? = null
        fun getBaseURL(): String {
            return "https://finance-market.co.in/"
        }

        @JvmStatic
        fun getInstance(): DemoApp {
            return instance as DemoApp
        }
    }

}