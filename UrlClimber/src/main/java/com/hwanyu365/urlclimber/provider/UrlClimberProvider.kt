package com.hwanyu365.urlclimber.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.text.TextUtils

class UrlClimberProvider : ContentProvider() {

    private var mDbHelper: UrlClimberDbHelper? = null
    private val HISTORY = 0
    private val HISTORY_ID = 1
    private val mUriMatcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    override fun onCreate(): Boolean {
        mDbHelper = UrlClimberDbHelper(context)
        mUriMatcher.addURI(UrlClimberContract.AUTHORITY, UrlClimberContract.History.PATH, HISTORY)
        mUriMatcher.addURI(UrlClimberContract.AUTHORITY, UrlClimberContract.History.PATH + "/#", HISTORY_ID)
        return true
    }

    override fun getType(uri: Uri): String? {
        when (mUriMatcher.match(uri)) {
            HISTORY -> return UrlClimberContract.History.CONTENT_TYPE
            HISTORY_ID -> return UrlClimberContract.History.CONTENT_ITEM_TYPE
            else -> throw IllegalArgumentException("Unknown URI")
        }
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        return getSQLiteDatabase(false)?.query(getTableName(uri), projection, concatenateSelection(uri, selection), selectionArgs, null, null, sortOrder)
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val rowId: Long? = getSQLiteDatabase(true)?.insert(getTableName(uri), null, values)
        return if (rowId!! < 0) {
            null
        } else ContentUris.withAppendedId(getContentUri(uri), rowId)
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        return getSQLiteDatabase(true)?.update(getTableName(uri), values, concatenateSelection(uri, selection), selectionArgs)
                ?: 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return getSQLiteDatabase(true)?.delete(getTableName(uri), concatenateSelection(uri, selection), selectionArgs)
                ?: 0
    }

    private fun getSQLiteDatabase(isWritable: Boolean): SQLiteDatabase? {
        return if (isWritable) mDbHelper?.getWritableDatabase() else mDbHelper?.getReadableDatabase()
    }

    private fun getContentUri(uri: Uri): Uri {
        when (mUriMatcher.match(uri)) {
            HISTORY, HISTORY_ID -> return UrlClimberContract.History.CONTENT_URI
            else -> throw IllegalArgumentException()
        }
    }

    private fun getTableName(uri: Uri): String {
        when (mUriMatcher.match(uri)) {
            HISTORY, HISTORY_ID -> return UrlClimberContract.History.TABLE
            else -> throw IllegalArgumentException()
        }
    }

    private fun concatenateSelection(uri: Uri, oldSelection: String?): String {
        val rtSelection = StringBuilder()
        val strColId: String

        when (mUriMatcher.match(uri)) {
            HISTORY, HISTORY_ID -> strColId = UrlClimberContract.History.ID
            else -> throw IllegalArgumentException("Unknown URI $uri")
        }

        rtSelection.append(strColId)
        rtSelection.append(" = '")
        rtSelection.append(uri.pathSegments[1])
        rtSelection.append("'")

        if (!TextUtils.isEmpty(oldSelection)) {
            rtSelection.append(" AND (")
            rtSelection.append(oldSelection)
            rtSelection.append(")")
        }

        return rtSelection.toString()
    }
}