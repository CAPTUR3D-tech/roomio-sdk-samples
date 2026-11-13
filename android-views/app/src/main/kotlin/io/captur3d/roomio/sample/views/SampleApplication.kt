package io.captur3d.roomio.sample.views

import android.app.Application
import io.captur3d.roomio.sdk.Roomio

class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Roomio.isWorkerProcess()) return
        Roomio.configure(this, publishableKey = "pk_test_replace_me")
    }
}
