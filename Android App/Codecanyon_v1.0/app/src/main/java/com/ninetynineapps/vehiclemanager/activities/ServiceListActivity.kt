package com.ninetynineapps.vehiclemanager.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import com.ninetynineapps.vehiclemanager.R
import com.ninetynineapps.vehiclemanager.adapters.ServiceListAdapter
import com.ninetynineapps.vehiclemanager.common.CommonConstantAd
import com.ninetynineapps.vehiclemanager.common.CommonConstants
import com.ninetynineapps.vehiclemanager.common.CommonUtilities
import com.ninetynineapps.vehiclemanager.interfaces.AdapterItemCallback
import com.ninetynineapps.vehiclemanager.interfaces.AdsCallback
import com.ninetynineapps.vehiclemanager.interfaces.ServiceListDownloadCallback
import com.ninetynineapps.vehiclemanager.pojo.ServiceClass
import com.ninetynineapps.vehiclemanager.pojo.VehicleClass
import com.ninetynineapps.vehiclemanager.services.GetServiceList
import kotlinx.android.synthetic.main.activity_accident_detail.*
import kotlinx.android.synthetic.main.activity_puc_list.*
import kotlinx.android.synthetic.main.activity_refuel_detail.*
import kotlinx.android.synthetic.main.activity_service_list.*
import kotlinx.android.synthetic.main.activity_service_list.imgAdd
import kotlinx.android.synthetic.main.activity_service_list.imgBack
import kotlinx.android.synthetic.main.activity_service_list.llAdView
import kotlinx.android.synthetic.main.activity_service_list.llAdViewFacebook
import kotlinx.android.synthetic.main.activity_service_list.llNoData
import java.util.*

class ServiceListActivity : AppCompatActivity(), View.OnClickListener, ServiceListDownloadCallback,
    AdapterItemCallback {

    private var serviceClassArrayList = ArrayList<ServiceClass>()
    private var vehicleId = ""
    private var vehicleMinDate = ""
    private var isDataUpdated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_list)
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
        rvServiceList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            this,
            androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
            false
        )

        imgBack.setOnClickListener(this)
        imgAdd.setOnClickListener(this)
    }

    private fun loadDataFromIntent() {
        val intent = intent
        if (intent!!.hasExtra(CommonConstants.KeyVehicleDetail)) {
            try {
                val aClass = intent.getSerializableExtra(CommonConstants.KeyVehicleDetail) as VehicleClass
                vehicleId = aClass.vehicleId
                vehicleMinDate = aClass.vehiclePurchaseDate
                GetServiceList(this, vehicleId, this@ServiceListActivity).callGetServiceListService()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun setServiceDetailDownloadCallback(serviceClassArrayList: ArrayList<ServiceClass>) {
        this.serviceClassArrayList = serviceClassArrayList
        if (serviceClassArrayList.size > 0) {
            rvServiceList.visibility = View.VISIBLE
            llNoData.visibility = View.GONE
            val mLayoutManager = LinearLayoutManager(this)
            mLayoutManager.reverseLayout = true
            mLayoutManager.stackFromEnd = true
            rvServiceList.layoutManager = mLayoutManager
            rvServiceList.adapter = ServiceListAdapter(this, serviceClassArrayList, this@ServiceListActivity)
        } else {
            rvServiceList.visibility = View.GONE
            llNoData.visibility = View.VISIBLE
        }
    }

    override fun onClick(p0: View?) {
        val id = p0!!.id
        if (id == R.id.imgBack) {
            onBackPressed()
        } else if (id == R.id.imgAdd) {
//            CommonUtilities.onClickAddVehicleDetail(this, this@ServiceListActivity)
            openServiceDetailActivity(null)
        }
    }

    override fun onItemTypeClickCallback(mPos: Int) {
        if (CommonUtilities.isOnline(this)) {
            try {
                val aClass = serviceClassArrayList[mPos]
                openServiceDetailActivity(aClass)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            startActivity(Intent(this, NoInternetActivity::class.java))
        }
    }

    /*override fun adLoadingFailed() {
        openServiceDetailActivity(null)
    }

    override fun adClose() {
        openServiceDetailActivity(null)
    }

    override fun startNextScreen() {
        openServiceDetailActivity(null)
    }*/

    private fun openServiceDetailActivity(aClass: ServiceClass?) {
        val intent = Intent(this, ServiceDetailActivity::class.java)
        intent.putExtra(CommonConstants.KeyVehicleId, vehicleId)
        intent.putExtra(CommonConstants.KeyVehicleMinDate, vehicleMinDate)
        if (aClass != null) {
            intent.putExtra(CommonConstants.KeyServiceDetail, aClass)
        }
        startActivityForResult(intent, CommonConstants.RequestDataUpdated)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val key = CommonConstants.KeyIsDataUpdated
        if (resultCode == Activity.RESULT_OK) {
            if (data!!.hasExtra(key)) {
                if (data.getBooleanExtra(key, false)) {
                    isDataUpdated = true
                    GetServiceList(this, vehicleId, this@ServiceListActivity).callGetServiceListService()
                }
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