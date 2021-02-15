package com.hwanyu365.urlclimber.provider

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteException
import android.net.Uri
import android.provider.BaseColumns
import android.provider.BaseColumns._ID

import com.hwanyu365.urlclimber.util.Log

object UrlClimberContract {

    private val TAG = "UrlClimberContract"

    internal val AUTHORITY = "com.hwanyu365.urlclimber"
    internal val AUTHORITY_URI = Uri.parse("content://$AUTHORITY")

    class History : BaseColumns {
        companion object {
            val ID = _ID
            val TABLE = "History"
            val PATH = TABLE

            internal val CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, TABLE)
            internal val CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.hwanyu365.urlclimber." + TABLE
            internal val CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.hwanyu365.urlclimber." + TABLE

            private val COL_PRE_FIX = "col_pre_fix"
            private val COL_MIDDLE = "col_middle"
            private val COL_POST_FIX = "col_post"

            internal val SQL_CREATE_TABLE = ("CREATE TABLE "
                    + TABLE + "("
                    + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_PRE_FIX + " TEXT NOT NULL, "
                    + COL_MIDDLE + " TEXT NOT NULL, "
                    + COL_POST_FIX + " TEXT NOT NULL" + ");")

            fun insert(context: Context, prefix: String, middle: String, postFix: String): Uri? {
                return try {
                    val cr = context.contentResolver

                    val values = ContentValues()
                    values.put(COL_PRE_FIX, prefix)
                    values.put(COL_MIDDLE, middle)
                    values.put(COL_MIDDLE, postFix)

                    cr.insert(CONTENT_URI, values)
                } catch (e: SQLiteException) {
                    e.printStackTrace()
                    null
                }
            }

            fun update(context: Context, id: Int, prefix: String, middle: String, postFix: String): Int {
                return try {
                    val cr = context.contentResolver

                    val values = ContentValues()
                    values.put(COL_PRE_FIX, prefix)
                    values.put(COL_MIDDLE, middle)
                    values.put(COL_MIDDLE, postFix)

                    val selection = "$ID=?"
                    val selectionArgs = arrayOf(id.toString())

                    cr.update(CONTENT_URI, values, selection, selectionArgs)
                } catch (e: SQLiteException) {
                    e.printStackTrace()
                    -1
                }
            }

            fun delete(context: Context, id: Int): Int {
                return delete(context, intArrayOf(id))
            }

            fun delete(context: Context, ids: IntArray?): Int {
                if (ids == null) {
                    Log.d(TAG, "invalid arguments")
                    return -1
                }

                return try {
                    val cr = context.contentResolver

                    val selection = "$ID=?"
                    val selectionArgs = arrayOfNulls<String>(ids.size)

                    for (i in ids.indices) {
                        selectionArgs[i] = ids[i].toString()
                    }

                    cr.delete(CONTENT_URI, selection, selectionArgs)
                } catch (e: SQLiteException) {
                    e.printStackTrace()
                    -1
                }

            }
        }
    }
}
