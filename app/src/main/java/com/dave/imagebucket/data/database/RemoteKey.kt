package com.dave.imagebucket.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKey(
    @PrimaryKey val query: String,
    val nextPage: Int?,
    val lastUpdated: Long // 캐시 만료 시간을 체크하기 위함
)