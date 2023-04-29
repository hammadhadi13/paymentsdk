package com.noonpayments.paymentsdk.Base

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.arhamsoft.ilets.domain.repositories.GetApiResponseRepo
import com.noonpayments.paymentsdk.Utils.CommonMethods.cancelledOrderJSON
import com.noonpayments.paymentsdk.Utils.URLs.finalBaseUrl
import com.noonpayments.paymentsdk.ViewModel.ApiCallingViewModel
import com.noonpayments.paymentsdk.helpers.Helper
import com.noonpayments.paymentsdk.models.*
import com.retech.yapiee.domain.retrofit.RetrofitClient.Companion.getInstance
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import java.util.*
import kotlin.coroutines.Continuation

open class BaseActivity : AppCompatActivity() {

    var language = "en"
    lateinit var mainHandler: Handler
    lateinit var helper: Helper
    lateinit var setup: NoonPaymentsSetup
    lateinit var userUI: NoonPaymentsUI
    lateinit var data: NoonPaymentsData
    lateinit var noonPaymentsResponse: NoonPaymentsResponse
    lateinit var apiCallingViewModel: ApiCallingViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainHandler = Handler(this.mainLooper)
        helper = Helper()
        //setup the UI
        apiCallingViewModel = ApiCallingViewModel(GetApiResponseRepo(getInstance()))
        setup = NoonPaymentsSetup.getInstance()
        userUI = setup.noonUI
        data = setup.noonData
        noonPaymentsResponse = NoonPaymentsResponse()
        setBaseUrl()
        getObserver()

    }

    private fun setBaseUrl() {
        var url = NoonPaymentsAPIConfig.NOON_URL_LIVE_ORDER
        if (data.paymentMode == PaymentMode.TEST) url =
            NoonPaymentsAPIConfig.NOON_URL_TEST_ORDER
        finalBaseUrl = url
    }

    fun setLocale(activity: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources = activity.resources
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        return activity
    }

    fun callCancelAPI(orderNumber: String?) {
        val json = cancelledOrderJSON(orderNumber!!)
        lifecycleScope.launch(Dispatchers.IO) {
            apiCallingViewModel.callCancelApi(json)
        }
    }

    fun getObserver() {
        apiCallingViewModel.getCancelResponse().observe(this) { model ->
            if (model.resultCode != null) {
                if (model.resultCode == 0) {
                    Toast.makeText(this, "Payment Cancelled!", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(this, model.message.toString(), Toast.LENGTH_LONG).show();

                }
            }
        }

        apiCallingViewModel.getErrorResponse().observe(this) {
            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}