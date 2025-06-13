package com.dave.imagebucket.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bucket_items")
data class BucketItemEntity(
    @PrimaryKey val thumbnailUrl: String,
    val dateTime: String,
    val savedTimestamp: Long = System.currentTimeMillis()
)