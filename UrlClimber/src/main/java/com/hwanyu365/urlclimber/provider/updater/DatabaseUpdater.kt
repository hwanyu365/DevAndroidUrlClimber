package com.hwanyu365.urlclimber.provider.updater

import android.database.sqlite.SQLiteDatabase

internal abstract class DatabaseUpdater(val mNextUpdater: IDatabaseUpdater?) : IDatabaseUpdater {

    fun addColumn(db: SQLiteDatabase, table: String, column: String, type: String) {
        db.execSQL("ALERT $table ADD $column $type")
    }
}
