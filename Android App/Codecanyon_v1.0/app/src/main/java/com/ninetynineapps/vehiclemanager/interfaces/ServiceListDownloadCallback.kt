package com.ninetynineapps.vehiclemanager.interfaces

import com.ninetynineapps.vehiclemanager.pojo.ServiceClass
import java.util.ArrayList

interface ServiceListDownloadCallback {
    fun setServiceDetailDownloadCallback(serviceClassArrayList: ArrayList<ServiceClass>)
}