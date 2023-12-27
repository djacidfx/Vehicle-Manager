package com.ninetynineapps.vehiclemanager.interfaces

import com.ninetynineapps.vehiclemanager.pojo.PUCClass

interface PUCListDownloadCallback {
    fun setPUCDetailDownloadCallback(pucClassArrayList: ArrayList<PUCClass>)
}