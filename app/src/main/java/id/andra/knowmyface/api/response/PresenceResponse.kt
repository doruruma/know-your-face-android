package id.andra.knowmyface.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import id.andra.knowmyface.model.Presence

@JsonClass(generateAdapter = true)
data class PresenceResponse(
    @Json(name = "data")
    val data: Presence? = null
)
