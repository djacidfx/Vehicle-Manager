package com.ninetynineapps.vehiclemanager.interfaces

import com.ninetynineapps.vehiclemanager.pojo.SummaryClass

interface SummaryDownloadCallback {
    fun setSummaryDetailDownloadCallback(summaryClassArrayList : ArrayList<SummaryClass>)
}