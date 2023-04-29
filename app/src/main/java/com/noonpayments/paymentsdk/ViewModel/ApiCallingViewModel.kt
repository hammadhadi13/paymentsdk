package com.noonpayments.paymentsdk.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arhamsoft.ilets.domain.repositories.GetApiResponseRepo
import com.noonpayments.paymentsdk.models.PaymentResponseModel
import com.noonpayments.paymentsdk.models.ResponseModel
import okhttp3.RequestBody

class ApiCallingViewModel(private val getApiResponseRepo: GetApiResponseRepo) : ViewModel() {

    private var cancelResponse: MutableLiveData<ResponseModel> = MutableLiveData()
    private var paymentResponse: MutableLiveData<PaymentResponseModel> = MutableLiveData()
    private var finalPaymentResponse: MutableLiveData<PaymentResponseModel> = MutableLiveData()
    private var errorResponse: MutableLiveData<String> = MutableLiveData()

    fun getCancelResponse(): MutableLiveData<ResponseModel> {
    return cancelResponse
    }

    fun getPaymentResponse():MutableLiveData<PaymentResponseModel>{
        return paymentResponse
    }

    fun getErrorResponse():MutableLiveData<String>{
        return errorResponse
    }

    fun getFinalPaymentResponse():MutableLiveData<PaymentResponseModel>{
        return  finalPaymentResponse
    }

    suspend fun callCancelApi(obj:RequestBody) {
        try {
            cancelResponse.postValue(getApiResponseRepo.getResponseData(obj))
        } catch (e: Exception) {
            errorResponse.postValue(e.message.toString())
        }
    }

    suspend fun callPaymentApi(obj:RequestBody){
        try {
            paymentResponse.postValue(getApiResponseRepo.getResponsePaymentData(obj))
        } catch (e: Exception) {
            errorResponse.postValue(e.message.toString())
        }
    }

    suspend fun callFinalPaymentApi(orderId:String){
        try {
            finalPaymentResponse.postValue(getApiResponseRepo.getResponseFinalPaymentData(orderId))
        } catch (e: Exception) {
            errorResponse.postValue(e.message.toString())
        }
    }

}