package com.arhamsoft.ilets.domain.repositories

import com.retech.yapiee.domain.retrofit.RestApi
import okhttp3.RequestBody


class GetApiResponseRepo(private val restApi: RestApi) {

    suspend fun getResponseData(obj:RequestBody) = restApi.callCancel(obj)

    suspend fun getResponsePaymentData(obj:RequestBody) = restApi.callPayment(obj)

    suspend fun getResponseFinalPaymentData(orderId:String) = restApi.callFinalApi(orderId)
}