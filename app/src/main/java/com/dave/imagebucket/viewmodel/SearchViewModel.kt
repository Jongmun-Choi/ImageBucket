package com.dave.imagebucket.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.dave.imagebucket.data.model.ImageItem
import com.dave.imagebucket.data.state.SearchUiState
import com.dave.imagebucket.network.repository.BucketRepository
import com.dave.imagebucket.network.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val bucketRepository: BucketRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _currentQuery = MutableStateFlow("")

    val searchResults: StateFlow<PagingData<ImageItem>> = _currentQuery
        .flatMapLatest { query ->
            // 검색어가 있을 때만 Pager를 생성
            if (query.isNotBlank()) {
                searchRepository.getSearchResultsStream(query)
            } else {
                MutableStateFlow(PagingData.empty())
            }
        }
        .cachedIn(viewModelScope)
        // 검색 결과와 보관함 상태를 결합하여 isSaved 업데이트
        .combine(bucketRepository.getBucketItems()) { pagingData, bucketItems ->
            val saveUrls = bucketItems.map { it.thumbnailUrl }.toSet()
            pagingData.map { searchResult ->
                searchResult.copy(isSaved = saveUrls.contains(searchResult.thumbnailUrl))
            }
        }
        // ViewModel이 살아있는 동안 결과를 캐시

        .asStateFlow(initialValue = PagingData.empty(), scope = viewModelScope)


    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
    }

    fun search() {
        _currentQuery.value = _uiState.value.query
    }

    fun toggleSave(item: ImageItem) {
        viewModelScope.launch {
            if (item.isSaved) {
                bucketRepository.deleteItem(item.thumbnailUrl)
            } else {
                bucketRepository.saveItem(item)
            }
        }
    }
}

// StateFlow로 변환하기 위한 확장 함수
fun <T> Flow<T>.asStateFlow(
    initialValue: T,
    scope: CoroutineScope,
    started: SharingStarted = SharingStarted.WhileSubscribed(5000)
): StateFlow<T> = this.stateIn(scope, started, initialValue)