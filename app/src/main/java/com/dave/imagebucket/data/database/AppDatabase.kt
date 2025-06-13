package com.dave.imagebucket.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dave.imagebucket.data.model.ImageItem

@Database(
    entities = [BucketItemEntity::class, RemoteKey::class, SearchResultEntity::class],
    version = 1,
)

abstract class AppDatabase: RoomDatabase() {
    abstract fun bucketItemDao(): BucketItemDao
    abstract fun remoteKeyDao(): RemoteKeyDao
    abstract fun searchResultDao(): SearchResultDao
}