package com.ninetynineapps.vehiclemanager.interfaces

import com.ninetynineapps.vehiclemanager.pojo.ExpenseClass
import java.util.ArrayList

interface ExpenseListDownloadCallback {
    fun setExpenseDetailDownloadCallback(expenseClassArrayList: ArrayList<ExpenseClass>)
}