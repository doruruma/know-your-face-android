package id.andra.knowmyface.extension

import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

sealed class Failure(
    open val httpCode: Int? = null
) : IOException() {
    data object UnknownError : Failure() {
        private fun readResolve(): Any = UnknownError
    }

    data class ApiError(
        override var httpCode: Int = 0,
        override var message: String
    ) : Failure()

    data class ValidationException(
        override var httpCode: Int = 0,
        override var message: String
    ) : Failure()
}

fun Throwable.handleThrowable(): Failure {
    if (this is HttpException) {
        val response = this.response()?.errorBody()?.string()
        val errors = StringBuilder()
        val message = StringBuilder()
        response?.let {
            try {
                errors.append(JSONObject(it).getString("errors"))
            } catch (_: JSONException) {
            }
            try {
                message.append(JSONObject(it).getString("message"))
            } catch (_: JSONException) {
            }
        }
        return if (this.code() == 422)
            Failure.ValidationException(
                httpCode = this.code(),
                message = errors.toString()
            )
        else
            Failure.ApiError(
                httpCode = this.code(),
                message = message.toString()
            )
    } else {
        return Failure.UnknownError
    }
}
