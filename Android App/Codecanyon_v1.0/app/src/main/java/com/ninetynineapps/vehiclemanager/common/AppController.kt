package com.ninetynineapps.vehiclemanager.common

import android.app.Application
import android.util.Base64

class AppController : Application() {

    companion object {
        var BaseUrl = ""
    }


    override fun onCreate() {
        super.onCreate()
        BaseUrl = "http://benzatineinfotech.com/webservice/vehicle_app/index.php/vehicle_api/"
    }
}