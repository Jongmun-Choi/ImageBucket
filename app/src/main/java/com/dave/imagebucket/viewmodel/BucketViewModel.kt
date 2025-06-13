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
    private val lockerRepository: BucketRepository
) : ViewModel() {

    val lockerItems: StateFlow<List<BucketItemEntity>> = lockerRepository.getBucketItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun removeItem(item: BucketItemEntity) {
        viewModelScope.launch {
            lockerRepository.deleteItem(item.thumbnailUrl)
        }
    }
}