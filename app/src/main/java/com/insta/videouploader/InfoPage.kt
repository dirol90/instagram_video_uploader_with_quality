package com.insta.videouploader

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_info_page.*
import androidx.annotation.Nullable
import android.widget.Toast
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.SkuType
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.SkuDetailsParams
import com.android.billingclient.api.Purchase
import android.text.TextUtils



class InfoPage : AppCompatActivity(), BillingClientStateListener,
    PurchasesUpdatedListener {

    companion object{
        var sharedPref: SharedPreferences? = null
    }

    lateinit private var billingClient: BillingClient
    lateinit private var skuList: MutableList<SkuDetails>
    var flowParams: BillingFlowParams? = null

    internal var PERMISSIONS_ALL = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )
    private val REQUEST_EXTERNAL_STORAGE = 991

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_page)

        sharedPref = applicationContext.getSharedPreferences("first_run", 0)


        skuList = mutableListOf()
        billingClient =
            BillingClient.newBuilder(this).setListener(this).enablePendingPurchases().build()

        val mContext = this

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    querySkuDetails(mContext)
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && queryPurchases().isNotEmpty()) {
                        querySkuDetails(mContext)
                        val purchasesList = queryPurchases()
                        for (i in purchasesList.indices) {
                            val purchaseId = purchasesList[i].sku
                            if (TextUtils.equals(mSkuId1, purchaseId)) {
                                payComplete(true)
                            }
                        }
                    }
                }
            }
            override fun onBillingServiceDisconnected() {
                Toast.makeText(mContext, "Something wrong, try again", Toast.LENGTH_LONG).show()
            }
        })

        next_btn2.setOnClickListener {
            billingClient.launchBillingFlow(this, flowParams)
        }

        tv_skip.setOnClickListener {
            payComplete(false)
        }

        politics_tv2.setOnClickListener {
            val url =
                "https://sites.google.com/view/instavideouploader/%D0%B3%D0%BB%D0%B0%D0%B2%D0%BD%D0%B0%D1%8F"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            querySkuDetails(this)
            val purchasesList = queryPurchases()
            for (i in purchasesList.indices) {
                val purchaseId = purchasesList[i].sku
                if (TextUtils.equals(mSkuId1, purchaseId)) {
                    payComplete(true)
                }
            }
        }
    }


    override fun onBillingServiceDisconnected() {
        Toast.makeText(this, "Something wrong, try again", Toast.LENGTH_LONG).show()
    }

    override fun onBillingSetupFinished(billingResult: BillingResult?) {
        val params = SkuDetailsParams.newBuilder()
        billingClient.querySkuDetailsAsync(params.build()) { _, skuDetailsList ->
            if (billingResult!!.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                querySkuDetails(this)
                val purchasesList = queryPurchases()
                for (i in purchasesList.indices) {
                    val purchaseId = purchasesList[i].sku
                    if (TextUtils.equals(mSkuId1, purchaseId)) {
                        payComplete(true)
                    }
                }
            }
        }
    }

    fun payComplete(b: Boolean){
        val day = sharedPref!!.getLong("day", -1)
        if (!b && day < System.currentTimeMillis()-24*60*60*1000) {
            sharedPref!!.edit().putLong("day", System.currentTimeMillis()).apply()
            sharedPref!!.edit().putInt("day_val", 0).apply()
        }

        sharedPref!!.edit().putBoolean("is_first", false).apply()

        getPermitions()

        val i = Intent(this, MainActivity::class.java)
        if (!b) i.putExtra("free", true) else i.putExtra("free", false)
        startActivity(i)
        this.finish()
    }

    private val mSkuId1 = "subscription_month"
    private val mSkuId2 = "subscription_year"
    private val mSkuId3 = "subscription_week"


    private fun querySkuDetails(context: Context) {
        val skuDetailsParamsBuilder = SkuDetailsParams.newBuilder()
        val localSkus = arrayListOf<String>()
        localSkus.add(mSkuId1)
        localSkus.add(mSkuId2)
        localSkus.add(mSkuId3)


        skuDetailsParamsBuilder.setSkusList(localSkus).setType(SkuType.SUBS)
        billingClient.querySkuDetailsAsync(
            skuDetailsParamsBuilder.build()
        ) { _, skuDetailsList ->
            if (skuDetailsList != null) {
                skuList = skuDetailsList
                flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuList[0])
                    .build()
            }
        }
    }

    private fun queryPurchases(): List<Purchase> {
        val purchasesResult = billingClient.queryPurchases(SkuType.SUBS)
        return purchasesResult.purchasesList
    }

    private fun getPermitions() {
        var permission =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS_ALL,
                REQUEST_EXTERNAL_STORAGE
            )
        }
        permission =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS_ALL,
                REQUEST_EXTERNAL_STORAGE
            )
        }
        permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS_ALL,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }
}
