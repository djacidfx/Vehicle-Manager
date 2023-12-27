package com.ninetynineapps.vehiclemanager.activities

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.CompoundButton
import com.ninetynineapps.vehiclemanager.R
import com.ninetynineapps.vehiclemanager.common.CommonConstantAd
import com.ninetynineapps.vehiclemanager.common.CommonConstants
import com.ninetynineapps.vehiclemanager.common.CommonUtilities
import com.ninetynineapps.vehiclemanager.common.DecimalDigitsInputFilter
import com.ninetynineapps.vehiclemanager.interfaces.ServiceListUploadCallback
import com.ninetynineapps.vehiclemanager.pojo.ServiceClass
import com.ninetynineapps.vehiclemanager.services.SetServiceList
import kotlinx.android.synthetic.main.activity_accident_detail.*
import kotlinx.android.synthetic.main.activity_refuel_detail.*
import kotlinx.android.synthetic.main.activity_service_detail.*
import kotlinx.android.synthetic.main.activity_service_detail.etDate
import kotlinx.android.synthetic.main.activity_service_detail.etDescription
import kotlinx.android.synthetic.main.activity_service_detail.etKmReading
import kotlinx.android.synthetic.main.activity_service_detail.etTotalAmount
import kotlinx.android.synthetic.main.activity_service_detail.imgBack
import kotlinx.android.synthetic.main.activity_service_detail.llAdView
import kotlinx.android.synthetic.main.activity_service_detail.llAdViewFacebook
import kotlinx.android.synthetic.main.activity_service_detail.tvClear
import kotlinx.android.synthetic.main.activity_service_detail.tvSave

import java.text.SimpleDateFormat
import java.util.*

class ServiceDetailActivity : AppCompatActivity(), View.OnClickListener, ServiceListUploadCallback,
    CompoundButton.OnCheckedChangeListener {

    private var vehicleId = ""
    private var vehicleMinDate = ""
    private var serviceId = ""
    private var isDataUpdated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_detail)
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

        chkBody.setOnCheckedChangeListener(this)
        chkBrakes.setOnCheckedChangeListener(this)

        chkClutch.setOnCheckedChangeListener(this)
        chkCollingSystem.setOnCheckedChangeListener(this)

        chkEngine.setOnCheckedChangeListener(this)
        chkSparkPlug.setOnCheckedChangeListener(this)

        chkGeneral.setOnCheckedChangeListener(this)
        chkOther.setOnCheckedChangeListener(this)

        chkOilChange.setOnCheckedChangeListener(this)
        chkBattery.setOnCheckedChangeListener(this)

        tvSave.setOnClickListener(this)
        tvClear.setOnClickListener(this)
    }

    private fun loadDataFromIntent() {
        val intent = intent
        if (intent!!.hasExtra(CommonConstants.KeyVehicleId)) {
            try {
                vehicleId = intent.getStringExtra(CommonConstants.KeyVehicleId)!!
                vehicleMinDate = intent.getStringExtra(CommonConstants.KeyVehicleMinDate)!!
                if (intent.hasExtra(CommonConstants.KeyServiceDetail)) {
                    val aClass = intent.getSerializableExtra(CommonConstants.KeyServiceDetail) as ServiceClass
                    serviceId = aClass.serviceId

                    etDate.setText(aClass.serviceDate)

                    chkBody.isChecked = aClass.serviceBody == "1"
                    chkBrakes.isChecked = aClass.serviceBrakes == "1"

                    chkClutch.isChecked = aClass.serviceClutch == "1"
                    chkCollingSystem.isChecked = aClass.serviceCollingSystem == "1"

                    chkEngine.isChecked = aClass.serviceEngine == "1"
                    chkSparkPlug.isChecked = aClass.serviceSparkPlug == "1"

                    chkGeneral.isChecked = aClass.serviceGeneral == "1"
                    chkOther.isChecked = aClass.serviceOther == "1"

                    chkOilChange.isChecked = aClass.serviceOilChange == "1"
                    chkBattery.isChecked = aClass.serviceBattery == "1"

                    etGarage.setText(aClass.serviceGarageName)
                    etContactNo.setText(aClass.serviceContactNo)
                    try {
                        etTotalAmount.setText(aClass.serviceAmount)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    etKmReading.setText(aClass.serviceKmReading)
                    etDescription.setText(aClass.serviceDescription)
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
        } else if (id == R.id.tvSave) {
            checkValidation()
        } else if (id == R.id.tvClear) {
            showClearDataConfirmDialog(this)
        }
    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {

    }

    private var datePickerDialog: DatePickerDialog? = null

    private fun openDatePickerDialog(context: Context) {
        try {
            if (datePickerDialog == null) {
                val calendar = Calendar.getInstance()
                val cYear = calendar.get(Calendar.YEAR)
                val cMonth = calendar.get(Calendar.MONTH)
                val cDay = calendar.get(Calendar.DAY_OF_MONTH)
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

    private fun checkValidation() {
        if (etDate.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgServiceDateRequired)
        } else if (!checkAnyServiceChecked()) {
            CommonUtilities.showToast(this, CommonConstants.MsgSelectAnyService)
        } else if (etTotalAmount.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgServiceTotalAmountRequired)
        } else {
            if (CommonUtilities.isOnline(this)) {
                callSetServiceMasterService()
            } else {
                startActivity(Intent(this, NoInternetActivity::class.java))
            }
        }
    }

    private fun checkAnyServiceChecked(): Boolean {
        var isChecked = false
        when {
            chkBody.isChecked -> isChecked = true
            chkBrakes.isChecked -> isChecked = true
            chkClutch.isChecked -> isChecked = true
            chkCollingSystem.isChecked -> isChecked = true
            chkEngine.isChecked -> isChecked = true
            chkSparkPlug.isChecked -> isChecked = true
            chkGeneral.isChecked -> isChecked = true
            chkOther.isChecked -> isChecked = true
            chkOilChange.isChecked -> isChecked = true
            chkBattery.isChecked -> isChecked = true
        }
        return isChecked
    }

    private fun callSetServiceMasterService() {
        val aClass = ServiceClass()
        try {
            aClass.serviceId = serviceId
            aClass.serviceDate = etDate.text.toString()
            if (chkBody.isChecked) {
                aClass.serviceBody = "1"
            } else {
                aClass.serviceBody = "0"
            }

            if (chkBrakes.isChecked) {
                aClass.serviceBrakes = "1"
            } else {
                aClass.serviceBrakes = "0"
            }

            if (chkClutch.isChecked) {
                aClass.serviceClutch = "1"
            } else {
                aClass.serviceClutch = "0"
            }

            if (chkCollingSystem.isChecked) {
                aClass.serviceCollingSystem = "1"
            } else {
                aClass.serviceCollingSystem = "0"
            }

            if (chkEngine.isChecked) {
                aClass.serviceEngine = "1"
            } else {
                aClass.serviceEngine = "0"
            }

            if (chkSparkPlug.isChecked) {
                aClass.serviceSparkPlug = "1"
            } else {
                aClass.serviceSparkPlug = "0"
            }

            if (chkGeneral.isChecked) {
                aClass.serviceGeneral = "1"
            } else {
                aClass.serviceGeneral = "0"
            }

            if (chkOther.isChecked) {
                aClass.serviceOther = "1"
            } else {
                aClass.serviceOther = "0"
            }

            if (chkOilChange.isChecked) {
                aClass.serviceOilChange = "1"
            } else {
                aClass.serviceOilChange = "0"
            }

            if (chkBattery.isChecked) {
                aClass.serviceBattery = "1"
            } else {
                aClass.serviceBattery = "0"
            }


            aClass.serviceGarageName = etGarage.text.toString()
            aClass.serviceContactNo = etContactNo.text.toString()
            aClass.serviceAmount = etTotalAmount.text.toString()
            aClass.serviceKmReading = etKmReading.text.toString()
            aClass.serviceDescription = etDescription.text.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        SetServiceList(this, vehicleId, aClass, this@ServiceDetailActivity).callSetServiceListService()
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
        if (serviceId.isEmpty()) {
            etDate.setText("")
        }

        chkBody.isChecked = false
        chkBrakes.isChecked = false

        chkClutch.isChecked = false
        chkCollingSystem.isChecked = false

        chkEngine.isChecked = false
        chkSparkPlug.isChecked = false

        chkGeneral.isChecked = false
        chkOther.isChecked = false

        chkOilChange.isChecked = false
        chkBattery.isChecked = false

        etGarage.setText("")
        etTotalAmount.setText("")
        etContactNo.setText("")
        etKmReading.setText("")
        etDescription.setText("")
    }


    override fun setServiceDetailUploadCallback(isSuccess: Boolean) {
        if (isSuccess) {
            isDataUpdated = true
            CommonUtilities.setResultAndFinish(this, isDataUpdated)
        }
    }

    override fun onBackPressed() {
        CommonUtilities.showAlertFinishOnClick(this, isDataUpdated)
    }
}