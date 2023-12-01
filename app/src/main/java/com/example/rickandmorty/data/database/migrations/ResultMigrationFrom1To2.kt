package com.example.rickandmorty.data.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class ResultMigrationFrom1To2 : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.run {
            execSQL("DROP TABLE IF EXISTS `rickandmortycharacters`")

            execSQL(
                "CREATE TABLE IF NOT EXISTS `rickandmortycharacters` (" +
                        "`created` TEXT, " +
                        "`episode` TEXT, " +
                        "`gender` TEXT, " +
                        "`id` INTEGER, " +
                        "`image` TEXT, " +
                        "`location` TEXT, " +
                        "`name` TEXT, " +
                        "`origin` TEXT, " +
                        "`species` TEXT, " +
                        "`status` TEXT, " +
                        "`type` TEXT, " +
                        "`url` TEXT, " +
                        "PRIMARY KEY(`id`))"
            )
        }
    }
}