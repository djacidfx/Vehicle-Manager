package com.ninetynineapps.vehiclemanager.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.view.View
import com.ninetynineapps.vehiclemanager.R
import com.ninetynineapps.vehiclemanager.common.CommonConstants
import com.ninetynineapps.vehiclemanager.common.CommonUtilities
import com.ninetynineapps.vehiclemanager.interfaces.AccidentListUploadCallback
import com.ninetynineapps.vehiclemanager.pojo.AccidentClass
import com.ninetynineapps.vehiclemanager.services.SetAccidentList
import kotlinx.android.synthetic.main.activity_accident_detail.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import android.text.InputFilter
import android.text.TextWatcher
import com.ninetynineapps.vehiclemanager.common.CommonConstantAd
import com.ninetynineapps.vehiclemanager.common.DecimalDigitsInputFilter
import kotlinx.android.synthetic.main.activity_accident_detail.etDate
import kotlinx.android.synthetic.main.activity_accident_detail.etKmReading
import kotlinx.android.synthetic.main.activity_accident_detail.etTotalAmount
import kotlinx.android.synthetic.main.activity_accident_detail.imgBack
import kotlinx.android.synthetic.main.activity_accident_detail.llAdView
import kotlinx.android.synthetic.main.activity_accident_detail.llAdViewFacebook
import kotlinx.android.synthetic.main.activity_accident_detail.tvClear
import kotlinx.android.synthetic.main.activity_accident_detail.tvSave
import kotlinx.android.synthetic.main.activity_refuel_detail.*


class AccidentDetailActivity : AppCompatActivity(), View.OnClickListener, AccidentListUploadCallback {

    private var vehicleId = ""
    private var vehicleMinDate = ""
    private var accidentId = ""
    private var isDataUpdated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accident_detail)
        initViews()
        loadDataFromIntent()
//        CommonUtilities.loadBannerAd(findViewById(R.id.adViewBottom))

        if (CommonUtilities.getPref(this,CommonConstants.AD_TYPE_FB_GOOGLE,"") == CommonConstants.AD_GOOGLE &&
            CommonUtilities.getPref(this,CommonConstants.STATUS_ENABLE_DISABLE,"") == CommonConstants.ENABLE) {
            CommonConstantAd.loadBannerGoogleAd(this,llAdView)
            llAdViewFacebook.visibility= View.GONE
            llAdView.visibility = View.VISIBLE
        } else if (CommonUtilities.getPref(this,CommonConstants.AD_TYPE_FB_GOOGLE,"") == CommonConstants.AD_FACEBOOK
            && CommonUtilities.getPref(this,CommonConstants.STATUS_ENABLE_DISABLE,"") == CommonConstants.ENABLE) {
            CommonConstantAd.loadBannerFacebookAd(this,llAdViewFacebook)
            llAdViewFacebook.visibility= View.VISIBLE
            llAdView.visibility = View.GONE
        }else{
            llAdView.visibility = View.GONE
            llAdViewFacebook.visibility= View.GONE
        }

        CommonUtilities.addKeyboardDetectListener(this,llAdView,llAdViewFacebook)
    }

    private fun initViews() {
        etKmReading.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(7, 1));
        etKmReading.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val data = CommonUtilities.getKmReadingData(p0)
                if (data.isNotEmpty()) {
                    etKmReading.setText(data)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })

        imgBack.setOnClickListener(this)
        etDate.setOnClickListener(this)
        etTime.setOnClickListener(this)
        tvSave.setOnClickListener(this)
        tvClear.setOnClickListener(this)
    }

    private fun loadDataFromIntent() {
        val intent = intent
        if (intent!!.hasExtra(CommonConstants.KeyVehicleId)) {
            try {
                vehicleId = intent.getStringExtra(CommonConstants.KeyVehicleId)!!
                vehicleMinDate = intent.getStringExtra(CommonConstants.KeyVehicleMinDate)!!
                if (intent.hasExtra(CommonConstants.KeyAccidentDetail)) {
                    val aClass = intent.getSerializableExtra(CommonConstants.KeyAccidentDetail) as AccidentClass
                    accidentId = aClass.accidentId

                    etDate.setText(aClass.accidentDate)
                    etTime.setText(aClass.accidentTime)

                    etDriverName.setText(aClass.accidentDriverName)

                    try {
                        etTotalAmount.setText(aClass.accidentAmount)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    etKmReading.setText(aClass.accidentKmReading)
                    etDescription.setText(aClass.accidentDescription)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onClick(p0: View?) {
        val id = p0!!.id
        if (id == R.id.imgBack) {
            CommonUtilities.showAlertFinishOnClick(this, isDataUpdated)
        } else if (id == R.id.etDate) {
            openDatePickerDialog(this)
        } else if (id == R.id.etTime) {
            openTimePickerDialog(this)
        } else if (id == R.id.tvSave) {
            checkValidation()
        } else if (id == R.id.tvClear) {
            showClearDataConfirmDialog(this)
        }
    }

    private var datePickerDialog: DatePickerDialog? = null

    private fun openDatePickerDialog(context: Context) {
        try {
            if (datePickerDialog == null) {
                val calendar = Calendar.getInstance()
                val cYear = calendar.get(Calendar.YEAR)
                val cMonth = calendar.get(Calendar.MONTH)
                val cDay = calendar.get(Calendar.DAY_OF_MONTH) - 1
                datePickerDialog =
                        DatePickerDialog(context, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                            try {
                                try {
                                    calendar.set(Calendar.YEAR, year)
                                    calendar.set(Calendar.MONTH, month)
                                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                                    val sdf = SimpleDateFormat(CommonConstants.CapDateFormat, Locale.getDefault())
                                    etDate.setText(sdf.format(calendar.time))
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }, cYear, cMonth, cDay)
                datePickerDialog!!.datePicker.minDate = CommonUtilities.stringDateToLongFormatForMinDate(vehicleMinDate)
                datePickerDialog!!.datePicker.maxDate = Date().time
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            if (!datePickerDialog!!.isShowing) {
                datePickerDialog!!.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var timePickerDialog: TimePickerDialog? = null

    private fun openTimePickerDialog(context: Context) {
        if (timePickerDialog == null) {
            try {
                val calendar = Calendar.getInstance()
                val cHour = calendar.get(Calendar.HOUR_OF_DAY)
                val cMin = calendar.get(Calendar.MINUTE)
                timePickerDialog =
                        TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                            val finalTime = "$hourOfDay:$minute"
                            etTime.setText(CommonUtilities.get12HourFormatTime(finalTime))
                        }, cHour, cMin, true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        try {
            if (!timePickerDialog!!.isShowing) {
                timePickerDialog!!.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkValidation() {
        if (etDate.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgAccidentDateRequired)
        } else if (etTime.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgAccidentTimeRequired)
        } else if (etTotalAmount.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgAccidentAmountRequired)
        } else if (etKmReading.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgAccidentKmRequired)
        } else {
            if (CommonUtilities.isOnline(this)) {
                callSetAccidentMasterService()
            } else {
                startActivity(Intent(this, NoInternetActivity::class.java))
            }
        }
    }

    private fun callSetAccidentMasterService() {
        val aClass = AccidentClass()
        try {
            aClass.accidentId = accidentId
            aClass.accidentDate = etDate.text.toString()
            aClass.accidentTime = etTime.text.toString()
            aClass.accidentDriverName = etDriverName.text.toString()
            aClass.accidentAmount = etTotalAmount.text.toString()
            aClass.accidentKmReading = etKmReading.text.toString()
            aClass.accidentDescription = etDescription.text.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        SetAccidentList(this, vehicleId, aClass, this@AccidentDetailActivity).callSetAccidentListService()
    }

    private fun showClearDataConfirmDialog(context: Context) {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle(CommonConstants.CapConfirm)
        alertDialog.setMessage(CommonConstants.MsgDoYouWantToClear)
        alertDialog.setPositiveButton(CommonConstants.CapClear) { dialog, _ ->
            dialog.dismiss()
            clearAllData()
        }
        alertDialog.setNegativeButton(CommonConstants.CapCancel) { dialog, _ -> dialog.dismiss() }
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun clearAllData() {
        if (accidentId.isEmpty()) {
            etDate.setText("")
            etTime.setText("")
        }
        etDriverName.setText("")
        etTotalAmount.setText("")
        etKmReading.setText("")
        etDescription.setText("")
    }


    override fun setAccidentDetailUploadCallback(isSuccess: Boolean) {
        if (isSuccess) {
            isDataUpdated = true
            CommonUtilities.setResultAndFinish(this, isDataUpdated)
        }
    }

    override fun onBackPressed() {
        CommonUtilities.showAlertFinishOnClick(this, isDataUpdated)
    }
}