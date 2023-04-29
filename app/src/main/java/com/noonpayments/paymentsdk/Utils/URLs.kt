package com.noonpayments.paymentsdk.Utils

import okhttp3.OkHttpClient

object URLs {

     var authHeader = ""
    var finalBaseUrl =""

     const val NOON_URL_TEST = "https://api-test.noonpayments.com/payment/v1/"
     const val NOON_URL_LIVE = "https://api.noonpayments.com/payment/v1/"
     const val NOON_URL_TEST_ORDER = NOON_URL_TEST
     const val NOON_URL_LIVE_ORDER = NOON_URL_LIVE


    const val order = "order/"


    val baseClient: OkHttpClient = getApiClient()
    private fun getApiClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }

}