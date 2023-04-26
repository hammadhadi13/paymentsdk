package com.noonpayments.paymentsdk.Utils

import okhttp3.OkHttpClient

object URLs {

    const val authHeader = "Key_Test UGx1Z2luLlBsdWdpbl90ZXN0OmRmMDU1YTFiMjU4YzQ1MzRiZWZmNjlkMmFmN2JlOTk2"


     const val NOON_URL_TEST = "https://api-test.noonpayments.com/payment/v1/"
     const val NOON_URL_LIVE = "https://api.noonpayments.com/payment/v1/"
     const val NOON_URL_TEST_ORDER = NOON_URL_TEST + "order"
     const val NOON_URL_LIVE_ORDER = NOON_URL_LIVE + "order"


    val baseClient: OkHttpClient = getApiClient()
    private fun getApiClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }

}