package com.example.uchet.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.uchet.entities.Sotrudnik
import kotlinx.coroutines.flow.Flow

@Dao
interface SotrudnikDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sotrudnik: Sotrudnik)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(employees: List<Sotrudnik>)

    @Query("SELECT * FROM sotrudniki")
    fun getAll(): Flow<List<Sotrudnik>>

    @Query("SELECT * FROM sotrudniki where uid = :uid")
    suspend fun getByUID(uid: Long): Sotrudnik?

    @Query("SELECT * FROM sotrudniki where id = :id")
    suspend fun getByID(id: String): Sotrudnik?

    @Update
    suspend fun update(sotrudnik: Sotrudnik)

    @Delete
    suspend fun delete(sotrudnik: Sotrudnik)

    @Query("DELETE FROM sotrudniki")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM sotrudniki")
    suspend fun getRowCount(): Int

    @Query("SELECT id FROM sotrudniki ORDER BY RANDOM() LIMIT :i")
    suspend fun getIds(i: Int): List<String>
}