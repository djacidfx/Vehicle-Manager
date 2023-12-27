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
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import com.ninetynineapps.vehiclemanager.R
import com.ninetynineapps.vehiclemanager.common.CommonConstantAd
import com.ninetynineapps.vehiclemanager.common.CommonConstants
import com.ninetynineapps.vehiclemanager.common.CommonUtilities
import com.ninetynineapps.vehiclemanager.common.DecimalDigitsInputFilter
import com.ninetynineapps.vehiclemanager.interfaces.ExpenseListUploadCallback
import com.ninetynineapps.vehiclemanager.pojo.ExpenseClass
import com.ninetynineapps.vehiclemanager.services.SetExpenseList
import kotlinx.android.synthetic.main.activity_accident_detail.*
import kotlinx.android.synthetic.main.activity_expense_detail.*
import kotlinx.android.synthetic.main.activity_expense_detail.etDate
import kotlinx.android.synthetic.main.activity_expense_detail.etDescription
import kotlinx.android.synthetic.main.activity_expense_detail.etKmReading
import kotlinx.android.synthetic.main.activity_expense_detail.etTotalAmount
import kotlinx.android.synthetic.main.activity_expense_detail.imgBack
import kotlinx.android.synthetic.main.activity_expense_detail.llAdView
import kotlinx.android.synthetic.main.activity_expense_detail.llAdViewFacebook
import kotlinx.android.synthetic.main.activity_expense_detail.tvClear
import kotlinx.android.synthetic.main.activity_expense_detail.tvSave
import kotlinx.android.synthetic.main.activity_refuel_detail.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class ExpenseDetailActivity : AppCompatActivity(), View.OnClickListener, ExpenseListUploadCallback {

    private var vehicleId = ""
    private var vehicleMinDate = ""
    private var expenseId = ""
    private var isDataUpdated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_detail)
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

        etExpenseType.setOnClickListener(this)

        tvSave.setOnClickListener(this)
        tvClear.setOnClickListener(this)
    }

    private fun loadDataFromIntent() {
        val intent = intent
        if (intent!!.hasExtra(CommonConstants.KeyVehicleId)) {
            try {
                vehicleId = intent.getStringExtra(CommonConstants.KeyVehicleId)!!
                vehicleMinDate = intent.getStringExtra(CommonConstants.KeyVehicleMinDate)!!
                if (intent.hasExtra(CommonConstants.KeyExpenseDetail)) {
                    val aClass = intent.getSerializableExtra(CommonConstants.KeyExpenseDetail) as ExpenseClass
                    expenseId = aClass.expenseId

                    etDate.setText(aClass.expenseDate)
                    etExpenseType.setText(aClass.expenseType)

                    try {
                        etTotalAmount.setText(aClass.expenseAmount)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    etKmReading.setText(aClass.expenseKmReading)
                    etDescription.setText(aClass.expenseDescription)
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
        }  else if (id == R.id.etExpenseType) {
            openExpenseTypeMenu(p0)
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

    private fun openExpenseTypeMenu(view: View) {
        val menu = PopupMenu(this, view, Gravity.END)
        menu.setOnMenuItemClickListener { item ->
            etExpenseType.setText(item.title.toString())
            false
        }
        menu.inflate(R.menu.menu_expense_type)
        menu.show()
    }

    private fun checkValidation() {
        if (etDate.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgExpenseDateRequired)
        } else if (etExpenseType.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgExpenseTypeRequired)
        } else if (etTotalAmount.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgExpenseTotalAmountRequired)
        } else {
            if (CommonUtilities.isOnline(this)) {
                callSetExpenseMasterService()
            } else {
                startActivity(Intent(this, NoInternetActivity::class.java))
            }
        }
    }

    private fun callSetExpenseMasterService() {
        val aClass = ExpenseClass()
        try {
            aClass.expenseId = expenseId
            aClass.expenseDate = etDate.text.toString()
            aClass.expenseType = etExpenseType.text.toString()
            aClass.expenseAmount = etTotalAmount.text.toString()
            aClass.expenseKmReading = etKmReading.text.toString()
            aClass.expenseDescription = etDescription.text.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        SetExpenseList(this, vehicleId, aClass, this@ExpenseDetailActivity).callSetExpenseListService()
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
        if (expenseId.isEmpty()) {
            etDate.setText("")
        }

        etTotalAmount.setText("")
        etKmReading.setText("")
        etDescription.setText("")
    }


    override fun setExpenseDetailUploadCallback(isSuccess: Boolean) {
        if (isSuccess) {
            isDataUpdated = true
            CommonUtilities.setResultAndFinish(this, isDataUpdated)
        }
    }

    override fun onBackPressed() {
        CommonUtilities.showAlertFinishOnClick(this, isDataUpdated)
    }
}