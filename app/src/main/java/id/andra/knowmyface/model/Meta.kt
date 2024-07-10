package id.andra.knowmyface.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Meta(
    @Json(name = "total")
    val total: Int = 0,
    @Json(name = "count")
    val count: Int = 0,
    @Json(name = "per_page")
    val perPage: Int = 0,
    @Json(name = "current_page")
    val currentPage: Int = 0,
    @Json(name = "total_pages")
    val totalPages: Int = 0
)