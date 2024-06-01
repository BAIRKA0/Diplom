package com.example.uchet.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.uchet.entities.Transport
import kotlinx.coroutines.flow.Flow

@Dao
interface TransportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transport: Transport)

    @Query("SELECT * FROM transport")
    suspend fun getAll(): List<Transport>

    @Query("SELECT * FROM transport where id = :id")
    fun get(id: Int): Transport?

    @Query("SELECT name FROM transport where id = :id")
    suspend fun getName(id: Int): String

    @Delete
    suspend fun delete(transport: Transport)

    @Query("DELETE FROM transport")
    suspend fun deleteAll()

    @Update
    suspend fun update(transport: Transport)

    @Query("SELECT COUNT(*) FROM transport")
    suspend fun getRowCount(): Int
}