package com.example.uchet.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.uchet.Dao.AccDao
import com.example.uchet.Dao.DepartureDao
import com.example.uchet.Dao.DocumentDao
import com.example.uchet.Dao.SotrInDocDao
import com.example.uchet.Dao.SotrudnikDao
import com.example.uchet.Dao.TransportDao
import com.example.uchet.converters.DateConverter
import com.example.uchet.entities.Acc
import com.example.uchet.entities.Departure
import com.example.uchet.entities.Sotrudnik
import com.example.uchet.entities.Document
import com.example.uchet.entities.SotrudnikiInDocument
import com.example.uchet.entities.Transport

@TypeConverters(value = [DateConverter::class])
@Database (
    entities = [
        Document::class,
        Acc::class,
        Sotrudnik::class,
        SotrudnikiInDocument::class,
        Departure::class,
        Transport::class,
               ],
    version = 1,
    exportSchema = false
)
abstract class MainDB : RoomDatabase() {
    abstract fun documentDao(): DocumentDao
    abstract fun accDao(): AccDao
    abstract fun departureDao(): DepartureDao
    abstract fun transportDao(): TransportDao
    abstract fun sotrudnikDao(): SotrudnikDao
    abstract fun sotrInDocDao(): SotrInDocDao

    companion object{
        @Volatile
        private var INSTANCE: MainDB? = null
        fun createUchetDB(context: Context): MainDB{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context,
                    MainDB::class.java,
              "uchet"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}