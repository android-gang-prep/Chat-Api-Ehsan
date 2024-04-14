package com.ehsannarmani.apiprj_ehsan.models.socket

import com.google.gson.annotations.SerializedName

data class Chat(
    @SerializedName("name")
    val from:String,
    val to:String,
    val content:String,
    val type:String = "message"
)