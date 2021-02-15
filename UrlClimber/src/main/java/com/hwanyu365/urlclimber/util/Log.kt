package com.hwanyu365.urlclimber.util

@Suppress("unused")
object Log {
    private val TAG_PREFIX = "[Hwanyu365]_"
    private val REAL_METHOD_POS = 2

    private val DEBUG = true

    private fun prefix(): StringBuilder {
        val ste = Throwable().stackTrace
        val realMethod = ste[REAL_METHOD_POS]
        return StringBuilder("[")
                .append(realMethod.methodName)
                .append("():")
                .append(realMethod.lineNumber)
                .append("] ")
    }

    fun v(tag: String, log: String) {
        android.util.Log.v(TAG_PREFIX + tag, prefix().append(log).toString())
    }

    fun dv(tag: String, log: String) {
        if (DEBUG) {
            android.util.Log.v(TAG_PREFIX + tag, prefix().append(log).toString())
        }
    }

    fun d(tag: String, log: String) {
        android.util.Log.d(TAG_PREFIX + tag, prefix().append(log).toString())
    }

    fun dd(tag: String, log: String) {
        if (DEBUG) {
            android.util.Log.d(TAG_PREFIX + tag, prefix().append(log).toString())
        }
    }

    fun i(tag: String, log: String) {
        android.util.Log.i(TAG_PREFIX + tag, prefix().append(log).toString())
    }

    fun w(tag: String, log: String) {
        android.util.Log.w(TAG_PREFIX + tag, prefix().append(log).toString())
    }

    fun e(tag: String, log: String) {
        android.util.Log.e(TAG_PREFIX + tag, prefix().append(log).toString())
    }
}
