package com.ninetynineapps.vehiclemanager.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.CompoundButton
import com.ninetynineapps.vehiclemanager.R
import com.ninetynineapps.vehiclemanager.common.CommonConstantAd
import com.ninetynineapps.vehiclemanager.common.CommonConstants
import com.ninetynineapps.vehiclemanager.common.CommonUtilities
import com.ninetynineapps.vehiclemanager.interfaces.SummaryDownloadCallback
import com.ninetynineapps.vehiclemanager.pojo.SummaryClass
import com.ninetynineapps.vehiclemanager.pojo.VehicleClass
import com.ninetynineapps.vehiclemanager.services.GetSummaryList
import kotlinx.android.synthetic.main.activity_accident_detail.*
import kotlinx.android.synthetic.main.activity_refuel_detail.*
import kotlinx.android.synthetic.main.activity_summary.*
import kotlinx.android.synthetic.main.activity_summary.imgBack
import kotlinx.android.synthetic.main.activity_summary.llAdView
import kotlinx.android.synthetic.main.activity_summary.llAdViewFacebook

class SummaryActivity : AppCompatActivity(), SummaryDownloadCallback, View.OnClickListener,
    CompoundButton.OnCheckedChangeListener {

    private var summaryClassArrayList = ArrayList<SummaryClass>()
    private var vehicleId = ""
    private var isDataUpdated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

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
        chkIncludePurchasePrice.setOnCheckedChangeListener(this)
    }

    private fun loadDataFromIntent() {
        val intent = intent
        if (intent!!.hasExtra(CommonConstants.KeyVehicleDetail)) {
            try {
                val aClass = intent.getSerializableExtra(CommonConstants.KeyVehicleDetail) as VehicleClass
                vehicleId = aClass.vehicleId
                GetSummaryList(this, vehicleId, this@SummaryActivity).callGetSummaryListService()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun setSummaryDetailDownloadCallback(summaryClassArrayList: ArrayList<SummaryClass>) {
        this.summaryClassArrayList = summaryClassArrayList
        try {
            if (summaryClassArrayList.size > 0) {
                val aClass = summaryClassArrayList[0]
                tvTotalExpenditurePrice.text = aClass.totalExpenseWithoutPrice
                tvPurchasePrice.text = aClass.vehiclePurchasePrice
                tvRefuelPrice.text = aClass.refuelAmount
                tvServicePrice.text = aClass.serviceAmount
                tvExpensePrice.text = aClass.expenseDetailAmount
                tvInsurancePrice.text = aClass.insuranceAmount
                tvPermitPrice.text = aClass.permitCost
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(p0: View?) {
        val id = p0!!.id
        if (id == R.id.imgBack) {
            CommonUtilities.showAlertFinishOnClick(this, isDataUpdated)
        }
    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        try {
            if (summaryClassArrayList.size > 0) {
                val aClass = summaryClassArrayList[0]
                if (p1) {
                    tvTotalExpenditurePrice.text = aClass.totalExpenseWithPrice
                } else {
                    tvTotalExpenditurePrice.text = aClass.totalExpenseWithoutPrice
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
