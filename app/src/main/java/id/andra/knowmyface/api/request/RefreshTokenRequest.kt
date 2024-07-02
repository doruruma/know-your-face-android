package id.andra.knowmyface.api.request

import com.squareup.moshi.Json

data class RefreshTokenRequest(
    @Json(name = "refresh_token")
    val refreshToken: String = ""
)
