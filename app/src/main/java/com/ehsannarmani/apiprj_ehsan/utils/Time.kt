package com.ehsannarmani.apiprj_ehsan.utils

import androidx.compose.material3.Text

fun Long.toReadableTime():String{

    var minutes = this/60
    var seconds = this%60


    val readableMinute = if (minutes < 10){
        "0$minutes"
    }else{
        minutes.toString()
    }
    val readableSecond = if (seconds < 10) {
        "0$seconds"
    }else{
        seconds.toString()
    }

    return "00:$readableMinute:$readableSecond"
}