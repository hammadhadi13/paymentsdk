package com.arhamsoft.ilets.domain.repositories

import com.google.gson.JsonObject
import com.retech.yapiee.domain.retrofit.RestApi
import okhttp3.RequestBody


class GetApiResponseRepo(private val restApi: RestApi) {

    suspend fun getResponseData(obj:String) = restApi.callCancel(obj)

    suspend fun getResponsePaymentData(obj:String) = restApi.callPayment(obj)

    suspend fun getResponseFinalPaymentData(orderId:String) = restApi.callFinalApi(orderId)
}