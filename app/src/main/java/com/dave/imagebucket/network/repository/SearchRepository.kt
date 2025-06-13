package com.dave.imagebucket.network.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.dave.imagebucket.data.database.AppDatabase
import com.dave.imagebucket.data.model.ImageItem
import com.dave.imagebucket.data.model.toImageItem
import com.dave.imagebucket.data.paging.SearchRemoteMediator
import com.dave.imagebucket.network.KakaoApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Singleton
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
@Singleton
class SearchRepository @Inject constructor(
    private val apiService: KakaoApiService,
    private val database: AppDatabase
)
{

    fun getSearchResultsStream(query: String): Flow<PagingData<ImageItem>> {
        // DB를 PagingSource로 사용
        val pagingSourceFactory = { database.searchResultDao().pagingSource(query) }

        return Pager(
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders = false
            ),
            remoteMediator = SearchRemoteMediator( // RemoteMediator 설정
                query = query,
                database = database,
                apiService = apiService
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { pagingData ->
            // DB Entity를 UI Model로 변환
            pagingData.map { it.toImageItem() }
        }
    }
}