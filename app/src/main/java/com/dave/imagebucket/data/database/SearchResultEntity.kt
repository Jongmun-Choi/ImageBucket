package com.dave.imagebucket.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_results")
data class SearchResultEntity(
    @PrimaryKey val id: String,
    val query: String, // 어떤 검색어에 대한 결과인지 저장
    val thumbnailUrl: String,
    val dateTime: String,
    val itemIndex: Int // Paging 순서를 위한 인덱스
)