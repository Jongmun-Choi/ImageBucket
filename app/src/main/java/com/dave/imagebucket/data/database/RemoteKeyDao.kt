package com.dave.imagebucket.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(remoteKey: RemoteKey)

    @Query("SELECT * FROM remote_keys WHERE `query` = :query")
    suspend fun getByQuery(query: String): RemoteKey?

    @Query("DELETE FROM remote_keys WHERE `query` = :query")
    suspend fun deleteByQuery(query: String)
}