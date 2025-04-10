package com.example.myapplication

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Helper class for managing SQLite database operations.
 * Handles database creation and version management.
 */
class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        // Database metadata
        private const val DATABASE_NAME = "UserDatabase.db"
        private const val DATABASE_VERSION = 1

        // Table definitions
        const val TABLE_USERS = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_AGE = "age"

        // SQL statements
        private const val SQL_CREATE_TABLE = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT,
                $COLUMN_AGE INTEGER
            )
        """

        private const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_USERS"
    }

    /**
     * Called when the database is created for the first time.
     * Creates tables and performs any initial setup.
     */
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE)
    }

    /**
     * Called when the database needs to be upgraded.
     * Drops existing tables and recreates them.
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This simple implementation drops and recreates tables
        db.execSQL(SQL_DELETE_TABLE)
        onCreate(db)
    }

    /**
     * Called when the database needs to be downgraded.
     * Uses the same implementation as upgrade.
     */
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
}