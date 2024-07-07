package id.andra.knowmyface.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Presence(
    @Json(name = "id")
    val id: Int = 0,
    @Json(name = "user_id")
    val userId: Int = 0,
    @Json(name = "user")
    val user: User = User(),
    @Json(name = "schedule_time_in")
    val scheduleTimeIn: String = "",
    @Json(name = "schedule_time_out")
    val scheduleTimeOut: String = "",
    @Json(name = "time_in")
    val timeIn: String = "",
    @Json(name = "time_out")
    val timeOut: String? = null,
    @Json(name = "longitude_clock_in")
    val longitudeClockIn: String = "",
    @Json(name = "longitude_clock_out")
    val longitudeClockOut: String? = null,
    @Json(name = "latitude_clock_in")
    val latitudeClockIn: String = "",
    @Json(name = "latitude_clock_out")
    val latitudeClockOut: String? = null,
    @Json(name = "clock_in_distance")
    val clockInDistance: Double? = null,
    @Json(name = "clock_out_distance")
    val clockOutDistance: Double? = null,
    @Json(name = "face_image_clock_in")
    val faceImageClockIn: String = "",
    @Json(name = "face_image_clock_out")
    val faceImageClockOut: String? = null,
    @Json(name = "is_remote")
    val isRemote: Int = 0,
    @Json(name = "created_at")
    val createdAt: String = "",
    @Json(name = "updated_at")
    val updatedAt: String = ""
)
