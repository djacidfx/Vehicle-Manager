package com.ninetynineapps.vehiclemanager.activities

import android.app.Activity
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
import com.ninetynineapps.vehiclemanager.interfaces.AdsCallback
import com.ninetynineapps.vehiclemanager.interfaces.DeleteVehicleCallback
import com.ninetynineapps.vehiclemanager.interfaces.VehicleListDownloadCallback
import com.ninetynineapps.vehiclemanager.pojo.VehicleClass
import com.ninetynineapps.vehiclemanager.services.DeleteVehicleData
import com.ninetynineapps.vehiclemanager.services.GetVehicleData
import kotlinx.android.synthetic.main.activity_accident_detail.*
import kotlinx.android.synthetic.main.activity_refuel_detail.*
import kotlinx.android.synthetic.main.activity_vehicle_categories.*
import kotlinx.android.synthetic.main.activity_vehicle_categories.imgBack
import kotlinx.android.synthetic.main.activity_vehicle_categories.imgOptionMenu
import kotlinx.android.synthetic.main.activity_vehicle_categories.llAdView
import kotlinx.android.synthetic.main.activity_vehicle_categories.llAdViewFacebook
import java.util.ArrayList

class VehicleCategories : AppCompatActivity(), View.OnClickListener, VehicleListDownloadCallback,
    DeleteVehicleCallback {

    private var aClass: VehicleClass? = null
    private var isDataUpdated = false
    private var isEditClick = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_categories)

        initViews()
        loadDataFromIntent()

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
        imgOptionMenu.setOnClickListener(this)

        llRefuel.setOnClickListener(this)
        llService.setOnClickListener(this)
        llExpense.setOnClickListener(this)
        llInsurance.setOnClickListener(this)

        llPermit.setOnClickListener(this)
        llPUC.setOnClickListener(this)
        llAccident.setOnClickListener(this)
        llSummary.setOnClickListener(this)
    }

    private fun loadDataFromIntent() {
        val intent = intent
        if (intent!!.hasExtra(CommonConstants.KeyVehicleDetail)) {
            try {
                aClass = intent.getSerializableExtra(CommonConstants.KeyVehicleDetail) as VehicleClass
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onClick(p0: View?) {
        val id = p0!!.id
        if (id == R.id.imgBack) {
            onBackPressed()
        } else if (id == R.id.imgOptionMenu) {
            openEditDeleteMenu(p0)
        } else {
            if (CommonUtilities.isOnline(this)) {
                if (id == R.id.llRefuel) {
                    openNextActivity(Intent(this, RefuelListActivity::class.java))
                } else if (id == R.id.llService) {
                    openNextActivity(Intent(this, ServiceListActivity::class.java))
                } else if (id == R.id.llExpense) {
                    openNextActivity(Intent(this, ExpenseListActivity::class.java))
                } else if (id == R.id.llInsurance) {
                    openNextActivity(Intent(this, InsuranceListActivity::class.java))
                } else if (id == R.id.llPermit) {
                    openNextActivity(Intent(this, PermitListActivity::class.java))
                } else if (id == R.id.llPUC) {
                    openNextActivity(Intent(this, PUCListActivity::class.java))
                } else if (id == R.id.llAccident) {
                    openNextActivity(Intent(this, AccidentListActivity::class.java))
                } else if (id == R.id.llSummary) {
                    openNextActivity(Intent(this, SummaryActivity::class.java))
                }
            } else {
                startActivity(Intent(this, NoInternetActivity::class.java))
            }
        }
    }

    override fun setVehicleDetailDownloadCallback(vehicleClassArrayList: ArrayList<VehicleClass>) {
        if (vehicleClassArrayList.size > 0) {
            aClass = vehicleClassArrayList[0]
        }
    }

    private fun openEditDeleteMenu(view: View) {
        val menu = PopupMenu(this, view, Gravity.END)
        menu.setOnMenuItemClickListener { item ->
            val id = item.itemId
            if (id == R.id.editData) {
                isEditClick = true
//                CommonUtilities.initFullAdd(this, this@VehicleCategories)
                if (isEditClick) {
                    openNextActivity(Intent(this, VehicleDetailActivity::class.java))
                } else {
                    onBackPressed()
                }
            } else if (id == R.id.deleteData) {
                isEditClick = false
                openDeleteConfirmDialog(this)
            }
            false
        }
        menu.inflate(R.menu.menu_edit_del_options)
        menu.show()
    }

    private fun openNextActivity(intent: Intent) {
        intent.putExtra(CommonConstants.KeyVehicleDetail, aClass)
        startActivityForResult(intent, CommonConstants.RequestDataUpdated)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val key = CommonConstants.KeyIsDataUpdated
        if (resultCode == Activity.RESULT_OK) {
            if (data!!.hasExtra(key)) {
                if (data.getBooleanExtra(key, false)) {
                    isDataUpdated = true
                    GetVehicleData(this, aClass!!.vehicleId,this@VehicleCategories).callGetVehicleDataService()
                }
            }
        }
    }

   /* override fun adLoadingFailed() {
        if (isEditClick) {
            openNextActivity(Intent(this, VehicleDetailActivity::class.java))
        } else {
            onBackPressed()
        }
    }

    override fun adClose() {
        if (isEditClick) {
            openNextActivity(Intent(this, VehicleDetailActivity::class.java))
        } else {
            onBackPressed()
        }
    }

    override fun startNextScreen() {
        if (isEditClick) {
            openNextActivity(Intent(this, VehicleDetailActivity::class.java))
        } else {
            onBackPressed()
        }
    }*/

    private fun openDeleteConfirmDialog(context: Context) {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle(CommonConstants.CapConfirm)
        alertDialog.setMessage(CommonConstants.MsgDoYouWantToDeleteItem)
        alertDialog.setPositiveButton(CommonConstants.CapDelete) { dialog, _ ->
            dialog.dismiss()
            if (CommonUtilities.isOnline(this)) {
                DeleteVehicleData(this, aClass!!.vehicleId, this@VehicleCategories).callDeleteVehicleDataService()
            } else {
                startActivity(Intent(this, NoInternetActivity::class.java))
            }
        }
        alertDialog.setNegativeButton(CommonConstants.CapCancel) { dialog, _ -> dialog.dismiss() }
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    override fun setDeleteVehicleCallback(isSuccess: Boolean) {
        if (isSuccess) {
            isDataUpdated = true
//            CommonUtilities.onClickDeleteVehicle(this,this@VehicleCategories)
            if (isEditClick) {
                openNextActivity(Intent(this, VehicleDetailActivity::class.java))
            } else {
                onBackPressed()
            }
        }
    }

    override fun onBackPressed() {
        if (isDataUpdated) {
            val intent = Intent()
            intent.putExtra(CommonConstants.KeyIsDataUpdated, true)
            setResult(Activity.RESULT_OK, intent)
        }
        finish()
    }
}