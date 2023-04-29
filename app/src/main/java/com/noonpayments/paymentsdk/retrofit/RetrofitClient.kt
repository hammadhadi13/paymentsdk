package com.retech.yapiee.domain.retrofit

import com.noonpayments.paymentsdk.Utils.URLs
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitClient {
    var timeOut: Long = 60
    val apiClient: RestApi
        get() {
            val retrofit = createRetrofit()
            return retrofit.create(RestApi::class.java)
        }

    private fun createOkHttpClient(): OkHttpClient {

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val builder = OkHttpClient.Builder()
        builder.readTimeout(timeOut, TimeUnit.MINUTES)
        builder.connectTimeout(timeOut, TimeUnit.MINUTES)
        builder.writeTimeout(timeOut, TimeUnit.MINUTES)
        builder.addInterceptor(interceptor)
        builder.addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader(
                    "Authorization",
                    URLs.authHeader
                )
                .build()
            chain.proceed(request)
        }

        builder.addInterceptor(Interceptor { chain ->
            val request = chain.request()
            return@Interceptor chain.proceed(request)
        })
        return builder.build()
    }

    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(URLs.finalBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(createOkHttpClient())
            .build()
    }

    companion object {
        private var instance: RetrofitClient? = null


        fun getInstance(): RestApi {
            if (instance == null)
                instance = RetrofitClient()
            return instance!!.apiClient
        }
    }
}