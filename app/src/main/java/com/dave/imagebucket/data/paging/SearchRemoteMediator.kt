package com.dave.imagebucket.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.dave.imagebucket.data.database.AppDatabase
import com.dave.imagebucket.data.database.RemoteKey
import com.dave.imagebucket.data.database.SearchResultEntity
import com.dave.imagebucket.network.KakaoApiService
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

private const val CACHE_TIMEOUT_MINUTES = 5

@OptIn(ExperimentalPagingApi::class)
class SearchRemoteMediator(
    private val query: String,
    private val database: AppDatabase,
    private val apiService: KakaoApiService
) : RemoteMediator<Int, SearchResultEntity>() {

    private val searchResultDao = database.searchResultDao()
    private val remoteKeyDao = database.remoteKeyDao()

    override suspend fun initialize(): InitializeAction {
        val remoteKey = remoteKeyDao.getByQuery(query) ?: return InitializeAction.LAUNCH_INITIAL_REFRESH

        val cacheTimeout = java.util.concurrent.TimeUnit.MILLISECONDS.convert(CACHE_TIMEOUT_MINUTES.toLong(), java.util.concurrent.TimeUnit.MINUTES)

        // 5분이 지났으면 캐시를 무효화하고 새로고침
        return if (System.currentTimeMillis() - remoteKey.lastUpdated >= cacheTimeout) {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, SearchResultEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = remoteKeyDao.getByQuery(query)
                    remoteKey?.nextPage ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            // API 호출
            val combinedResults = coroutineScope {
                val imageDeferred = async { apiService.searchImages(query = query, page = page, size = 15) }
                val videoDeferred = async { apiService.searchVideos(query = query, page = page, size = 15) }
                // ... (결과를 합치고 정렬하는 로직은 PagingSource와 동일)
                val imageResults = imageDeferred.await().documents
                val videoResults = videoDeferred.await().documents
                (imageResults.map { it.thumbnailUrl to it.datetime } + videoResults.map { it.thumbnailUrl to it.datetime })
                    .sortedByDescending { it.second }
            }

            val endOfPaginationReached = combinedResults.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    searchResultDao.clearByQuery(query)
                    remoteKeyDao.deleteByQuery(query)
                }

                val lastIndex = searchResultDao.getLastItemIndex(query) ?: 0
                val nextKey = if (endOfPaginationReached) null else page + 1

                val entities = combinedResults.mapIndexed { index, pair ->
                    SearchResultEntity(
                        id = pair.first,
                        query = query,
                        thumbnailUrl = pair.first,
                        dateTime = pair.second,
                        itemIndex = lastIndex + index + 1
                    )
                }
                searchResultDao.insertAll(entities)
                remoteKeyDao.insert(RemoteKey(query, nextKey, System.currentTimeMillis()))
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}