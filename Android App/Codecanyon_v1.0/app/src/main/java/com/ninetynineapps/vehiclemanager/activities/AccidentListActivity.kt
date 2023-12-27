package com.ninetynineapps.vehiclemanager.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import com.ninetynineapps.vehiclemanager.R
import com.ninetynineapps.vehiclemanager.adapters.AccidentListAdapter
import com.ninetynineapps.vehiclemanager.common.CommonConstantAd
import com.ninetynineapps.vehiclemanager.common.CommonConstants
import com.ninetynineapps.vehiclemanager.common.CommonUtilities
import com.ninetynineapps.vehiclemanager.interfaces.AccidentListDownloadCallback
import com.ninetynineapps.vehiclemanager.interfaces.AdapterItemCallback
import com.ninetynineapps.vehiclemanager.interfaces.AdsCallback
import com.ninetynineapps.vehiclemanager.pojo.AccidentClass
import com.ninetynineapps.vehiclemanager.pojo.VehicleClass
import com.ninetynineapps.vehiclemanager.services.GetAccidentList
import kotlinx.android.synthetic.main.activity_accident_detail.*
import kotlinx.android.synthetic.main.activity_accident_list.*
import kotlinx.android.synthetic.main.activity_accident_list.imgAdd
import kotlinx.android.synthetic.main.activity_accident_list.imgBack
import kotlinx.android.synthetic.main.activity_accident_list.llAdView
import kotlinx.android.synthetic.main.activity_accident_list.llAdViewFacebook
import kotlinx.android.synthetic.main.activity_accident_list.llNoData

class AccidentListActivity : AppCompatActivity(), View.OnClickListener, AccidentListDownloadCallback,
    AdapterItemCallback {

    private var accidentClassArrayList = ArrayList<AccidentClass>()
    private var vehicleId = ""
    private var vehicleMinDate = ""
    private var isDataUpdated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accident_list)
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
        rvAccidentList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
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
                GetAccidentList(this, vehicleId, this@AccidentListActivity).callGetAccidentListService()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun setAccidentDetailDownloadCallback(accidentClassArrayList: ArrayList<AccidentClass>) {
        this.accidentClassArrayList = accidentClassArrayList
        if (accidentClassArrayList.size > 0) {
            rvAccidentList.visibility = View.VISIBLE
            llNoData.visibility = View.GONE
            val mLayoutManager = LinearLayoutManager(this)
            mLayoutManager.reverseLayout = true
            mLayoutManager.stackFromEnd = true
            rvAccidentList.layoutManager = mLayoutManager
            rvAccidentList.adapter = AccidentListAdapter(this, accidentClassArrayList, this@AccidentListActivity)
        } else {
            rvAccidentList.visibility = View.GONE
            llNoData.visibility = View.VISIBLE
        }
    }

    override fun onClick(p0: View?) {
        val id = p0!!.id
        if (id == R.id.imgBack) {
            onBackPressed()
        } else if (id == R.id.imgAdd) {
//            CommonUtilities.onClickAddVehicleDetail(this, this@AccidentListActivity)
            openAccidentDetailActivity(null)
        }
    }

    override fun onItemTypeClickCallback(mPos: Int) {
        if (CommonUtilities.isOnline(this)) {
            try {
                val aClass = accidentClassArrayList[mPos]
                openAccidentDetailActivity(aClass)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            startActivity(Intent(this, NoInternetActivity::class.java))
        }
    }


   /* override fun adLoadingFailed() {
        openAccidentDetailActivity(null)
    }

    override fun adClose() {
        openAccidentDetailActivity(null)
    }

    override fun startNextScreen() {
        openAccidentDetailActivity(null)
    }*/

    private fun openAccidentDetailActivity(aClass: AccidentClass?) {
        val intent = Intent(this, AccidentDetailActivity::class.java)
        intent.putExtra(CommonConstants.KeyVehicleId, vehicleId)
        intent.putExtra(CommonConstants.KeyVehicleMinDate, vehicleMinDate)
        if (aClass != null) {
            intent.putExtra(CommonConstants.KeyAccidentDetail, aClass)
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
                    GetAccidentList(this, vehicleId, this@AccidentListActivity).callGetAccidentListService()
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