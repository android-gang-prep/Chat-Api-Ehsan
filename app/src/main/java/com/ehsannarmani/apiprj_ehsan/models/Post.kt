package com.ehsannarmani.apiprj_ehsan.models


data class Favourite(
    val favourites:List<Post>
)
data class Post(
    val name:String,
    val id:Int,
    val image:String
)
