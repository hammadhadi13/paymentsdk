package com.noonpayments.paymentsdk.Base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.arhamsoft.ilets.domain.repositories.GetApiResponseRepo
import com.google.gson.Gson
import com.noonpayments.paymentsdk.Utils.CommonMethods
import com.noonpayments.paymentsdk.Utils.CommonMethods.cancelledOrderJSON
import com.noonpayments.paymentsdk.Utils.CommonMethods.makeRequestBodyParam
import com.noonpayments.paymentsdk.Utils.URLs.authHeader
import com.noonpayments.paymentsdk.Utils.URLs.finalBaseUrl
import com.noonpayments.paymentsdk.ViewModel.ApiCallingViewModel
import com.noonpayments.paymentsdk.helpers.Helper
import com.noonpayments.paymentsdk.models.*
import com.retech.yapiee.domain.retrofit.RetrofitClient.Companion.getInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*

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
        setup = NoonPaymentsSetup.getInstance()
        userUI = setup.noonUI
        data = setup.noonData
        noonPaymentsResponse = NoonPaymentsResponse()
        setBaseUrl()
        apiCallingViewModel = ApiCallingViewModel(GetApiResponseRepo(getInstance()))
        getObserver()
    }

    private fun setBaseUrl() {
        var url = NoonPaymentsAPIConfig.NOON_URL_LIVE_ORDER
        if (data.paymentMode == PaymentMode.TEST) url =
            NoonPaymentsAPIConfig.NOON_URL_TEST_ORDER
        finalBaseUrl = url
        authHeader = data.authorizationHeader.toString()
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

    fun callCancelAPI(orderNumber: String) {
        val json = cancelledOrderJSON(orderNumber)
        val body = makeRequestBodyParam(json)
        lifecycleScope.launch(Dispatchers.IO) {
            apiCallingViewModel.callCancelApi(body)
        }
    }

    fun getObserver() {
        apiCallingViewModel.getCancelResponse().observe(this) { model ->
            if (model.resultCode != null) {
                if (model.resultCode == 0) {
                    Toast.makeText(this, "Payment Cancelled!", Toast.LENGTH_LONG).show();

                    val response = NoonPaymentsResponse()
                    response.setDetails(Helper.STATUS_FAILURE, "Payment cancelled by user", "", "")
                    val resultIntent = Intent()
                    resultIntent.putExtra("noonresponse", Gson().toJson(response))
                    setResult(RESULT_OK, resultIntent)
                    finish()
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