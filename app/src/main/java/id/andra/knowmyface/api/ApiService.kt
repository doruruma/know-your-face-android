package id.andra.knowmyface.api

import id.andra.knowmyface.api.request.LoginRequest
import id.andra.knowmyface.api.request.RefreshTokenRequest
import id.andra.knowmyface.api.response.LoginResponse
import id.andra.knowmyface.api.response.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @POST("refresh-token")
    suspend fun refreshToken(@Body refreshTokenRequest: RefreshTokenRequest): LoginResponse

    @GET("user/current")
    suspend fun getCurrentUser(): UserResponse

}