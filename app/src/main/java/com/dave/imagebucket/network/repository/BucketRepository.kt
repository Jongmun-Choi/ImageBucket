package com.dave.imagebucket.network.repository

import com.dave.imagebucket.data.database.BucketItemEntity
import com.dave.imagebucket.data.database.BucketItemDao
import com.dave.imagebucket.data.model.ImageItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BucketRepository @Inject constructor(private val bucketDao: BucketItemDao) {

    fun getBucketItems(): Flow<List<BucketItemEntity>> = bucketDao.getAllItems()

    suspend fun saveItem(item: ImageItem) {
        val lockerItem = BucketItemEntity(
            thumbnailUrl = item.thumbnailUrl,
            dateTime = item.dateTime
        )
        bucketDao.insert(lockerItem)
    }

    suspend fun deleteItem(id: String) {
        bucketDao.delete(id)
    }

    fun isItemSaved(id: String): Flow<Boolean> = bucketDao.isSaved(id)
}