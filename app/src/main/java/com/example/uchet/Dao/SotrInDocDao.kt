package com.example.uchet.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.uchet.entities.SotrudnikiInDocument
import com.example.uchet.entities.SotrudnikiWithDocFields
import kotlinx.coroutines.flow.Flow

@Dao
interface SotrInDocDao {
    @Query("SELECT " +
            "sotrudniki_in_document.id, " +
            "sotrudniki_in_document.id_sotrudnik, "+
            "sotrudniki_in_document.id_doc, "+
            "sotrudniki_in_document.id_venue_in_doc, "+
            "sotrudniki_in_document.id_venue_fact, "+
            "sotrudniki_in_document.route, "+
            "sotrudniki_in_document.mark, "+
            "sotrudniki_in_document.available_in_doc, "+
            "sotrudniki.name, "+
            "sotrudniki.surname, "+
            "sotrudniki.patronymic, "+
            "sotrudniki.uid"+
            " FROM sotrudniki_in_document" +
            " INNER JOIN sotrudniki" +
            " ON sotrudniki_in_document.id_sotrudnik = sotrudniki.id" +
            " WHERE sotrudniki_in_document.id_doc = :id_doc")
    fun getById(id_doc: Int): Flow<List<SotrudnikiWithDocFields>>

    @Query("SELECT * FROM sotrudniki_in_document INNER JOIN sotrudniki ON sotrudniki_in_document.id_sotrudnik = sotrudniki.id WHERE sotrudniki_in_document.id_doc = :id_doc")
    fun getAllSotrInDoc(id_doc: Int): Flow<List<SotrudnikiWithDocFields>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sotrudnikiInDocument: SotrudnikiInDocument)

    @Query("UPDATE sotrudniki_in_document SET mark=:mark WHERE id_sotrudnik=:id")
    suspend fun changeMark(mark: Boolean,id: String)

    @Query("SELECT * FROM sotrudniki_in_document")
    fun getAll(): List<SotrudnikiInDocument>

    @Delete
    suspend fun delete(sotrudnikiInDocument: SotrudnikiInDocument)

    @Query("DELETE FROM sotrudniki_in_document")
    suspend fun deleteAll()

    @Query("DELETE FROM sotrudniki_in_document WHERE available_in_doc=0 AND id_doc= :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM sotrudniki_in_document WHERE id_sotrudnik = :id")
    suspend fun deleteSotrInDoc(id: String)

    @Query("UPDATE sotrudniki_in_document SET id_venue_fact=:id_venue WHERE id_sotrudnik=:id")
    suspend fun updateVenue(id: String,id_venue: Int)

    @Query("SELECT COUNT(*) FROM sotrudniki_in_document WHERE id_sotrudnik=:id")
    suspend fun getByUid(id: Long): Int

    @Update
    suspend fun update(sotrudnikiInDocument: SotrudnikiInDocument)

    @Query("SELECT COUNT(*) FROM sotrudniki_in_document")
    suspend fun getRowCount(): Int

    @Query("UPDATE sotrudniki_in_document SET mark=0 WHERE id_doc=:id")
    suspend fun clearMarks(id: Int)
}