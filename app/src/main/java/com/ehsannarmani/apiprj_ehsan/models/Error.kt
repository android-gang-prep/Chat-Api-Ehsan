package com.ehsannarmani.apiprj_ehsan.models

import com.google.gson.Gson

data class Error(val error:String)

fun String.toError():Error{
    return Gson().fromJson(this,Error::class.java)
}