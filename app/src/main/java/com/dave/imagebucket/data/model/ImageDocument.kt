package com.dave.imagebucket.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageDocument(
    @SerialName("thumbnail_url") val thumbnailUrl: String,
    @SerialName("datetime") val datetime: String
)
