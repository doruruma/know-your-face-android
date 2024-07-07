package id.andra.knowmyface.api.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PresenceRequest(
    @Json(name = "user_id")
    val userId: String? = null,
    @Json(name = "longitude")
    val longitude: String? = null,
    @Json(name = "latitude")
    val latitude: String? = null,
    @Json(name = "photo")
    val photo: List<Byte>? = null,
)
