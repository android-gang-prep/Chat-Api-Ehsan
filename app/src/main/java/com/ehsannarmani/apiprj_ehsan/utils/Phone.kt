package com.ehsannarmani.apiprj_ehsan.utils

import android.content.Context
import com.ehsannarmani.apiprj_ehsan.AppData
import com.ehsannarmani.apiprj_ehsan.R

fun String.isValidPhone(context: Context):Boolean{
    val codes = context.resources.getStringArray(R.array.CountryCodes)
    var phone = ""
    var countryCode = ""
    codes.forEach {
        val (code,country) = it.split(",")
        if (code in this){
            countryCode = code
            phone = this.replace(code,"")
        }
    }
    return phone.isNotEmpty() && countryCode.isNotEmpty()
}