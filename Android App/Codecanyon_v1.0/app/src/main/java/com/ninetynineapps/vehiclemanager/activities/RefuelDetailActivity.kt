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
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.ninetynineapps.vehiclemanager.R
import com.ninetynineapps.vehiclemanager.common.CommonConstantAd
import com.ninetynineapps.vehiclemanager.common.CommonConstants
import com.ninetynineapps.vehiclemanager.common.CommonUtilities
import com.ninetynineapps.vehiclemanager.common.DecimalDigitsInputFilter
import com.ninetynineapps.vehiclemanager.interfaces.RefuelListUploadCallback
import com.ninetynineapps.vehiclemanager.pojo.RefuelClass
import com.ninetynineapps.vehiclemanager.services.SetRefuelList
import kotlinx.android.synthetic.main.activity_accident_detail.*
import kotlinx.android.synthetic.main.activity_refuel_detail.*
import kotlinx.android.synthetic.main.activity_refuel_detail.etDate
import kotlinx.android.synthetic.main.activity_refuel_detail.etKmReading
import kotlinx.android.synthetic.main.activity_refuel_detail.etTotalAmount
import kotlinx.android.synthetic.main.activity_refuel_detail.imgBack
import kotlinx.android.synthetic.main.activity_refuel_detail.llAdView
import kotlinx.android.synthetic.main.activity_refuel_detail.llAdViewFacebook
import kotlinx.android.synthetic.main.activity_refuel_detail.tvClear
import kotlinx.android.synthetic.main.activity_refuel_detail.tvSave
import java.text.SimpleDateFormat
import java.util.*

class RefuelDetailActivity : AppCompatActivity(), View.OnClickListener, RefuelListUploadCallback {

    private var vehicleId = ""
    private var vehicleMinDate = ""
    private var refuelId = ""
    private var isDataUpdated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_refuel_detail)
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

        imgBack.setOnClickListener(this)

        etDate.setOnClickListener(this)

        etTotalAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                try {
                    val totalAmt = etTotalAmount.text.toString()
                    if (totalAmt.isNotEmpty()) {
                        val intTotalAmt = totalAmt.toInt()
                        if (etFuelPrice.text.toString().isNotEmpty()) {
                            try {
                                val intFuelPrice = etFuelPrice.text.toString().toFloat()
                                val qty = (intTotalAmt / intFuelPrice)
                                etQuantity.setText(String.format("%.2f", qty))
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    } else {
                        etQuantity.setText("")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }
        })

        etFuelPrice.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                try {
                    val totalAmt = etTotalAmount.text.toString()
                    if (totalAmt.isNotEmpty()) {
                        val intTotalAmt = totalAmt.toInt()
                        if (etFuelPrice.text.toString().isNotEmpty()) {
                            try {
                                val intFuelPrice = etFuelPrice.text.toString().toFloat()
                                val qty = (intTotalAmt / intFuelPrice)
                                etQuantity.setText(String.format("%.2f", qty))
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    } else {
                        etQuantity.setText("")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

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


        tvSave.setOnClickListener(this)
        tvClear.setOnClickListener(this)
    }

    private fun loadDataFromIntent() {
        val intent = intent
        if (intent!!.hasExtra(CommonConstants.KeyVehicleId)) {
            try {
                vehicleId = intent.getStringExtra(CommonConstants.KeyVehicleId)!!
                vehicleMinDate = intent.getStringExtra(CommonConstants.KeyVehicleMinDate)!!
                val fuelType = intent.getStringExtra(CommonConstants.KeyIsFuelType)
                etFuelType.setText(fuelType)
                Log.e("TAG", "loadDataFromIntent:::: $fuelType")
                if (intent.hasExtra(CommonConstants.KeyRefuelDetail)) {
                    val aClass = intent.getSerializableExtra(CommonConstants.KeyRefuelDetail) as RefuelClass
                    refuelId = aClass.refuelId

                    etDate.setText(aClass.refuelDate)
                    etFuelType.setText(aClass.refuelType)
                    Log.e("TAG", "loadDataFromIntent::::aClass    ${aClass.refuelType}")
                    try {
                        etTotalAmount.setText(aClass.refuelAmount)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    etFuelPrice.setText(aClass.refuelFuelPrice)
                    etQuantity.setText(aClass.refuelQuantity)
                    etFuelStation.setText(aClass.refuelStation)

                    etKmReading.setText(aClass.refuelKmReading)
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

    private var datePickerDialog: DatePickerDialog? = null

    private fun openDatePickerDialog(context: Context) {
        try {
            if (datePickerDialog == null) {
                val calendar = Calendar.getInstance()
                val cYear = calendar.get(Calendar.YEAR)
                val cMonth = calendar.get(Calendar.MONTH)
                val cDay = calendar.get(Calendar.DAY_OF_MONTH)
                datePickerDialog =
                        DatePickerDialog(
                            context,
                            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                                try {
                                    try {
                                        calendar.set(Calendar.YEAR, year)
                                        calendar.set(Calendar.MONTH, month)
                                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                                        val sdf = SimpleDateFormat(
                                            CommonConstants.CapDateFormat,
                                            Locale.getDefault()
                                        )
                                        etDate.setText(sdf.format(calendar.time))
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            },
                            cYear,
                            cMonth,
                            cDay
                        )
                datePickerDialog!!.datePicker.minDate =
                        CommonUtilities.stringDateToLongFormatForMinDate(vehicleMinDate)
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
            CommonUtilities.showToast(this, CommonConstants.MsgFuelDateRequired)
        } else if (etFuelType.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgFuelTypeRequired)
        } else if (etTotalAmount.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgFuelTotalAmountRequired)
        } else if (etFuelPrice.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgFuelPriceRequired)
        } else if (etKmReading.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgVehicleKmRequired)
        } else {
            if (CommonUtilities.isOnline(this)) {
                callSetFuelMasterService()
            } else {
                startActivity(Intent(this, NoInternetActivity::class.java))
            }
        }
    }

    private fun callSetFuelMasterService() {
        val aClass = RefuelClass()
        try {
            aClass.refuelId = refuelId
            aClass.refuelDate = etDate.text.toString()
            aClass.refuelType = etFuelType.text.toString()
            aClass.refuelAmount = etTotalAmount.text.toString()
            aClass.refuelFuelPrice = etFuelPrice.text.toString()
            aClass.refuelQuantity = etQuantity.text.toString()
            aClass.refuelStation = etFuelStation.text.toString()
            aClass.refuelKmReading = etKmReading.text.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        SetRefuelList(this, vehicleId, aClass, this@RefuelDetailActivity).callSetRefuelListService()
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
        if (refuelId.isEmpty()) {
            etDate.setText("")
            etFuelType.setText("")
        }
        etTotalAmount.setText("")
        etFuelPrice.setText("")
        etQuantity.setText("")
        etFuelStation.setText("")
        etKmReading.setText("")
    }

    override fun setRefuelDetailUploadCallback(isSuccess: Boolean) {
        if (isSuccess) {
            isDataUpdated = true
            CommonUtilities.setResultAndFinish(this, isDataUpdated)
        }
    }

    override fun onBackPressed() {
        CommonUtilities.showAlertFinishOnClick(this, isDataUpdated)
    }
}