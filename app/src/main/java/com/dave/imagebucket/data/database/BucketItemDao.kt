package com.dave.imagebucket.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BucketItemDao {
    @Query("SELECT * FROM bucket_items ORDER BY savedTimestamp ASC")
    fun getAllItems(): Flow<List<BucketItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: BucketItemEntity)

    @Query("DELETE FROM bucket_items WHERE thumbnailUrl = :thumbnailUrl")
    suspend fun delete(thumbnailUrl: String)

    @Query("SELECT EXISTS(SELECT 1 FROM bucket_items WHERE thumbnailUrl = :thumbnailUrl)")
    fun isSaved(thumbnailUrl: String): Flow<Boolean>
}