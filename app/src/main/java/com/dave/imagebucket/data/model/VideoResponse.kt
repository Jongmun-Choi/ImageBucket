package com.dave.imagebucket.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoResponse(
    @SerialName("documents") val documents: List<VideoDocument>
)
