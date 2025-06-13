package com.dave.imagebucket.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dave.imagebucket.data.database.BucketItemEntity
import com.dave.imagebucket.network.repository.BucketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BucketViewModel @Inject constructor(
    private val bucketRepository: BucketRepository
) : ViewModel() {

    val bucketItems: StateFlow<List<BucketItemEntity>> = bucketRepository.getBucketItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun removeItem(item: BucketItemEntity) {
        viewModelScope.launch {
            bucketRepository.deleteItem(item.thumbnailUrl)
        }
    }
}