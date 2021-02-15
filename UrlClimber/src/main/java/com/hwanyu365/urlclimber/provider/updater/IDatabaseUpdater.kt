package com.hwanyu365.urlclimber.provider.updater

import android.database.sqlite.SQLiteDatabase

interface IDatabaseUpdater {
    fun update(db: SQLiteDatabase, version: Int)
}
