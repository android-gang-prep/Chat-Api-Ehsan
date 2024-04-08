package com.ehsannarmani.apiprj_ehsan.utils

import android.content.Context
import android.content.SharedPreferences

fun Context.shared():SharedPreferences{
    return getSharedPreferences("main",Context.MODE_PRIVATE)
}