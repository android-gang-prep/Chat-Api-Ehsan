package com.ehsannarmani.apiprj_ehsan.models.socket

data class OnlineUsers(
    val type:String,
    val content:List<User>
){
    data class User(val name:String,val status:String)
}