package com.noonpayments.paymentsdk.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.arhamsoft.ilets.domain.repositories.GetApiResponseRepo
import com.google.gson.Gson
import com.noonpayments.paymentsdk.Base.BaseActivity
import com.noonpayments.paymentsdk.R
import com.noonpayments.paymentsdk.Utils.CommonMethods.encrypt
import com.noonpayments.paymentsdk.Utils.CommonMethods.getJSONDouble
import com.noonpayments.paymentsdk.Utils.CommonMethods.getJSONString
import com.noonpayments.paymentsdk.Utils.CommonMethods.makeRequestBodyParam
import com.noonpayments.paymentsdk.Utils.URLs
import com.noonpayments.paymentsdk.ViewModel.ApiCallingViewModel
import com.noonpayments.paymentsdk.databinding.ActivityFinalBinding
import com.noonpayments.paymentsdk.helpers.Helper
import com.noonpayments.paymentsdk.models.*
import com.retech.yapiee.domain.retrofit.RetrofitClient.Companion.getInstance
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*

class FinalActivity : BaseActivity() {

    var addNewCard = false
    var isSavedCard = false
    var cardNumber = ""
    var cardName = ""
    var expMonth = ""
    var expYear = ""
    var cvv = ""
    var cardToken = ""

    //response information
    var is3DS = false
    var responseJson = ""
    var responseMessage = ""
    var responseCardToken = ""
    var responseCardNumber = ""
    var responseCardType = ""
    var responseTransactionId = ""

    lateinit var mainHandle: Handler
    var context: Context? = null
    private var finishActivityLauncher: ActivityResultLauncher<Intent>? = null

    lateinit var viewModel: ApiCallingViewModel
    private lateinit var binding: ActivityFinalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = ActivityFinalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFinishOnTouchOutside(false)

        context = setLocale(this, language)
        apiCallingViewModel = ApiCallingViewModel(GetApiResponseRepo(getInstance()))
        val window = window
        val wlp = window.attributes
        wlp.gravity = Gravity.BOTTOM
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT
        window.attributes = wlp
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mainHandler = Handler(this.mainLooper)

        val extras = intent.extras
        addNewCard = extras!!.getBoolean("addnewcard")
        isSavedCard = extras.getBoolean("isSavedCard")
        if (addNewCard) {
            cardName = extras.getString("cardname")!!
            cardNumber = extras.getString("cardnumber")!!
            val exp = extras.getString("exp")
            expMonth = exp!!.substring(0, 2)
            expYear = "20" + exp.substring(3, 5)
            cvv = extras.getString("cvv")!!
        } else {
            cardToken = extras.getString("cardtoken")!!
            cvv = extras.getString("cvv")!!
        }

        initView()
        initEvents()
        setObserver()
        //start processing
        processPayment()
        onActivityResultFunction()
    }

    private fun initView() {
        binding.viewSuccess.visibility = View.GONE
        binding.txtPaySecure.text = context!!.resources.getString(R.string.payment_secure)
        binding.txtMessage1.text = context!!.resources.getString(R.string.payment_processing)
        binding.txtMessage2.text = ""

        //colors & icons
        userUI.setupDialog(this, binding.dialogMode)
        userUI.setupLogo(binding.viewLogo)
        binding.txtPaySecure.setTextColor(userUI.footerForegroundColor)
        binding.txtMessage1.setTextColor(userUI.paymentOptionHeadingForeground)
        binding.txtMessage2.setTextColor(userUI.paymentOptionHeadingForeground)
    }

    private fun initEvents() {
        binding.btnCancel.setOnClickListener { view -> doComplete() }
    }

    private fun do3DSflow(url3ds: String) {
        val intent = Intent(this, DSActivity::class.java)
        intent.putExtra("url3ds", url3ds)
        //        startActivityForResult(in, 4000);
        finishActivityLauncher!!.launch(intent)
    }

    private fun setObserver() {
        apiCallingViewModel.getErrorResponse().observe(this) {
            noonPaymentsResponse.setDetails(Helper.STATUS_FAILURE, it.toString(), "", "")
            displayResult(
                false,
                context!!.resources.getString(R.string.payment_failed),
                it.toString()
            )
        }
        apiCallingViewModel.getPaymentResponse().observe(this) { model ->
//            if (model!=null) {
            val responseStr = model.toString()
            Log.d("getPaymentResponse", "setObserver: ${model.toString()}")
            processResponse(model)

//            } else {
//                // Request not successful
//                noonPaymentsResponse.setDetails(Helper.STATUS_FAILURE, model.message, "", "")
//                displayResult(
//                    false,
//                    context!!.resources.getString(R.string.payment_failed),
//                    response.message
//                )
//            }
        }

        apiCallingViewModel.getFinalPaymentResponse().observe(this) { model ->
//            if (response.isSuccessful) {
            val responseStr = model.toString()
            Log.d("getFinalPaymentResponse", "setObserver: $model")
            validateOrder(responseStr)
//            } else {
//                // Request not successful
//                noonPaymentsResponse.setDetails(Helper.STATUS_FAILURE, response.message, "", "")
//                displayResult(
//                    false,
//                    context!!.resources.getString(R.string.payment_failed),
//                    response.message
//                )
//            }
        }
    }

    fun onActivityResultFunction() {
        finishActivityLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            object : ActivityResultCallback<ActivityResult?> {
                override fun onActivityResult(result: ActivityResult?) {
                    if (result?.resultCode == RESULT_OK) {
                        assert(result.data != null)
                        val rOrderId = result.data!!.getStringExtra("orderid")
                        val rMerchantId = result.data!!.getStringExtra("merchantid")
                        if (rOrderId!!.isEmpty() || rMerchantId!!.isEmpty() || rOrderId != data.orderId) {
                            //payment failure
                            val message = Helper.getLanguageTextByUser(
                                this@FinalActivity,
                                "payment_error_reference",
                                userUI.language,
                                ""
                            )
                            noonPaymentsResponse.setDetails(
                                Helper.STATUS_FAILURE,
                                message,
                                "",
                                responseJson
                            )
                            displayResult(
                                false, context!!.resources.getString(R.string.payment_failed),
                                message
                            )
                        } else {
                            //validate the order by calling inquiry
                            validateTransacton()
                        }
                    }
                }
            }
        )
    }

    private fun doComplete() {
        //setup according to api calls etc....
        val resultIntent = Intent()
        resultIntent.putExtra("noonresponse", Gson().toJson(noonPaymentsResponse))
        resultIntent.putExtra("response", "complete")
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun processPayment() {
        try {
            //set all values to default
            noonPaymentsResponse = NoonPaymentsResponse()
            responseJson = ""
            responseMessage = ""
            responseCardToken = ""
            responseTransactionId = ""
            val signature: String = createSignature()
            val json: String = createAddPaymentJSON(signature)
            val body = makeRequestBodyParam(json)
            Log.d("callPaymentApiPayLoad", "processPayment: $json")
            lifecycleScope.launch {
                apiCallingViewModel.callPaymentApi(body)
            }
        } catch (ex: Exception) {
//            ex.printStackTrace();
        }
    }

    private fun processResponse(response: PaymentResponseModel) {
        try {
            val success = false
            var resultCode = -1
            responseJson = response.toString()
//            val jsonObject = JSONObject(response)
//            resultCode = jsonObject.getInt("resultCode")
            resultCode = response.resultCode!!
//            responseMessage = getJSONString(jsonObject, "message")!!
            responseMessage = response.message.toString()
            if (resultCode == 0) {
                if (response.result?.nextActions == "CHECK_3DS_ENROLLMENT")
//                if (jsonObject.getJSONObject("result")
//                        .has("nextActions") && jsonObject.getJSONObject("result")
//                        .getString("nextActions") == "CHECK_3DS_ENROLLMENT"
//                )
                {
                    //get post url and open browser
                    val url3DS = response.result!!.checkoutData?.postUrl.toString()
//                        getJSONString(
//                        jsonObject.getJSONObject("result").getJSONObject("checkoutData"), "postUrl"
//                    )
                    if (url3DS.isNotEmpty()) {
                        do3DSflow(url3DS)
                    } else {
                        val responseMessage =
                            Helper.getLanguageTextByUser(this, "3ds_failed", userUI.language, "")
                        noonPaymentsResponse.setDetails(
                            Helper.STATUS_FAILURE,
                            responseMessage,
                            "",
                            response.toString()
                        )
                        displayResult(
                            false,
                            context!!.resources.getString(R.string.payment_failed),
                            responseMessage
                        )
                    }
                } else {
                    //NON 3DS flow
                    responseTransactionId = response.result?.transaction?.id.toString()
//                        getJSONString(
//                        jsonObject.getJSONObject("result").getJSONObject("transaction"), "id"
//                    )!!
                    validateTransacton()
                }
            } else {
                Log.d("watchingError", "processResponse: this is due to 0")
                noonPaymentsResponse.setDetails(
                    Helper.STATUS_FAILURE,
                    responseMessage,
                    "",
                    response.toString()
                )
                displayResult(
                    false, context!!.resources.getString(R.string.payment_failed),
                    responseMessage
                )
            }
        } catch (e: JSONException) {
            Log.d(
                "watchingError",
                "processResponse: this is due to exception ${e.message.toString()}"
            )

            noonPaymentsResponse.setDetails(
                Helper.STATUS_FAILURE,
                "Exception: " + e.message,
                "",
                response.toString()
            )
            displayResult(
                false, context!!.resources.getString(R.string.payment_failed),
                ""
            )
            //            e.printStackTrace();
        }
    }

    private fun validateOrder(response: String): Boolean {
        val isValid = false
        try {
            var status = Helper.STATUS_FAILURE
            var message: String? = ""
            val jsonObject = JSONObject(response)
            val resultCode = jsonObject.getInt("resultCode")
            message = getJSONString(jsonObject, "message")
            if (resultCode == 0) {
                //check amount and status
                val jsonOrder = jsonObject.getJSONObject("result").getJSONObject("order")
                val orderStatus = getJSONString(jsonOrder, "status")
                val orderReference = getJSONString(jsonOrder, "reference")
                val amount = if (data.paymentType.equals(
                        PaymentType.SALE.toString(),
                        ignoreCase = true
                    )
                ) getJSONDouble(jsonOrder, "totalSalesAmount") else getJSONDouble(
                    jsonOrder,
                    "totalAuthorizedAmount"
                )
                val jsonTransactions =
                    jsonObject.getJSONObject("result").getJSONArray("transactions")
                var transactionStatus: String? = ""
                if (jsonTransactions.length() > 0) {
                    transactionStatus = getJSONString(jsonTransactions.getJSONObject(0), "status")
                }
                if (orderStatus.equals(PaymentStatus.CANCELLED.toString(), ignoreCase = true)) {
                    status = Helper.STATUS_CANCELLED
                    message =
                        Helper.getLanguageTextByUser(this, "payment_cancelled", userUI.language, "")
                } else if (orderStatus.equals(
                        PaymentStatus.FAILED.toString(),
                        ignoreCase = true
                    ) || orderStatus!!.uppercase(
                        Locale.getDefault()
                    ) == PaymentStatus.EXPIRED.toString() || orderStatus!!.uppercase(
                        Locale.getDefault()
                    ) == PaymentStatus.REJECTED.toString()
                ) {
                    status = Helper.STATUS_FAILURE
                    message = context!!.resources.getString(R.string.payment_failed)
                } else {
                    if (transactionStatus.equals(
                            PaymentStatus.SUCCESS.toString(),
                            ignoreCase = true
                        )
                    ) {
                        if (data.paymentType.equals(
                                PaymentType.SALE.toString(),
                                ignoreCase = true
                            ) && amount >= data.amount || data.paymentType.equals(
                                PaymentType.AUTHORIZE.toString(),
                                ignoreCase = true
                            ) && amount >= data.amount || data.paymentType.equals(
                                "Authorize,Sale",
                                ignoreCase = true
                            ) && amount >= data.amount
                        ) {
                            status = Helper.STATUS_SUCCESS
                            message = context!!.resources.getString(R.string.payment_success)
                            responseTransactionId =
                                getJSONString(
                                    jsonObject.getJSONObject("result").getJSONArray("transactions")
                                        .getJSONObject(0), "id"
                                )!!

                            //set the token values
                            if (data.isAllowCardTokenization) {
                                if (jsonObject.getJSONObject("result").has("paymentDetails") &&
                                    jsonObject.getJSONObject("result")
                                        .getJSONObject("paymentDetails").has("tokenIdentifier")
                                ) {
                                    responseCardToken =
                                        getJSONString(
                                            jsonObject.getJSONObject("result")
                                                .getJSONObject("paymentDetails"), "tokenIdentifier"
                                        )!!
                                    responseCardNumber =
                                        getJSONString(
                                            jsonObject.getJSONObject("result")
                                                .getJSONObject("paymentDetails"), "paymentInfo"
                                        )!!
                                    val cn = getJSONString(
                                        jsonObject.getJSONObject("result")
                                            .getJSONObject("paymentDetails"), "paymentInfo"
                                    )
                                    responseCardType = Helper.getCardType(cn, data.currency)
                                }
                            }
                        } else {
                            status = Helper.STATUS_FAILURE
                            message = Helper.getLanguageTextByUser(
                                this,
                                "payment_error_amount",
                                userUI.language,
                                ""
                            )
                        }
                    } else {
                        status = Helper.STATUS_FAILURE
                        message = Helper.getLanguageTextByUser(
                            this,
                            "payment_cancelled",
                            userUI.language,
                            ""
                        )
                    }
                }
            }
            if (status == Helper.STATUS_SUCCESS) {
                //display results
                if (data.isAllowCardTokenization && !responseCardToken.isEmpty()) {
                    noonPaymentsResponse.setDetails(
                        Helper.STATUS_SUCCESS,
                        message,
                        responseTransactionId,
                        response,
                        responseCardNumber,
                        responseCardType,
                        responseCardToken
                    )
                } else {
                    noonPaymentsResponse.setDetails(
                        Helper.STATUS_SUCCESS,
                        message,
                        responseTransactionId,
                        response
                    )
                }
                displayResult(
                    true, data.getDisplayAmount(userUI.language),
                    context!!.resources.getString(R.string.payment_success)
                )
            } else {
                if (message!!.isEmpty()) message =
                    context!!.resources.getString(R.string.payment_failed)
                noonPaymentsResponse.setDetails(Helper.STATUS_FAILURE, message, "", response)
                displayResult(
                    false, context!!.resources.getString(R.string.payment_failed),
                    message
                )
            }
        } catch (e: JSONException) {
            noonPaymentsResponse.setDetails(Helper.STATUS_FAILURE, e.message, "", response)
            displayResult(false, context!!.resources.getString(R.string.payment_failed), e.message)
            //            e.printStackTrace();
        }
        return isValid
    }

    private fun validateTransacton() {
        Log.d("callFinalPaymentApi", "validateTransacton: ${data.orderId}")
        lifecycleScope.launch {
            apiCallingViewModel.callFinalPaymentApi(data.orderId)
        }
    }

    private fun displayResult(success: Boolean, message1: String, message2: String?) {
        mainHandler.post {
            // do your work on main thread
            if (success) {
                binding.viewSuccess.setImageResource(R.drawable.ic_success)
                binding.txtMessage1.text = message1
                binding.txtMessage2.text = message2
            } else {
                binding.viewSuccess.setImageResource(R.drawable.ic_failure)
                binding.txtMessage1.text = message1
                binding.txtMessage2.text = ""
            }
            binding.pgrBar.visibility = View.GONE
            binding.viewSuccess.visibility = View.VISIBLE
        }
    }

    private fun createAddPaymentJSON(signature: String): String {
        return try {
            val initObject = JSONObject()
            initObject.put("apiOperation", "ADD_PAYMENT_INFO")
            val orderObject = JSONObject()
            orderObject.put("id", data.orderId)
            val paymentObject = JSONObject()
            paymentObject.put("type", "CARD")
            val cardObject = JSONObject()
            val conObj = JSONObject()
            if (addNewCard) {
                cardObject.put("nameOnCard", cardName)
                cardObject.put("numberPlain", cardNumber)
                cardObject.put("cvv", cvv)
                cardObject.put("expiryMonth", expMonth)
                cardObject.put("expiryYear", expYear)
                cardObject.put("sdkSignature", signature)
            } else {
                cardObject.put("cvv", cvv)
                cardObject.put("sdkSignature", signature)
                cardObject.put("tokenIdentifier", cardToken)
            }
            paymentObject.put("data", cardObject)
            conObj.put("payerConsentForToken", isSavedCard)
            initObject.put("order", orderObject)
            initObject.put("paymentData", paymentObject)
            initObject.put("configuration", conObj)
            initObject.toString()
        } catch (ex: java.lang.Exception) {
            Log.e("NoonSDK", ex.message!!)
            Log.e("NoonSDK", ex.stackTrace.toString())
            ""
        }
    }

    private fun createSignature(): String {
        var sha512 =
            data.orderId + "|" + data.paymentCategory + "|" + data.currency +
                    "|" + data.paymentChannel + "|ADD_PAYMENT_INFO|noonpayments_sdk"
        sha512 = sha512.lowercase(Locale.getDefault())
        val hash = hashString(sha512)
        return encrypt(hash, data.publicKey)!!
    }

    private fun hashString(input: String): String {
        return try {
            val md = MessageDigest.getInstance("SHA-512")
            val messageDigest = md.digest(input.toByteArray())
            val no = BigInteger(1, messageDigest)
            var hashtext = no.toString(16)
            while (hashtext.length < 128) {
                hashtext = "0$hashtext"
            }
            hashtext
        } catch (ex: java.lang.Exception) {
            ""
        }
    }
}