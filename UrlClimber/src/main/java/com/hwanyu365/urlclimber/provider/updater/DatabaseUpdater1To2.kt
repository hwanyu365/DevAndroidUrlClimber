package com.hwanyu365.urlclimber.provider.updater

import android.database.sqlite.SQLiteDatabase

internal class DatabaseUpdater1To2(nextUpdater: IDatabaseUpdater?) : DatabaseUpdater(nextUpdater) {

    override fun update(db: SQLiteDatabase, version: Int) {

    }
}