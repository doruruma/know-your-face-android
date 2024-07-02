package id.andra.knowmyface

import android.app.Application
import id.andra.knowmyface.api.RetrofitClient

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitClient.init(this)
    }
}