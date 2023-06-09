package com.noonpayments.paymentsdk.Utils

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher

object CommonMethods {

    fun stringToInteger(value: String): Int {
        return try {
            var i = 0
            i = value.toInt()
            i
        } catch (ex: Exception) {
            -1
        }
    }

    fun encrypt(data: String, publicKey: String): String? {
        var publicKey = publicKey
        return try {
            publicKey = publicKey.replace("-----BEGIN PUBLIC KEY-----\n", "")
            publicKey = publicKey.replace("\n-----END PUBLIC KEY-----\n", "")
            publicKey = publicKey.replace("\n", "")
            val publicBytes =
                Base64.getDecoder().decode(publicKey.toByteArray())
            val keySpec =
                X509EncodedKeySpec(publicBytes)
            val keyFactory =
                KeyFactory.getInstance("RSA")
            val pubKey = keyFactory.generatePublic(keySpec)
            val cipher =
                Cipher.getInstance("RSA/None/PKCS1Padding") //"RSA/ECB/PKCS1Padding"
            cipher.init(Cipher.ENCRYPT_MODE, pubKey)
            val encryptedMessage = cipher.doFinal(data.toByteArray())
            Base64.getEncoder().encodeToString(encryptedMessage)
        } catch (ex: java.lang.Exception) {
//            ex.printStackTrace()
            ""
        }
    }

    fun getJSONDouble(jObject: JSONObject, key: String): Double {
        var value = 0.0
        try {
            if (jObject.has(key)) value = jObject.getDouble(key)
        } catch (ex: java.lang.Exception) {
        }
        return value
    }

    fun getJSONString(jObject: JSONObject, key: String): String? {
        var value: String? = ""
        try {
            if (jObject.has(key)) value = jObject.getString(key)
        } catch (ex: java.lang.Exception) {
            val qqqq = key
        }
        return value
    }

    fun cancelledOrderJSON(orderNumber: String): String {
        return try {
            val initObject = JSONObject()
            initObject.put("apiOperation", "CANCEL")
            val orderObject = JSONObject()
            orderObject.put("id", orderNumber)
            initObject.put("order", orderObject)
            initObject.toString()
        } catch (ex: java.lang.Exception) {
            ""
        }
    }

    fun makeRequestBodyParam(json:String):RequestBody{
        val mediaType = "application/json; charset=utf-8".toMediaType()
        return json.toRequestBody(mediaType)
    }
}