package com.hwanyu365.urlclimber.provider

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.hwanyu365.urlclimber.provider.updater.DatabaseUpdater1To2
import com.hwanyu365.urlclimber.util.Log

private const val DB_NAME = "url_climber.db"
private const val DB_VERSION = 1

class UrlClimberDbHelper(context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    private val TAG = "ClimberDbHelper"

    override fun onCreate(db: SQLiteDatabase?) {
        Log.d(TAG, "")
        db?.execSQL(UrlClimberContract.History.SQL_CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "Database is updated $oldVersion to $newVersion")
        val updater1To2 = DatabaseUpdater1To2(null)
        updater1To2.update(db, oldVersion)
    }
}