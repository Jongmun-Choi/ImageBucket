package com.dave.imagebucket.data.paging

import android.app.appsearch.SearchResult
import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import androidx.paging.PagingState
import com.dave.imagebucket.data.model.ImageItem
import com.dave.imagebucket.network.KakaoApiService
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.time.ZonedDateTime
import javax.inject.Inject

private const val KAKAO_STARTING_PAGE_INDEX = 1
const val PAGING_SIZE = 15 // API 호출 시 size 값

class SearchPagingSource @Inject constructor(
    private val query: String,
    private val apiService: KakaoApiService
) : PagingSource<Int, ImageItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageItem> {
        val page = params.key ?: KAKAO_STARTING_PAGE_INDEX
        return try {
            // 이미지와 비디오 검색을 동시에 요청
            val combinedResults = coroutineScope {
                val imageDeferred = async {
                    apiService.searchImages(query = query, page = page, size = PAGING_SIZE)
                }
                val videoDeferred = async {
                    apiService.searchVideos(query = query, page = page, size = PAGING_SIZE)
                }

                val imageResults = imageDeferred.await().documents.map {
                    ImageItem(
                        thumbnailUrl = it.thumbnailUrl,
                        dateTime = it.datetime
                    )
                }
                val videoResults = videoDeferred.await().documents.map {
                    ImageItem(
                        thumbnailUrl = it.thumbnailUrl,
                        dateTime = it.datetime
                    )
                }

                (imageResults + videoResults).sortedByDescending { it.dateTime }
            }

            LoadResult.Page(
                data = combinedResults,
                prevKey = if (page == KAKAO_STARTING_PAGE_INDEX) null else page - 1,
                nextKey = if (combinedResults.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ImageItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}