package id.andra.knowmyface.model

import com.squareup.moshi.Json

data class Position(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    @Json(name = "created_at") val createdAt: String = "",
    @Json(name = "updated_at") val updatedAt: String = "",
    @Json(name = "deleted_at") val deletedAt: String? = null
)
