package id.andra.knowmyface.api.response

import com.squareup.moshi.Json

data class MessageResponse(
    @Json(name = "message")
    val message: String = ""
)