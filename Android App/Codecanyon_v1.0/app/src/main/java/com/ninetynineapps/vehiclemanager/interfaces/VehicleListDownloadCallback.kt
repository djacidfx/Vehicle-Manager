package com.ninetynineapps.vehiclemanager.interfaces

import com.ninetynineapps.vehiclemanager.pojo.VehicleClass
import java.util.ArrayList

interface VehicleListDownloadCallback {
    fun setVehicleDetailDownloadCallback(vehicleClassArrayList: ArrayList<VehicleClass>)
}