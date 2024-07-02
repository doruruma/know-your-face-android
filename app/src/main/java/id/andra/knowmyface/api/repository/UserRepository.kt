package id.andra.knowmyface.api.repository

import id.andra.knowmyface.api.Resource
import id.andra.knowmyface.api.RetrofitClient
import id.andra.knowmyface.api.request.LoginRequest
import id.andra.knowmyface.api.request.RefreshTokenRequest
import id.andra.knowmyface.api.response.LoginResponse
import id.andra.knowmyface.api.response.UserResponse
import id.andra.knowmyface.extension.handleThrowable

class UserRepository {

    suspend fun login(email: String, password: String): Resource<LoginResponse> {
        return try {
            val loginRequest = LoginRequest(email, password)
            val response = RetrofitClient.apiService.login(loginRequest)
            Resource.Success(response)
        } catch (e: Throwable) {
            val failure = e.handleThrowable()
            Resource.Error(
                error = failure,
                code = failure.httpCode
            )
        }
    }

    suspend fun refreshToken(refreshToken: String): Resource<LoginResponse> {
        return try {
            val refreshTokenRequest = RefreshTokenRequest(refreshToken)
            val response = RetrofitClient.apiService.refreshToken(refreshTokenRequest)
            Resource.Success(response)
        } catch (e: Throwable) {
            val failure = e.handleThrowable()
            Resource.Error(
                error = failure,
                code = failure.httpCode
            )
        }
    }

    suspend fun getCurrentUser(): Resource<UserResponse> {
        return try {
            val response = RetrofitClient.apiService.getCurrentUser()
            Resource.Success(response)
        } catch (e: Throwable) {
            Resource.Error(
                error = e.handleThrowable()
            )
        }
    }

}