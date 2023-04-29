package com.retech.yapiee.domain.retrofit

import com.noonpayments.paymentsdk.Utils.URLs
import com.noonpayments.paymentsdk.models.FinalPaymentResponseModel
import com.noonpayments.paymentsdk.models.PaymentResponseModel
import com.noonpayments.paymentsdk.models.ResponseModel
import okhttp3.RequestBody
import retrofit2.http.*

interface RestApi {

    @POST(URLs.order)
    suspend fun callCancel(
        @Body data: RequestBody
    ): ResponseModel

    @POST(URLs.order)
    suspend fun callPayment(
        @Body data: RequestBody
    ): PaymentResponseModel

    @GET("order/{order_Id}")
    suspend fun callFinalApi(
        @Path("order_Id") orderId: String
    ): FinalPaymentResponseModel
}