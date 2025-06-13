package com.dave.imagebucket.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoDocument(
    @SerialName("thumbnail") val thumbnailUrl: String,
    @SerialName("datetime") val datetime: String
)
