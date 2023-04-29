package com.noonpayments.paymentsdk.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class FinalPaymentResponseModel {

    @SerializedName("resultCode")
    @Expose
    var resultCode: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("resultClass")
    @Expose
    var resultClass: Int? = null

    @SerializedName("classDescription")
    @Expose
    var classDescription: String? = null

    @SerializedName("actionHint")
    @Expose
    var actionHint: String? = null

    @SerializedName("requestReference")
    @Expose
    var requestReference: String? = null

    @SerializedName("result")
    @Expose
    var result: Result? = null

    class Order {
        @SerializedName("status")
        @Expose
        var status: String? = null

        @SerializedName("creationTime")
        @Expose
        var creationTime: String? = null

        @SerializedName("totalAuthorizedAmount")
        @Expose
        var totalAuthorizedAmount: Float? = null

        @SerializedName("totalCapturedAmount")
        @Expose
        var totalCapturedAmount: Float? = null

        @SerializedName("totalRefundedAmount")
        @Expose
        var totalRefundedAmount: Float? = null

        @SerializedName("totalRemainingAmount")
        @Expose
        var totalRemainingAmount: Float? = null

        @SerializedName("totalReversedAmount")
        @Expose
        var totalReversedAmount: Float? = null

        @SerializedName("totalSalesAmount")
        @Expose
        var totalSalesAmount: Float? = null

        @SerializedName("errorCode")
        @Expose
        var errorCode: Int? = null

        @SerializedName("id")
        @Expose
        var id: Long? = null

        @SerializedName("amount")
        @Expose
        var amount: Float? = null

        @SerializedName("currency")
        @Expose
        var currency: String? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("reference")
        @Expose
        var reference: String? = null

        @SerializedName("category")
        @Expose
        var category: String? = null

        @SerializedName("channel")
        @Expose
        var channel: String? = null
    }

    class PaymentDetails {
        @SerializedName("instrument")
        @Expose
        var instrument: String? = null

        @SerializedName("tokenIdentifier")
        @Expose
        var tokenIdentifier: String? = null

        @SerializedName("cardAlias")
        @Expose
        var cardAlias: String? = null

        @SerializedName("mode")
        @Expose
        var mode: String? = null

        @SerializedName("integratorAccount")
        @Expose
        var integratorAccount: String? = null

        @SerializedName("paymentInfo")
        @Expose
        var paymentInfo: String? = null

        @SerializedName("payerInfo")
        @Expose
        var payerInfo: String? = null

        @SerializedName("brand")
        @Expose
        var brand: String? = null

        @SerializedName("scheme")
        @Expose
        var scheme: String? = null

        @SerializedName("expiryMonth")
        @Expose
        var expiryMonth: String? = null

        @SerializedName("expiryYear")
        @Expose
        var expiryYear: String? = null

        @SerializedName("isNetworkToken")
        @Expose
        var isNetworkToken: String? = null

        @SerializedName("cardType")
        @Expose
        var cardType: String? = null

        @SerializedName("cardCategory")
        @Expose
        var cardCategory: String? = null

        @SerializedName("cardCountry")
        @Expose
        var cardCountry: String? = null

        @SerializedName("cardCountryName")
        @Expose
        var cardCountryName: String? = null
    }

    class Result {
        @SerializedName("nextActions")
        @Expose
        var nextActions: String? = null

        @SerializedName("transactions")
        @Expose
        var transactions: List<Transaction>? = null

        @SerializedName("order")
        @Expose
        var order: Order? = null

        @SerializedName("paymentDetails")
        @Expose
        var paymentDetails: PaymentDetails? = null
    }

    class Transaction {
        @SerializedName("type")
        @Expose
        var type: String? = null

        @SerializedName("authorizationCode")
        @Expose
        var authorizationCode: String? = null

        @SerializedName("creationTime")
        @Expose
        var creationTime: String? = null

        @SerializedName("status")
        @Expose
        var status: String? = null

        @SerializedName("stan")
        @Expose
        var stan: String? = null

        @SerializedName("rrn")
        @Expose
        var rrn: String? = null

        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("amount")
        @Expose
        var amount: Float? = null

        @SerializedName("currency")
        @Expose
        var currency: String? = null
    }
}