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
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import com.google.gson.Gson
import com.ninetynineapps.vehiclemanager.R
import com.ninetynineapps.vehiclemanager.common.CommonConstantAd
import com.ninetynineapps.vehiclemanager.common.CommonConstants
import com.ninetynineapps.vehiclemanager.common.CommonUtilities
import com.ninetynineapps.vehiclemanager.common.DecimalDigitsInputFilter
import com.ninetynineapps.vehiclemanager.interfaces.AdsCallback
import com.ninetynineapps.vehiclemanager.interfaces.VehicleListUploadCallback
import com.ninetynineapps.vehiclemanager.pojo.VehicleClass
import com.ninetynineapps.vehiclemanager.services.SetVehicleList
import kotlinx.android.synthetic.main.activity_accident_detail.*
import kotlinx.android.synthetic.main.activity_refuel_detail.*
import kotlinx.android.synthetic.main.activity_vehicle_detail.*
import kotlinx.android.synthetic.main.activity_vehicle_detail.etDate
import kotlinx.android.synthetic.main.activity_vehicle_detail.etFuelType
import kotlinx.android.synthetic.main.activity_vehicle_detail.etKmReading
import kotlinx.android.synthetic.main.activity_vehicle_detail.imgBack
import kotlinx.android.synthetic.main.activity_vehicle_detail.llAdView
import kotlinx.android.synthetic.main.activity_vehicle_detail.llAdViewFacebook
import kotlinx.android.synthetic.main.activity_vehicle_detail.tvClear
import kotlinx.android.synthetic.main.activity_vehicle_detail.tvSave
import java.text.SimpleDateFormat
import java.util.*

class VehicleDetailActivity : AppCompatActivity(), View.OnClickListener, VehicleListUploadCallback, AdsCallback {

    private var vehicleId = ""
    private var isDataUpdated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_detail)
        initViews()
        loadDataFromIntent()

        if (CommonUtilities.getPref(this,CommonConstants.AD_TYPE_FB_GOOGLE,"") == CommonConstants.AD_GOOGLE
            && CommonUtilities.getPref(this,CommonConstants.STATUS_ENABLE_DISABLE,"") == CommonConstants.ENABLE) {
            CommonConstantAd.GooglebeforloadAd(this)
        } else if (CommonUtilities.getPref(this,CommonConstants.AD_TYPE_FB_GOOGLE,"") == CommonConstants.AD_FACEBOOK
            && CommonUtilities.getPref(this,CommonConstants.STATUS_ENABLE_DISABLE,"") == CommonConstants.ENABLE) {
            CommonConstantAd.FacebookbeforeloadFullAd(this)
        }


//        CommonUtilities.loadBannerAd(findViewById(R.id.adViewBottom))
        if (CommonUtilities.getPref(this, CommonConstants.AD_TYPE_FB_GOOGLE, "") == CommonConstants.AD_GOOGLE &&
            CommonUtilities.getPref(this, CommonConstants.STATUS_ENABLE_DISABLE, "") == CommonConstants.ENABLE
        ) {
            CommonConstantAd.loadBannerGoogleAd(this, llAdView)
            llAdViewFacebook.visibility = View.GONE
            llAdView.visibility = View.VISIBLE
        } else if (CommonUtilities.getPref(this, CommonConstants.AD_TYPE_FB_GOOGLE, "") == CommonConstants.AD_FACEBOOK
            && CommonUtilities.getPref(this, CommonConstants.STATUS_ENABLE_DISABLE, "") == CommonConstants.ENABLE
        ) {
            CommonConstantAd.loadBannerFacebookAd(this, llAdViewFacebook)
            llAdViewFacebook.visibility = View.VISIBLE
            llAdView.visibility = View.GONE
        } else {
            llAdView.visibility = View.GONE
            llAdViewFacebook.visibility = View.GONE
        }
        CommonUtilities.addKeyboardDetectListener(this,llAdView,llAdViewFacebook)
    }

    private fun initViews() {
        imgBack.setOnClickListener(this)
        tvType.setOnClickListener(this)

        etTitle.setOnClickListener(this)
        etFuelType.setOnClickListener(this)

        etBuildYear.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val calendar = Calendar.getInstance()
                val cYear = calendar.get(Calendar.YEAR)
                val data = p0!!.toString()
                if (data.isNotEmpty() && data.toInt() <= cYear) {
                    etDisplayName.setText(data.plus(" - ").plus(etRegistrationNo.text.toString()))
                } else {
                    etDisplayName.setText("")
                }
            }

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }
        })

        etRegistrationNo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val data = p0!!.toString()
                etDisplayName.setText(etBuildYear.text.toString().plus(" - ").plus(data))
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

        etDate.setOnClickListener(this)
        tvSave.setOnClickListener(this)
        tvClear.setOnClickListener(this)
    }

    private fun loadDataFromIntent() {
        val intent = intent
        if (intent!!.hasExtra(CommonConstants.KeyVehicleDetail)) {
            try {
                val aClass = intent.getSerializableExtra(CommonConstants.KeyVehicleDetail) as VehicleClass

                vehicleId = aClass.vehicleId
                tvType.text = aClass.vehicleType
                etTitle.setText(aClass.vehicleTitle)
                etBrand.setText(aClass.vehicleBrand)
                etModel.setText(aClass.vehicleModel)

                etBuildYear.setText(aClass.vehicleBuildYear)
                etRegistrationNo.setText(aClass.vehicleRegistrationNo)
                etFuelType.setText(aClass.vehicleFuelType)

                etTankCapacity.setText(aClass.vehicleTankCapacity)
                etDisplayName.setText(aClass.vehicleDisplayName)
                etDate.setText(aClass.vehiclePurchaseDate)

                etPurchasePrice.setText(aClass.vehiclePurchasePrice)
                etKmReading.setText(aClass.vehicleKmReading)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onClick(p0: View?) {
        val id = p0!!.id
        if (id == R.id.imgBack) {
            CommonUtilities.showAlertFinishOnClick(this, isDataUpdated)
        } else if (id == R.id.tvType) {
            openVehicleTypeMenu(p0)
        } else if (id == R.id.etTitle) {
            openVehicleTitleMenu(p0)
        } else if (id == R.id.etFuelType) {
            openFuelTypeMenu(p0)
        } else if (id == R.id.etDate) {
            openDatePickerDialog(this)
        } else if (id == R.id.tvSave) {
            checkValidation()
        } else if (id == R.id.tvClear) {
            showClearDataConfirmDialog(this)
        }
    }

    private fun openVehicleTypeMenu(view: View) {
        val menu = PopupMenu(this, view, Gravity.END)
        menu.setOnMenuItemClickListener { item ->
            tvType.text = item.title.toString()
            false
        }
        menu.inflate(R.menu.menu_vehicle_type)
        menu.show()
    }

    private fun openVehicleTitleMenu(view: View) {
        val menu = PopupMenu(this, view, Gravity.END)
        menu.setOnMenuItemClickListener { item ->
            etTitle.setText(item.title.toString())
            false
        }
        menu.inflate(R.menu.menu_vehicle_title)
        menu.show()
    }

    private fun openFuelTypeMenu(view: View) {
        val menu = PopupMenu(this, view, Gravity.END)
        menu.setOnMenuItemClickListener { item ->
            etFuelType.setText(item.title.toString())
            false
        }
        menu.inflate(R.menu.menu_fuel_type)
        menu.show()
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
        if (etTitle.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgVehicleTypeRequired)
        } else if (etBrand.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgVehicleBrandRequired)
        } else if (etModel.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgVehicleModelRequired)
        } else if (etBuildYear.text.toString().isEmpty() || etBuildYear.text.toString().length != 4) {
            CommonUtilities.showToast(this, CommonConstants.MsgVehicleBuildYearRequired)
        } else if (etBuildYear.text.toString().length != 4) {
            CommonUtilities.showToast(this, CommonConstants.MsgEnterCorrectBuildYear)
        } else if (etRegistrationNo.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgVehicleRegNoRequired)
        } else if (etFuelType.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgVehicleFuelTypeRequired)
        } else if (etPurchasePrice.text.toString().isEmpty()) {
            CommonUtilities.showToast(this, CommonConstants.MsgVehiclePurchasePriceRequired)
        } else {
            if (CommonUtilities.isOnline(this)) {
//                callSetVehicleMasterService()
                showFullAd()
            } else {
                startActivity(Intent(this, NoInternetActivity::class.java))
            }
        }
    }

    private fun showFullAd() {
        if (CommonUtilities.getPref(this, CommonConstants.EXIT_BTN_COUNT, 1) == 1) {
            if (CommonUtilities.getPref(this, CommonConstants.AD_TYPE_FB_GOOGLE, "") == CommonConstants.AD_GOOGLE
                && CommonUtilities.getPref(this, CommonConstants.STATUS_ENABLE_DISABLE, "") == CommonConstants.ENABLE
            ) {
                CommonConstantAd.showInterstitialAdsGoogle(this,this)
            } else if (CommonUtilities.getPref(this, CommonConstants.AD_TYPE_FB_GOOGLE, "") == CommonConstants.AD_FACEBOOK
                && CommonUtilities.getPref(this, CommonConstants.STATUS_ENABLE_DISABLE, "") == CommonConstants.ENABLE
            ) {
                CommonConstantAd.showInterstitialAdsFacebook(this)
            } else {
                callSetVehicleMasterService()
            }
            CommonUtilities.setPref(this, CommonConstants.EXIT_BTN_COUNT, 2)
        } else {
            CommonUtilities.setPref(this, CommonConstants.EXIT_BTN_COUNT, 1)
            callSetVehicleMasterService()
        }
    }

    private fun callSetVehicleMasterService() {
        val aClass = VehicleClass()
        try {
            aClass.deviceId = CommonUtilities.getAndroidId(this)
            aClass.vehicleId = vehicleId
            aClass.vehicleType = tvType.text.toString()
            aClass.vehicleTitle = etTitle.text.toString()
            aClass.vehicleBrand = etBrand.text.toString()
            aClass.vehicleModel = etModel.text.toString()
            aClass.vehicleBuildYear = etBuildYear.text.toString()
            aClass.vehicleRegistrationNo = etRegistrationNo.text.toString()
            aClass.vehicleFuelType = etFuelType.text.toString()
            aClass.vehicleTankCapacity = etTankCapacity.text.toString()
            aClass.vehicleDisplayName = etDisplayName.text.toString()
            aClass.vehiclePurchaseDate = etDate.text.toString()
            aClass.vehiclePurchasePrice = etPurchasePrice.text.toString()
            aClass.vehicleKmReading = etKmReading.text.toString()
            Log.e("TAG", "callSetVehicleMasterService:All Data::::::  " + Gson().toJson(aClass))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        SetVehicleList(this, aClass, this@VehicleDetailActivity).callSetVehicleListService()
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

    override fun setVehicleDetailUploadCallback(isSuccess: Boolean) {
        if (isSuccess) {
            isDataUpdated = true
            CommonUtilities.setResultAndFinish(this, isDataUpdated)
        }
    }

    private fun clearAllData() {
        tvType.text = resources.getString(R.string.vehicle_condition_old)
        etTitle.setText("")
        etBrand.setText("")
        etModel.setText("")

        etBuildYear.setText("")
        etRegistrationNo.setText("")
        etFuelType.setText("")

        etTankCapacity.setText("")
        etDisplayName.setText("")
        etDate.setText("")

        etPurchasePrice.setText("")
        etKmReading.setText("")
    }

    override fun onBackPressed() {
        CommonUtilities.showAlertFinishOnClick(this, isDataUpdated)
    }



    override fun startNextScreen() {
        callSetVehicleMasterService()
    }

    override fun onLoaded() {

    }

}


/*
        fun popupWindowDogs(): PopupWindow {
                val popupWindow = PopupWindow(this)
                val listViewDogs = ListView(this)
                listViewDogs.adapter = dogsAdapter(popUpContents)
                listViewDogs.onItemClickListener = DogsDropdownOnItemClickListener()
                popupWindow.isFocusable = true
                popupWindow.width = 250
                popupWindow.height = WindowManager.LayoutParams.WRAP_CONTENT
                popupWindow.contentView = listViewDogs
                return popupWindow
         }

        private fun dogsAdapter(dogsArray: Array<String>): ArrayAdapter<String> {
            return object : ArrayAdapter<String>(this, R.layout.custom_spinner_layout, dogsArray) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

                    val item = getItem(position)
                    val itemArr = item!!.split("::".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val text = itemArr[0]
                    val id = itemArr[1]

                    val listItem = TextView(this@VehicleDetailActivity)
                    listItem.text = text
                    listItem.tag = id
                    listItem.textSize = 22f
                    listItem.setPadding(10, 10, 10, 10)
                    listItem.setTextColor(Color.WHITE)
                    return listItem
                }
            }
        }

    inner class DogsDropdownOnItemClickListener : OnItemClickListener {

        override fun onItemClick(arg0: AdapterView<*>, v: View, arg2: Int, arg3: Long) {

            val mContext = v.context
            val vdActivity = mContext as VehicleDetailActivity
            val fadeInAnimation = AnimationUtils.loadAnimation(v.context, android.R.anim.fade_in)
            fadeInAnimation.duration = 10
            v.startAnimation(fadeInAnimation)
            vdActivity.popupWindowDogs!!.dismiss()
            val selectedItemText = (v as TextView).text.toString()
            vdActivity.buttonShowDropDown.setText(selectedItemText)
            val selectedItemTag = v.tag.toString()
            Toast.makeText(mContext, "Dog ID is: $selectedItemTag", Toast.LENGTH_SHORT).show()
        }
    }






















    //    private var mDropdown: PopupWindow? = null
//    private fun initiatePopupWindow(view: View): PopupWindow {
//        try {
//            val mInflater: LayoutInflater = applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//            val layout = mInflater.inflate(R.layout.custom_spinner_layout, null)
//            val itema = layout.findViewById(R.id.ItemA) as TextView
//            val itemb = layout.findViewById(R.id.ItemB) as TextView
//            layout.measure(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            )
//            val margin = resources.getDimension(R.dimen.twenty_dp).toInt()
//            val linearLayout = LinearLayout(this)
//            val layoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            )
//            val layoutParams = layout.layoutParams as LinearLayout.LayoutParams
//            layoutParams.setMargins(margin, 0, margin, 0)
//            layout.layoutParams = layoutParams
//            layout.setPadding(margin, 0, margin, 0)
//            layout.minimumWidth = layout.width-100
//            layout.minimumHeight = layout.height-100
//            mDropdown = PopupWindow(
//                layout,
//                FrameLayout.LayoutParams.MATCH_PARENT,
//                FrameLayout.LayoutParams.WRAP_CONTENT,
//                true
//            )
//            val background = resources.getDrawable(android.R.drawable.editbox_dropdown_dark_frame)
//            mDropdown!!.setBackgroundDrawable(background)
//            mDropdown!!.showAsDropDown(view, 10, 10)
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return mDropdown!!
//    }
*/