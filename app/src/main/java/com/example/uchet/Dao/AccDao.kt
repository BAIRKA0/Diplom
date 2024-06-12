package com.example.uchet.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.uchet.entities.Acc
import kotlinx.coroutines.flow.Flow

@Dao
interface AccDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: Acc)
    @Query("SELECT * FROM accounts")
    fun getAllAccount(): Flow<List<Acc>>
    @Delete
    suspend fun deleteAccount(account: Acc)
    @Query("DELETE FROM accounts")
    suspend fun deleteAllAccount()
    @Update
    suspend fun updateAccount(account: Acc)
    @Query("SELECT COUNT(*) FROM accounts")
    suspend fun getRowCount(): Int
    @Query("SELECT COUNT(*) FROM accounts WHERE login = :login AND password = :password")
    suspend fun checkAcc(login: String, password: String): Int
    @Query("SELECT COUNT(*) FROM accounts WHERE uid = :uid")
    suspend fun getAccByPass(uid: String): Int
}