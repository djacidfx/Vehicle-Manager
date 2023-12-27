package com.ninetynineapps.vehiclemanager.interfaces

import com.ninetynineapps.vehiclemanager.pojo.InsuranceClass
import java.util.ArrayList

interface InsuranceListDownloadCallback {
    fun setInsuranceDetailDownloadCallback(insuranceClassArrayList: ArrayList<InsuranceClass>)
}