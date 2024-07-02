package id.andra.knowmyface.api.request

import com.squareup.moshi.Json

data class LoginRequest(
    @Json(name = "username")
    val email: String = "",
    @Json(name = "password")
    val password: String = ""
)