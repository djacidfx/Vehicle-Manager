package com.ninetynineapps.vehiclemanager.activities

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import com.ninetynineapps.vehiclemanager.R
import com.ninetynineapps.vehiclemanager.common.CommonConstantAd
import com.ninetynineapps.vehiclemanager.common.CommonConstants
import com.ninetynineapps.vehiclemanager.common.CommonUtilities
import com.ninetynineapps.vehiclemanager.interfaces.InsuranceListUploadCallback
import com.ninetynineapps.vehiclemanager.pojo.InsuranceClass
import com.ninetynineapps.vehiclemanager.services.SetInsuranceList
import kotlinx.android.synthetic.main.activity_accident_detail.*
import kotlinx.android.synthetic.main.activity_insurance_detail.*
import kotlinx.android.synthetic.main.activity_insurance_detail.etDescription
import kotlinx.android.synthetic.main.activity_insurance_detail.imgBack
import kotlinx.android.synthetic.main.activity_insurance_detail.llAdView
import kotlinx.android.synthetic.main.activity_insurance_detail.llAdViewFacebook
import kotlinx.android.synthetic.main.activity_insurance_detail.tvClear
import kotlinx.android.synthetic.main.activity_insurance_detail.tvSave
import kotlinx.android.synthetic.main.activity_refuel_detail.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class InsuranceDetailActivity : AppCompatActivity(), View.OnClickListener, InsuranceListUploadCallback {

    private var vehicleId = ""
    private var vehicleMinDate = ""
    private var insuranceId = ""
    private var isDataUpdated = false

    private var issueDatePickerDialog: DatePickerDialog? = null
    private var expiryDatePickerDialog: DatePickerDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insurance_detail)
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
        etPolicyType.setOnClickListener(this)
        etIssueDate.setOnClickListener(this)
        etExpiryDate.setOnClickListener(this)
        etPaymentMode.setOnClickListener(this)

        tvSave.setOnClickListener(this)
        tvClear.setOnClickListener(this)
    }

    private fun loadDataFromIntent() {
        val intent = intent
        if (intent!!.hasExtra(CommonConstants.KeyVehicleId)) {
            try {
                vehicleId = intent.getStringExtra(CommonConstants.KeyVehicleId)!!
                vehicleMinDate = intent.getStringExtra(CommonConstants.KeyVehicleMinDate)!!
                if (intent.hasExtra(CommonConstants.KeyInsuranceDetail)) {
                    val aClass = intent.getSerializableExtra(CommonConstants.KeyInsuranceDetail) as InsuranceClass
                    insuranceId = aClass.insuranceId

                    etCompany.setText(aClass.insuranceCompany)
                    etPolicyType.setText(aClass.insurancePolicyType)
                    etPolicyNo.setText(aClass.insurancePolicyNo)

                    etIssueDate.setText(aClass.insuranceIssueDate)
                    etExpiryDate.setText(aClass.insuranceExpiryDate)

                    etPaymentMode.setText(aClass.insurancePaymentMode)

                    try {
                        etInsuranceAmount.setText(aClass.insuranceAmount)
                        etPremiumAmount.setText(aClass.insurancePremium)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    etAgentName.setText(aClass.insuranceAgentName)
                    etAgentContactNo.setText(aClass.insuranceAgentPhone)
                    etDescription.setText(aClass.insuranceDescription)
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
        } else if (id == R.id.etPolicyType) {
            openPolicyTypeDialog(p0)
        } else if (id == R.id.etIssueDate) {
            openIssueDatePickerDialog(this)
        } else if (id == R.id.etExpiryDate) {
            openExpiryDatePickerDialog(this)
        } else if (id == R.id.etPaymentMode) {
            openPaymentModeMenu(p0)
        } else if (id == R.id.tvSave) {
            checkValidation()
        } else if (id == R.id.tvClear) {
            showClearDataConfirmDialog(this)
        }
    }

    private fun openPolicyTypeDialog(view: View) {
        val menu = PopupMenu(this, view, Gravity.END)
        menu.setOnMenuItemClickListener { item ->
            etPolicyType.setText(item.title.toString())
            false
        }
        menu.inflate(R.menu.menu_policy_type)
        menu.show()
    }
    private fun openPaymentModeMenu(view: View) {
        val menu = PopupMenu(this, view, Gravity.END)
        menu.setOnMenuItemClickListener { item ->
            etPaymentMode.setText(item.title.toString())
            false
        }
        menu.inflate(R.menu.menu_payment_mode)
        menu.show()
    }

    private fun openIssueDatePickerDialog(context: Context) {
        try {
            if (issueDatePickerDialog == null) {
                val calendar = Calendar.getInstance()
                val cYear = calendar.get(Calendar.YEAR)
                val cMonth = calendar.get(Calendar.MONTH)
                val cDay = calendar.get(Calendar.DAY_OF_MONTH)
                issueDatePickerDialog =
                        DatePickerDialog(context, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                            try {
                                try {
                                    calendar.set(Calendar.YEAR, year)
                                    calendar.set(Calendar.MONTH, month)
                                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                                    val sdf = SimpleDateFormat(CommonConstants.CapDateFormat, Locale.getDefault())
                                    etIssueDate.setText(sdf.format(calendar.time))
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }, cYear, cMonth, cDay)
                issueDatePickerDialog!!.datePicker.minDate = CommonUtilities.stringDateToLongFormatForMinDate(vehicleMinDate)
                issueDatePickerDialog!!.datePicker.maxDate = Date().time

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            if (!issueDatePickerDialog!!.isShowing) {
                issueDatePickerDialog!!.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openExpiryDatePickerDialog(context: Context) {
        try {
            if (expiryDatePickerDialog == null) {
                val calendar = Calendar.getInstance()
                val cYear = calendar.get(Calendar.YEAR)
                val cMonth = calendar.get(Calendar.MONTH)
                val cDay = calendar.get(Calendar.DAY_OF_MONTH)
                expiryDatePickerDialog =
                        DatePickerDialog(context, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                            try {
                                try {
                                    calendar.set(Calendar.YEAR, year)
                                    calendar.set(Calendar.MONTH, month)
                                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                                    val sdf = SimpleDateFormat(CommonConstants.CapDateFormat, Locale.getDefault())
                                    etExpiryDate.setText(sdf.format(calendar.time))
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }, cYear, cMonth, cDay)
                expiryDatePickerDialog!!.datePicker.minDate = Date().time
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            if (!expiryDatePickerDialog!!.isShowing) {
                expiryDatePickerDialog!!.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkValidation() {
        if (etCompany.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgInsuranceCompanyRequired)
        } else if (etPolicyType.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgInsurancePolicyTypeRequired)
        } else if (etPolicyNo.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgInsurancePolicyNoRequired)
        } else if (etIssueDate.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgInsuranceIssueDateRequired)
        } else if (etExpiryDate.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgInsuranceExpiryDateRequired)
        } else if (etPaymentMode.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgInsurancePaymentModeRequired)
        }  else if (etInsuranceAmount.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgInsuranceAmountRequired)
        } else if (etPremiumAmount.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgInsurancePremiumRequired)
        } else {
            if (CommonUtilities.isOnline(this)) {
                callSetInsuranceMasterService()
            } else {
                startActivity(Intent(this, NoInternetActivity::class.java))
            }
        }
    }

    private fun callSetInsuranceMasterService() {
        val aClass = InsuranceClass()
        try {
            aClass.insuranceId = insuranceId
            aClass.insuranceCompany = etCompany.text.toString()
            aClass.insurancePolicyType = etPolicyType.text.toString()
            aClass.insurancePolicyNo = etPolicyNo.text.toString()
            aClass.insuranceIssueDate = etIssueDate.text.toString()
            aClass.insuranceExpiryDate = etExpiryDate.text.toString()
            aClass.insurancePaymentMode = etPaymentMode.text.toString()
            aClass.insuranceAmount = etInsuranceAmount.text.toString()
            aClass.insurancePremium = etPremiumAmount.text.toString()
            aClass.insuranceAgentName = etAgentName.text.toString()
            aClass.insuranceAgentPhone = etAgentContactNo.text.toString()
            aClass.insuranceDescription = etDescription.text.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        SetInsuranceList(this, vehicleId, aClass, this@InsuranceDetailActivity).callSetInsuranceListService()
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
        etCompany.setText("")
        etPolicyType.setText("")
        etPolicyNo.setText("")
        if (insuranceId.isEmpty()) {
            etIssueDate.setText("")
            etExpiryDate.setText("")
        }
        etPaymentMode.setText("")
        etInsuranceAmount.setText("")
        etPremiumAmount.setText("")
        etAgentName.setText("")
        etAgentContactNo.setText("")
        etDescription.setText("")
    }

    override fun setInsuranceDetailUploadCallback(isSuccess: Boolean) {
        if (isSuccess) {
            isDataUpdated = true
            CommonUtilities.setResultAndFinish(this, isDataUpdated)
        }
    }

    override fun onBackPressed() {
        CommonUtilities.showAlertFinishOnClick(this, isDataUpdated)
    }
}