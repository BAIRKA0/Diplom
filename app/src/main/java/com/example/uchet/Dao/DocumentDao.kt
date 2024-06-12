package com.example.uchet.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.uchet.entities.Document
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface DocumentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(document: Document)

    @Query("SELECT * FROM documents")
    fun getAll(): Flow<List<Document>>

//    @Query("SELECT * FROM documents where ")
//    fun getByDate(): Flow<List<Document>>

    @Query("SELECT * FROM documents where id = :id")
    suspend fun get(id: Int): Document?

    @Query("SELECT * FROM documents where departure_date = :departureDate")
    fun getDocsByDate(departureDate: Date): Flow<List<Document>>

    @Query("SELECT id FROM documents where distribution = :distribution AND departure_date = :departureDate AND transport = :transport AND venues = :venues")
    suspend fun getId(distribution: String, departureDate: Date, transport: String, venues: String): Int?

    @Delete
    suspend fun delete(document: Document)

    @Query("DELETE FROM documents")
    suspend fun deleteAll()

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(document: Document)

    @Query("SELECT COUNT(*) FROM documents")
    suspend fun getRowCount(): Int
}