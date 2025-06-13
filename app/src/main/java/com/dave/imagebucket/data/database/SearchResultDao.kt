package com.dave.imagebucket.data.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SearchResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(results: List<SearchResultEntity>)

    // 쿼리에 해당하는 PagingSource를 반환 (Room이 페이징 처리)
    @Query("SELECT * FROM search_results WHERE `query` = :query ORDER BY itemIndex ASC")
    fun pagingSource(query: String): PagingSource<Int, SearchResultEntity>

    @Query("DELETE FROM search_results WHERE `query` = :query")
    suspend fun clearByQuery(query: String)

    @Query("SELECT MAX(itemIndex) FROM search_results WHERE `query` = :query")
    suspend fun getLastItemIndex(query: String): Int?
}