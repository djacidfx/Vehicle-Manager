package com.ninetynineapps.vehiclemanager.interfaces

import com.ninetynineapps.vehiclemanager.pojo.RefuelClass
import java.util.ArrayList

interface RefuelListDownloadCallback {
    fun setRefuelDetailDownloadCallback(refuelClassArrayList: ArrayList<RefuelClass>)
}