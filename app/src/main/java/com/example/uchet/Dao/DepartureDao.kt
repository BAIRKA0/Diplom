package com.example.uchet.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.uchet.entities.Departure
import kotlinx.coroutines.flow.Flow

//venue = departure
@Dao
interface DepartureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(departure: Departure)

    @Query("SELECT * FROM departure")
    suspend fun getAll(): List<Departure>

    @Query("SELECT name FROM departure where id = :id")
    suspend fun get(id: Int): String

    @Delete
    suspend fun delete(departure: Departure)

    @Query("DELETE FROM departure")
    suspend fun deleteAll()

    @Update
    suspend fun update(departure: Departure)

    @Query("SELECT COUNT(*) FROM departure")
    suspend fun getRowCount(): Int
}