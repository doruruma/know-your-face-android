package id.andra.knowmyface.api.repository

import android.util.Log
import id.andra.knowmyface.api.Resource
import id.andra.knowmyface.api.RetrofitClient
import id.andra.knowmyface.api.request.PresenceRequest
import id.andra.knowmyface.api.response.MessageResponse
import id.andra.knowmyface.api.response.PresenceResponse
import id.andra.knowmyface.api.response.PresencesResponse
import id.andra.knowmyface.extension.handleThrowable
import id.andra.knowmyface.helper.DateUtil
import id.andra.knowmyface.helper.ParamUtil

class PresenceRepository {

    suspend fun getPresences(page: Int): Resource<PresencesResponse> {
        return try {
            val response = RetrofitClient.apiService.getPresences(page = page)
            Resource.Success(response)
        } catch (e: Throwable) {
            Log.d("PRESENCE-ERROR", "${e.message}")
            Resource.Error(error = e.handleThrowable())
        }
    }

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

    suspend fun clockIn(request: PresenceRequest): Resource<PresenceResponse> {
        return try {
            val response = RetrofitClient.apiService.presenceClockIn(
                userId = ParamUtil.createPartFromString("${request.userId}"),
                longitude = ParamUtil.createPartFromString("${request.longitude}"),
                latitude = ParamUtil.createPartFromString("${request.latitude}"),
                photo = request.photo?.let {
                    ParamUtil.prepareFilePart(
                        partName = "face_image_clock_in",
                        fileName = "presence-in-${DateUtil.getCurrentDateTime()}.jpg",
                        fileBytes = it
                    )
                }
            )
            Resource.Success(response)
        } catch (e: Throwable) {
            val failure = e.handleThrowable()
            Resource.Error(
                error = failure,
                code = failure.httpCode
            )
        }
    }

    suspend fun clockOut(request: PresenceRequest): Resource<PresenceResponse> {
        return try {
            val response = RetrofitClient.apiService.presenceClockOut(
                userId = ParamUtil.createPartFromString("${request.userId}"),
                longitude = ParamUtil.createPartFromString("${request.longitude}"),
                latitude = ParamUtil.createPartFromString("${request.latitude}"),
                photo = request.photo?.let {
                    ParamUtil.prepareFilePart(
                        partName = "face_image_clock_out",
                        fileName = "presence-out-${DateUtil.getCurrentDateTime()}.jpg",
                        fileBytes = it
                    )
                }
            )
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