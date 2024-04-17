package com.ehsannarmani.apiprj_ehsan.models.socket

import com.google.gson.annotations.SerializedName

data class Chat(
    val id:String = System.currentTimeMillis().toString(),
    @SerializedName("name")
    val from:String,
    val to:String,
    val content:String,
    val type:String = "message",
    val step:String? = null,
)