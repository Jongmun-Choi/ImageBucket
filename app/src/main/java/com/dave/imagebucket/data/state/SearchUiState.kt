package com.dave.imagebucket.data.state

import com.dave.imagebucket.data.model.ImageItem

data class SearchUiState(
    val query: String = "",
    val results: List<ImageItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)