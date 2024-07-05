package id.andra.knowmyface.api.repository

import id.andra.knowmyface.api.Resource
import id.andra.knowmyface.api.RetrofitClient
import id.andra.knowmyface.api.response.MessageResponse
import id.andra.knowmyface.extension.handleThrowable

class PresenceRepository {

    suspend fun checkStatus(): Resource<MessageResponse> {
        return try {
            val response = RetrofitClient.apiService.checkPresenceStatus()
            Resource.Success(response)
        } catch (e: Throwable) {
            val failure = e.handleThrowable()
            Resource.Error(
                error = failure,
                code = failure.httpCode
            )
        }
    }

}