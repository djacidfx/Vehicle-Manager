package com.ninetynineapps.vehiclemanager.activities

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import com.ninetynineapps.vehiclemanager.R
import com.ninetynineapps.vehiclemanager.common.CommonConstants
import com.ninetynineapps.vehiclemanager.common.CommonUtilities
import kotlinx.android.synthetic.main.activity_no_internet.*

class NoInternetActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_internet)

        initViews()
    }

    private fun initViews() {
        tvRetry.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.tvRetry) {
            checkInternet()
        }
    }

    private fun checkInternet() {
        showProgress()
        Handler().postDelayed({
            hideProgress()
            if (CommonUtilities.isOnline(this)) {
                finish()
            }
        }, 1500)
    }

    private var pDialog: ProgressDialog? = null

    private fun showProgress() {
        try {
            pDialog = ProgressDialog(this)
            pDialog!!.setMessage(CommonConstants.CapPleaseWait)
            pDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            pDialog!!.setCancelable(false)
            pDialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun hideProgress() {
        try {
            if (pDialog != null && pDialog!!.isShowing) {
                pDialog!!.dismiss()
            }
        } catch (e: Exception) {
        }
    }

    override fun onBackPressed() {
        Log.e("<><>","onBackPressed")
    }
}
