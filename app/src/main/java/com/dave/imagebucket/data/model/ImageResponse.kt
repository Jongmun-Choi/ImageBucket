package com.dave.imagebucket.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageResponse(
    @SerialName("documents") val documents: List<ImageDocument>
)
