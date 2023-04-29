package com.noonpayments.paymentsdk.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class ResponseModel {

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

    class Result {
        @SerializedName("order")
        @Expose
        var order: Order? = null
    }

    class Order {
        @SerializedName("status")
        @Expose
        var status: String? = null

        @SerializedName("creationTime")
        @Expose
        var creationTime: String? = null

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

}