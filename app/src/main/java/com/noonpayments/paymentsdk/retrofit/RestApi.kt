package com.retech.yapiee.domain.retrofit

import com.google.gson.JsonObject
import com.noonpayments.paymentsdk.Utils.URLs
import com.noonpayments.paymentsdk.models.ResponseModel
import okhttp3.MultipartBody
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
    ): ResponseModel

    @GET("order/{post_id}")
    suspend fun callFinalApi(
        @Path("orderId") postId: String
    ): ResponseModel


//
//    // Get User Profile
//    @GET(URLS.getProfile)
//    suspend fun getUser(
//        @Header("Authorization") header: String,
//        @Query(value = "profileId", encoded = true) profileId: String,
//    ): MDUser
//
//    // Register User
//    @POST(URLS.register)
//    suspend fun registerUser(
//        @Body data: JsonObject
//    ): MDUser
//
//
//    // Client Home Data
//    @POST(URLS.getClientHomeData)
//    suspend fun clientHomeData(
//        @Body data: JsonObject
//    ): MDHomeData
//
//
//    @POST(URLS.getInfluencerHomeData)
//    suspend fun influencersHomeData(
//        @Body data: JsonObject
//    ): MDInfluencerHomeData
//
//
//    // Get Banners
//    @GET(URLS.getBanners)
//    suspend fun getBannersData(
//        @Header("Authorization") header: String,
//        @Query(value = "userRole", encoded = true) userRole: Int,
//    ): BannerModel
//
//
//    @GET(URLS.refreshToken)
//    suspend fun refreshToken(
//        @Query(value = "token", encoded = true) token: String,
//    ): MDRefreshToken
//
//    @POST(URLS.getInfluencers)
//    suspend fun getInfluencers(
//        @Body data: JsonObject
//    ): MDInfluencers
//
//    @GET(URLS.getCategories)
//    suspend fun getCategories(): CategoryMD
//
//
//    @GET(URLS.mapPlaces)
//    suspend fun getPlaces(
//        @Query(value = "query", encoded = true) query: String,
//        @Query(value = "key", encoded = true) key: String,
//    ): MDLocation
//
//    @Multipart
//    @POST(URLS.setProfile)
//    suspend fun updateProfile(
//        @Part(value = "name") name: RequestBody,
//        @Part(value = "isOpenForInvitation") isOpenForInvitation: RequestBody,
//        @Part(value = "brandName") brandName: RequestBody,
//        @Part profileImage: MultipartBody.Part,
//        @Part coverImage: MultipartBody.Part,
//        @Part(value = "PhoneNumber") PhoneNumber: RequestBody,
//        @Part(value = "About") About: RequestBody,
//        @Part(value = "Latitude") Latitude: RequestBody,
//        @Part(value = "Longitude") Longitude: RequestBody,
//        @Part(value = "Gender") Gender: RequestBody,
//        @Part(value = "DOB") DOB: RequestBody,  // mm-dd-yyyy
//        @Part(value = "categoriesId") categoriesId: RequestBody,
//        @Part(value = "language") language: RequestBody,
//        @Part(value = "Location") Location: RequestBody,
//        @Part(value = "phoneCode") phoneCode: RequestBody,
//        @Part(value = "countryCode") countryCode: RequestBody,
//    ): MDUser
//
//
//    @Multipart
//    @POST(URLS.createOffer)
//    suspend fun createOffer(
//        @Part(value = "Status") Status: RequestBody,
//        @Part(value = "OfferType") OfferType: RequestBody,
//        @Part(value = "Title") Title: RequestBody,
//        @Part(value = "OfferCategory") OfferCategory: RequestBody,
//        @Part(value = "Description") Description: RequestBody,
//        @Part(value = "ExpiryDate") ExpiryDate: RequestBody,
//        @Part offerFiles: List<MultipartBody.Part>,
//        @Part(value = "IsUserRetrictedToUploadImage") IsUserRetrictedToUploadImage: RequestBody,
//        @Part(value = "Latitude") Latitude: RequestBody,
//        @Part(value = "Longitude") Longitude: RequestBody,
//        @Part(value = "OfferLocation") OfferLocation: RequestBody,
//        @Part(value = "Gender") Gender: RequestBody,
//        @Part(value = "MinAgeLim") MinAgeLim: RequestBody,
//        @Part(value = "MaxAgeLim") MaxAgeLim: RequestBody,
//        @Part(value = "TotalAvailCount") TotalAvailCount: RequestBody,
//        @Part(value = "MinFollowerCount") MinFollowerCount: RequestBody,
//        @Part(value = "MaxFollowerCount") MaxFollowerCount: RequestBody,
//        @Part(value = "OfferInvites") OfferInvites: RequestBody
//    ): MDContactSupport
//
//
//    @Multipart
//    @POST(URLS.updateOffer)
//    suspend fun updateOffer(
//        @Part(value = "Code") Code: RequestBody,
//        @Part(value = "Status") Status: RequestBody,
//        @Part(value = "OfferType") OfferType: RequestBody,
//        @Part(value = "Title") Title: RequestBody,
//        @Part(value = "OfferCategory") OfferCategory: RequestBody,
//        @Part(value = "Description") Description: RequestBody,
//        @Part(value = "ExpiryDate") ExpiryDate: RequestBody,
//        @Part offerFiles: List<MultipartBody.Part>?,
//        @Part(value = "IsUserRetrictedToUploadImage") IsUserRetrictedToUploadImage: RequestBody,
//        @Part(value = "Latitude") Latitude: RequestBody,
//        @Part(value = "Longitude") Longitude: RequestBody,
//        @Part(value = "OfferLocation") OfferLocation: RequestBody,
//        @Part(value = "Gender") Gender: RequestBody,
//        @Part(value = "MinAgeLim") MinAgeLim: RequestBody,
//        @Part(value = "MaxAgeLim") MaxAgeLim: RequestBody,
//        @Part(value = "TotalAvailCount") TotalAvailCount: RequestBody,
//        @Part(value = "MinFollowerCount") MinFollowerCount: RequestBody,
//        @Part(value = "MaxFollowerCount") MaxFollowerCount: RequestBody,
//        @Part(value = "OfferInvites") OfferInvites: RequestBody?
//    ): MDContactSupport
//
//
//    @POST(URLS.getActiveOffers)
//    suspend fun getActiveOffers(
//        @Body data: JsonObject
//    ): MDActiveOffers
//
//    @POST(URLS.contactSupport)
//    suspend fun contactSupport(
//        @Body data: JsonObject
//    ): MDContactSupport
//
//    @GET(URLS.faqs)
//    suspend fun getFAQs(
//    ): FAQsModel
//
//    @POST(URLS.updatePassword)
//    suspend fun updatePassword(
//        @Body data: JsonObject
//    ): MDContactSupport
//
//    @POST(URLS.forgotPassword)
//    suspend fun forgotPassword(
//        @Body data: JsonObject
//    ): MDContactSupport
//
//    @GET(URLS.deleteOffer)
//    suspend fun deleteOffer(
//        @Query(value = "code", encoded = true) code: String,
//    ): MDContactSupport
//
//    @GET(URLS.scanQR)
//    suspend fun scanQRCode(
//        @Query(value = "offerId", encoded = true) offerId: String,
//        @Query(value = "qrCode", encoded = true) qrCode: Int,
//    ): MDOfferScanned
//
//    @POST(URLS.offerAvailed)
//    suspend fun offerAvailed(
//        @Body data: JsonObject
//    ): MDContactSupport
//
//
//    @GET(URLS.getReviews)
//    suspend fun getReviews(
//        @Query(value = "offerId", encoded = true) offerId: String,
//        @Query(value = "userId", encoded = true) userId: String,
//    ): MDReviews
//
//    @POST(URLS.getClients)
//    suspend fun getClients(
//        @Body data: JsonObject
//    ): MDClients
//
//    @GET(URLS.generateQRCode)
//    suspend fun generateQR(
//        @Query(value = "offerId", encoded = true) offerId: String
//    ): MDQR
//
//    @POST(URLS.clientOfferStatus)
//    suspend fun clientOfferStatus(
//        @Body data: JsonObject
//    ): MDContactSupport
//
//
//    @POST(URLS.influencerOfferStatus)
//    suspend fun influencerOfferStatus(
//        @Body data: JsonObject
//    ): MDContactSupport
//
//
//    @POST(URLS.getNotifications)
//    suspend fun getNotifications(
//        @Body data: JsonObject
//    ): MDNotifications
//
//    @POST(URLS.addReferenceLink)
//    suspend fun addReferenceLink(
//        @Body data: JsonObject
//    ): MDContactSupport
//
//    @POST(URLS.addReviews)
//    suspend fun addReview(
//        @Body data: JsonObject
//    ): MDContactSupport


}