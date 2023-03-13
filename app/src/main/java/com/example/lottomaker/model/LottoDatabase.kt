package com.example.lottomaker.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    version = 1,
    entities = [
        WinningNumber::class
    ]
)
abstract class LottoDatabase: RoomDatabase() {
    abstract fun lottoDao(): LottoDao

    companion object {
        private const val  DB_NAME = "lotto.db"
        @Volatile private var INSTANCE: LottoDatabase? = null

        fun getDatabase(context: Context): LottoDatabase {
            return INSTANCE ?: synchronized(this){
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): LottoDatabase {
            return Room.databaseBuilder(context.applicationContext, LottoDatabase::class.java, DB_NAME)
                .allowMainThreadQueries()
                .build()
        }
    }
}