package com.example.bloom.model.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bloom.model.model.entities.Discovery
import kotlinx.coroutines.flow.Flow

@Dao
interface DiscoveryDao {
    @Query("SELECT * FROM discoveries WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllDiscoveriesByUserId(userId: String): Flow<List<Discovery>>

    @Query("SELECT * FROM discoveries WHERE userId = :userId AND (name LIKE :query OR name LIKE :query) ORDER BY timestamp DESC")
    fun searchDiscoveries(userId: String, query: String): Flow<List<Discovery>>

    @Query("SELECT * FROM discoveries WHERE id = :id")
    suspend fun getDiscoveryById(id: String): Discovery?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiscovery(discovery: Discovery)

    @Query("DELETE FROM discoveries WHERE id = :id")
    suspend fun deleteDiscoveryById(id: String)
}