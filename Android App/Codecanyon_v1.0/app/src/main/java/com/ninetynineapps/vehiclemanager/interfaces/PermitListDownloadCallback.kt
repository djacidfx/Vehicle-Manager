package com.ninetynineapps.vehiclemanager.interfaces

import com.ninetynineapps.vehiclemanager.pojo.PermitClass

interface PermitListDownloadCallback {
    fun setPermitDetailDownloadCallback(permitClassArrayList: ArrayList<PermitClass>)
}