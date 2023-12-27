package com.ninetynineapps.vehiclemanager.activities

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.ninetynineapps.vehiclemanager.R
import com.ninetynineapps.vehiclemanager.adapters.VehicleListAdapter
import com.ninetynineapps.vehiclemanager.common.CommonConstantAd
import com.ninetynineapps.vehiclemanager.common.CommonConstants
import com.ninetynineapps.vehiclemanager.common.CommonUtilities
import com.ninetynineapps.vehiclemanager.interfaces.AdapterItemCallback
import com.ninetynineapps.vehiclemanager.interfaces.VehicleListDownloadCallback
import com.ninetynineapps.vehiclemanager.notifications.AlarmReceiver
import com.ninetynineapps.vehiclemanager.pojo.VehicleClass
import com.ninetynineapps.vehiclemanager.services.GetVehicleList
import kotlinx.android.synthetic.main.activity_accident_detail.*
import kotlinx.android.synthetic.main.activity_accident_list.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.imgAdd
import kotlinx.android.synthetic.main.activity_main.llAdView
import kotlinx.android.synthetic.main.activity_main.llAdViewFacebook
import kotlinx.android.synthetic.main.activity_main.llNoData
import kotlinx.android.synthetic.main.activity_refuel_detail.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), View.OnClickListener, VehicleListDownloadCallback,
    AdapterItemCallback {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var vehicleClassArrayList = ArrayList<VehicleClass>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        checkForNotification()
        //subScribeToFirebaseTopic()
        callGetAdsId()
        val emailId = CommonUtilities.getEmailIdFromPref(this)
        if (emailId != null && emailId.isNotEmpty()) {
            llSignOutMenu.visibility = View.VISIBLE
            tvLogin.visibility = View.GONE
            tvMsg.visibility = View.GONE

            tvEmailId.visibility = View.VISIBLE
            tvEmailId.text = emailId

            GetVehicleList(this, this@MainActivity).callGetVehicleListService()
        } else {
            llSignOutMenu.visibility = View.GONE
            tvLogin.visibility = View.VISIBLE
            tvMsg.visibility = View.VISIBLE

            tvEmailId.visibility = View.GONE
            tvEmailId.text = ""

            googleSignIn()
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

        CommonUtilities.addKeyboardDetectListener(this, llAdView, llAdViewFacebook)
    }

    private fun initViews() {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        vehicleClassArrayList = ArrayList()

        rvCategoryList.layoutManager = LinearLayoutManager(
            this,
            androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
            false
        )

        imgToggle.setOnClickListener(this)
        imgAdd.setOnClickListener(this)
        tvLogin.setOnClickListener(this)

        llHomeMenu.setOnClickListener(this)
        llContactMenu.setOnClickListener(this)
        llRateMenu.setOnClickListener(this)
        llShareMenu.setOnClickListener(this)
        llMoreMenu.setOnClickListener(this)
        llSignOutMenu.setOnClickListener(this)
        llExitMenu.setOnClickListener(this)
    }

    private fun checkForNotification() {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        if (!pref.getBoolean(CommonConstants.KeyIsFirstTime, false)) {
            setAlarmManager()
            val editor = pref.edit()
            editor.putBoolean(CommonConstants.KeyIsFirstTime, true)
            editor.apply()
        }
    }

    override fun onClick(view: View) {
        val id = view.id
        when (id) {
            R.id.imgToggle -> openDrawer()
            R.id.imgAdd -> {
                val emailId = CommonUtilities.getEmailIdFromPref(this)
                if (emailId != null && emailId.isNotEmpty()) {
//                    CommonUtilities.initFullAdd(this, this@MainActivity)
                    openVehicleDetailsActivity()
                } else {
                    googleSignIn()
                }
            }
            R.id.tvLogin -> googleSignIn()
            R.id.llHomeMenu -> closeDrawer()
            R.id.llContactMenu -> contactUs()
            R.id.llRateMenu -> rateUs()
            R.id.llShareMenu -> shareAppLink()
            R.id.llSignOutMenu -> googleSignOut()
            R.id.llExitMenu -> confirmationDialog(this, getString(R.string.exit_confirmation))
        }
    }


    private fun googleSignIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, CommonConstants.RC_GOOGLE_SIGN_IN)
    }

    private fun googleSignOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this) {

            CommonUtilities.setEmailIdInPref(this, "")
            CommonUtilities.showToast(this@MainActivity, CommonConstants.MsgYouAreSignOut)

            llSignOutMenu.visibility = View.GONE
            tvLogin.visibility = View.VISIBLE
            tvMsg.visibility = View.VISIBLE

            tvEmailId.visibility = View.GONE
            tvEmailId.text = ""

            closeDrawer()
            vehicleClassArrayList.clear()
            vehicleClassArrayList = ArrayList()
            setVehicleDetailDownloadCallback(vehicleClassArrayList)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                val emailId = account.email
                if (emailId != null) {
                    CommonUtilities.setEmailIdInPref(this, emailId)
                    CommonUtilities.showToast(this, CommonConstants.MsgSignInSuccessfully)

                    llSignOutMenu.visibility = View.VISIBLE
                    tvLogin.visibility = View.GONE
                    tvMsg.visibility = View.GONE

                    tvEmailId.visibility = View.VISIBLE
                    tvEmailId.text = emailId

                    GetVehicleList(this, this@MainActivity).callGetVehicleListService()
                } else {
                    CommonUtilities.showToast(this, CommonConstants.MsgSomethingWrong)
                }
            } else {
                CommonUtilities.showToast(this, CommonConstants.MsgSomethingWrong)
            }
        } catch (e: ApiException) {
            e.printStackTrace()
        }
    }

    override fun setVehicleDetailDownloadCallback(categoryClassArrayList: ArrayList<VehicleClass>) {
        this.vehicleClassArrayList = categoryClassArrayList
        if (vehicleClassArrayList.size > 0) {
            rvCategoryList.visibility = View.VISIBLE
            llNoData.visibility = View.GONE
            val mLayoutManager = LinearLayoutManager(this)
            mLayoutManager.reverseLayout = true
            mLayoutManager.stackFromEnd = true
            rvCategoryList.layoutManager = mLayoutManager
            rvCategoryList.adapter = VehicleListAdapter(this, vehicleClassArrayList, this@MainActivity)
        } else {
            rvCategoryList.visibility = View.GONE
            llNoData.visibility = View.VISIBLE
        }
    }

    private fun openDrawer() {

        try {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun closeDrawer() {

        try {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openVehicleDetailsActivity() {
        val intent = Intent(this, VehicleDetailActivity::class.java)
        startActivityForResult(intent, CommonConstants.RequestDataUpdated)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val key = CommonConstants.KeyIsDataUpdated
        if (requestCode == CommonConstants.RC_GOOGLE_SIGN_IN) {
//            if (resultCode == Activity.RESULT_OK) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
            task.addOnCompleteListener {
                OnCompleteListener<GoogleSignInClient> {
                    Log.e("TAG", "onActivityResult:Complete:::::: ")
                }
            }.addOnFailureListener { exception: java.lang.Exception ->
                Log.e("TAG", "onActivityResult:::Exception::: $exception")
                CommonUtilities.showToast(this, CommonConstants.MsgSignInCancelled)
            }
//            } else if (resultCode == Activity.RESULT_CANCELED) {
//                CommonUtilities.showToast(this, CommonConstants.MsgSignInCancelled)
//            }
            Log.e("TAG", "onActivityResult::::::::Google::: " + resultCode)
        } else if (resultCode == Activity.RESULT_OK) {
            if (data!!.hasExtra(key)) {
                if (data.getBooleanExtra(key, false)) {
                    GetVehicleList(this, this@MainActivity).callGetVehicleListService()
                }
            }
        }
    }

    override fun onItemTypeClickCallback(mPos: Int) {
        if (CommonUtilities.isOnline(this)) {
            val aClass = vehicleClassArrayList[mPos]
            val intent = Intent(this, VehicleCategories::class.java)
            intent.putExtra(CommonConstants.KeyVehicleDetail, aClass)
            startActivityForResult(intent, CommonConstants.RequestDataUpdated)
        } else {
            startActivity(Intent(this, NoInternetActivity::class.java))
        }
    }

    /*override fun adLoadingFailed() {
        openVehicleDetailsActivity()
    }

    override fun adClose() {
        openVehicleDetailsActivity()
    }

    override fun startNextScreen() {
        openVehicleDetailsActivity()
    }*/

    //Todo--------------------------------------------Menu Items--------------------------------------------------

    private fun rateUs() {
        val appPackageName = packageName
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store")))
        } catch (anfe: android.content.ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store")))
        }
    }

    private fun shareAppLink() {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        val link = "https://play.google.com/store/apps/details?id=${"your_package_name"}"
        shareIntent.putExtra(Intent.EXTRA_TEXT, link)
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name))
        shareIntent.type = "text/plain"
        startActivity(Intent.createChooser(shareIntent, resources.getString(R.string.app_name)))
    }

    private fun contactUs() {
        try {
            val sendIntentGmail = Intent(Intent.ACTION_SEND)
            sendIntentGmail.type = "plain/text"
            sendIntentGmail.setPackage("com.google.android.gm")
            sendIntentGmail.putExtra(Intent.EXTRA_EMAIL, arrayOf("your_email_id"))
            sendIntentGmail.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name) + " - Android")
            startActivity(sendIntentGmail)
        } catch (e: Exception) {
            val sendIntentIfGmailFail = Intent(Intent.ACTION_SEND)
            sendIntentIfGmailFail.type = "*/*"
            sendIntentIfGmailFail.putExtra(Intent.EXTRA_EMAIL, arrayOf("your_email_id"))
            sendIntentIfGmailFail.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name) + " - Android")
            if (sendIntentIfGmailFail.resolveActivity(packageManager) != null) {
                startActivity(sendIntentIfGmailFail)
            }
        }
    }


    //Todo--------------------------------------------Menu Items--------------------------------------------------
    private var doublePressToExit = false

    override fun onBackPressed() {
        try {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
                return
            }
            if (doublePressToExit) {
                super.onBackPressed()
                return
            }
            doublePressToExit = true
            CommonUtilities.showToast(this, CommonConstants.MsgDoubleBackToExit)
            Handler().postDelayed({ doublePressToExit = false }, 2000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setAlarmManager() {
        try {
            val alarmStartTime = Calendar.getInstance()
            alarmStartTime.set(Calendar.HOUR_OF_DAY, 12)
            alarmStartTime.set(Calendar.MINUTE, 0)
            alarmStartTime.set(Calendar.SECOND, 0)
            val alarmIntent = Intent(this, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0)

            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                alarmStartTime.timeInMillis,
                pendingIntent
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun confirmationDialog(
        content: Context,
        strMsg: String
    ): Boolean {

        val builder1 = AlertDialog.Builder(content, R.style.AlertDialogTheme)
        builder1.setMessage(strMsg)
        builder1.setCancelable(true)

        builder1.setPositiveButton("Yes") { dialog, _ ->
            dialog.cancel()
            val homeIntent = Intent(Intent.ACTION_MAIN)
            homeIntent.addCategory(Intent.CATEGORY_HOME)
            homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(homeIntent)
            finishAffinity()
        }

        builder1.setNegativeButton("No") { dialog, _ ->
            dialog.cancel()
        }

        val alert11 = builder1.create()
        alert11.show()

        return false
    }


    fun isNetworkConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }


    private fun subScribeToFirebaseTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("arms_workout_topic")
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e("subScribeFirebaseTopic", ": Fail")
                } else {
                    Log.e("subScribeFirebaseTopic", ": Success")
                }
            }
    }

    fun callGetAdsId() {
        try {
            if (isNetworkConnected()) {
                if (CommonConstants.ENABLE_DISABLE == CommonConstants.ENABLE) {
                    CommonUtilities.setPref(this, CommonConstants.AD_TYPE_FB_GOOGLE, CommonConstants.GOOGLE_FACEBOOK_Ad)
                    CommonUtilities.setPref(this, CommonConstants.FB_BANNER, CommonConstants.FB_BANNER_ID)
                    CommonUtilities.setPref(this, CommonConstants.FB_INTERSTITIAL, CommonConstants.FB_INTERSTITIAL_ID)
                    CommonUtilities.setPref(this, CommonConstants.GOOGLE_BANNER, CommonConstants.GOOGLE_BANNER_ID)
                    CommonUtilities.setPref(this, CommonConstants.GOOGLE_INTERSTITIAL, CommonConstants.GOOGLE_INTERSTITIAL_ID)
                    CommonUtilities.setPref(this, CommonConstants.STATUS_ENABLE_DISABLE, CommonConstants.ENABLE_DISABLE)
                    setAppAdId(CommonConstants.GOOGLE_ADMOB_APP_ID)
                } else {
                    CommonUtilities.setPref(this, CommonConstants.STATUS_ENABLE_DISABLE, CommonConstants.ENABLE_DISABLE)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    fun setAppAdId(id: String?) {
        try {
            val applicationInfo =
                packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            val bundle = applicationInfo.metaData
            val beforeChangeId = bundle.getString("com.google.android.gms.ads.APPLICATION_ID")
            Log.e("TAG", "setAppAdId:BeforeChange:::::  $beforeChangeId")
            applicationInfo.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", id)
            val AfterChangeId = bundle.getString("com.google.android.gms.ads.APPLICATION_ID")
            Log.e("TAG", "setAppAdId:AfterChange::::  $AfterChangeId")
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

}