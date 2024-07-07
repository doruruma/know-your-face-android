package id.andra.knowmyface.api

import id.andra.knowmyface.api.request.LoginRequest
import id.andra.knowmyface.api.request.RefreshTokenRequest
import id.andra.knowmyface.api.response.LoginResponse
import id.andra.knowmyface.api.response.MessageResponse
import id.andra.knowmyface.api.response.PresenceResponse
import id.andra.knowmyface.api.response.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @POST("refresh-token")
    suspend fun refreshToken(@Body refreshTokenRequest: RefreshTokenRequest): LoginResponse

    @GET("user/current")
    suspend fun getCurrentUser(): UserResponse

    @GET("presence/check-status")
    suspend fun checkPresenceStatus(): MessageResponse

    @Multipart
    @POST("presence/clock-in")
    suspend fun presenceClockIn(
        @Part("user_id") userId: RequestBody,
        @Part("longitude_clock_in") longitude: RequestBody,
        @Part("latitude_clock_in") latitude: RequestBody,
        @Part photo: MultipartBody.Part?
    ): PresenceResponse

    @Multipart
    @POST("presence/clock-out")
    suspend fun presenceClockOut(
        @Part("user_id") userId: RequestBody,
        @Part("longitude_clock_out") longitude: RequestBody,
        @Part("latitude_clock_out") latitude: RequestBody,
        @Part photo: MultipartBody.Part?
    ): PresenceResponse

}