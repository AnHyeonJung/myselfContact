package com.myself.myselfContact.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.myself.myselfContact.data.dao.ContentDao
import com.myself.myselfContact.model.ContentEntity

@Database(entities = [ContentEntity::class], version = 3)
abstract class AppDatabase : RoomDatabase(){
    abstract fun contentDao() : ContentDao
}

val migration_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE Sound Add COLUMN isChecked INTEGER NOT NULL DEFAULT 0"
        )
    }
}