package pw.androidthanatos.library

import android.os.Handler
import android.util.Log

/**
 * 工具类
 */

var DEBUG = true

fun  log(msg: String, tag: String = "OpenBitmap"){
    if (DEBUG)Log.d(tag, msg)
}

var openBitmapHandler: Handler? = null