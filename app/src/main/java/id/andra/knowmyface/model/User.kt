package id.andra.knowmyface.model

import com.squareup.moshi.Json

data class User(
    val id: Int? = 0,
    @Json(name = "position_id")
    val positionId: Int? = 0,
    val position: Position = Position(),
    val nik: String? = "",
    val name: String? = "",
    val phone: String? = "",
    val gender: String? = "",
    val email: String? = "",
    @Json(name = "formatted_gender")
    val formattedGender: String? = "",
    @Json(name = "profile_image")
    val profileImage: String? = "",
    @Json(name = "face_image")
    val faceImage: String? = null,
    @Json(name = "created_at")
    val createdAt: String? = "",
    @Json(name = "updated_at")
    val updatedAt: String? = ""
)
