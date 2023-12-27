package com.ninetynineapps.vehiclemanager.interfaces

import com.ninetynineapps.vehiclemanager.pojo.AccidentClass

interface AccidentListDownloadCallback {
    fun setAccidentDetailDownloadCallback(accidentClassArrayList: ArrayList<AccidentClass>)
}