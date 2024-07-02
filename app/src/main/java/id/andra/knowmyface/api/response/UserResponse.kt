package id.andra.knowmyface.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import id.andra.knowmyface.model.User

@JsonClass(generateAdapter = true)
data class UserResponse(
    @Json(name = "data")
    val data: User? = null
)
