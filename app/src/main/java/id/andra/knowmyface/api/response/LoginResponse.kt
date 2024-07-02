package id.andra.knowmyface.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "access_token")
    val token: String,
    @Json(name = "refresh_token")
    val refreshToken: String
)

@JsonClass(generateAdapter = true)
data class LoginError(
    @Json(name = "username")
    val email: List<String> = listOf(""),
    @Json(name = "password")
    val password: List<String> = listOf("")
)