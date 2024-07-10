package id.andra.knowmyface.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import id.andra.knowmyface.model.Meta
import id.andra.knowmyface.model.Presence

@JsonClass(generateAdapter = true)
data class PresencesResponse(
    @Json(name = "data")
    val data: List<Presence>,
    @Json(name = "meta")
    val meta: Meta
)
