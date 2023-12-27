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
import com.ninetynineapps.vehiclemanager.interfaces.PermitListUploadCallback
import com.ninetynineapps.vehiclemanager.pojo.PermitClass
import com.ninetynineapps.vehiclemanager.services.SetPermitList
import kotlinx.android.synthetic.main.activity_accident_detail.*
import kotlinx.android.synthetic.main.activity_permit_detail.*
import kotlinx.android.synthetic.main.activity_permit_detail.etDescription
import kotlinx.android.synthetic.main.activity_permit_detail.imgBack
import kotlinx.android.synthetic.main.activity_permit_detail.llAdView
import kotlinx.android.synthetic.main.activity_permit_detail.llAdViewFacebook
import kotlinx.android.synthetic.main.activity_permit_detail.tvClear
import kotlinx.android.synthetic.main.activity_permit_detail.tvSave
import kotlinx.android.synthetic.main.activity_refuel_detail.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class PermitDetailActivity : AppCompatActivity(), PermitListUploadCallback, View.OnClickListener {

    private var vehicleId = ""
    private var vehicleMinDate = ""
    private var permitId = ""
    private var isDataUpdated = false

    private var issueDatePickerDialog: DatePickerDialog? = null
    private var expiryDatePickerDialog: DatePickerDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permit_detail)
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
        etPermitType.setOnClickListener(this)
        etIssueDate.setOnClickListener(this)
        etExpiryDate.setOnClickListener(this)

        tvSave.setOnClickListener(this)
        tvClear.setOnClickListener(this)
    }

    private fun loadDataFromIntent() {
        val intent = intent
        if (intent!!.hasExtra(CommonConstants.KeyVehicleId)) {
            try {
                vehicleId = intent.getStringExtra(CommonConstants.KeyVehicleId)!!
                vehicleMinDate = intent.getStringExtra(CommonConstants.KeyVehicleMinDate)!!
                if (intent.hasExtra(CommonConstants.KeyPermitDetail)) {
                    val aClass = intent.getSerializableExtra(CommonConstants.KeyPermitDetail) as PermitClass
                    permitId = aClass.permitId

                    etPermitType.setText(aClass.permitType)

                    etIssueDate.setText(aClass.permitIssueDate)
                    etExpiryDate.setText(aClass.permitExpiryDate)

                    try {
                        etPermitNo.setText(aClass.permitNo)
                        etPermitCost.setText(aClass.permitCost)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    etDescription.setText(aClass.permitDescription)
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
        } else if (id == R.id.etPermitType) {
            openPermitTypeDialog(p0)
        } else if (id == R.id.etIssueDate) {
            openIssueDatePickerDialog(this)
        } else if (id == R.id.etExpiryDate) {
            openExpiryDatePickerDialog(this)
        } else if (id == R.id.tvSave) {
            checkValidation()
        } else if (id == R.id.tvClear) {
            showClearDataConfirmDialog(this)
        }
    }

    private fun openPermitTypeDialog(view: View) {
        val menu = PopupMenu(this, view, Gravity.END)
        menu.setOnMenuItemClickListener { item ->
            etPermitType.setText(item.title.toString())
            false
        }
        menu.inflate(R.menu.menu_permit_type)
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
        if (etPermitType.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgPermitPolicyTypeRequired)
        } else if (etIssueDate.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgPermitIssueDateRequired)
        } else if (etExpiryDate.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgPermitExpiryDateRequired)
        } else if (etPermitNo.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgPermitNoRequired)
        } else if (etPermitCost.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgPermitCostRequired)
        } else {
            if (CommonUtilities.isOnline(this)) {
                callSetPermitMasterService()
            } else {
                startActivity(Intent(this, NoInternetActivity::class.java))
            }
        }
    }

    private fun callSetPermitMasterService() {
        val aClass = PermitClass()
        try {
            aClass.permitId = permitId
            aClass.permitType = etPermitType.text.toString()
            aClass.permitIssueDate= etIssueDate.text.toString()
            aClass.permitExpiryDate= etExpiryDate.text.toString()
            aClass.permitNo = etPermitNo.text.toString()
            aClass.permitCost = etPermitCost.text.toString()
            aClass.permitDescription= etDescription.text.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        SetPermitList(this, vehicleId, aClass, this@PermitDetailActivity).callSetPermitListService()
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
        etPermitType.setText("")
        if (permitId.isEmpty()) {
            etIssueDate.setText("")
            etExpiryDate.setText("")
        }
        etPermitNo.setText("")
        etPermitCost.setText("")
        etDescription.setText("")
    }

    override fun setPermitDetailUploadCallback(isSuccess: Boolean) {
        if (isSuccess) {
            isDataUpdated = true
            CommonUtilities.setResultAndFinish(this, isDataUpdated)
        }
    }

    override fun onBackPressed() {
        CommonUtilities.showAlertFinishOnClick(this, isDataUpdated)
    }

}

