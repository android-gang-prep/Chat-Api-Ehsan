package com.ehsannarmani.apiprj_ehsan.models

import com.google.gson.annotations.SerializedName


data class Lives(
    val lives:List<Story>,
)
data class Story(
    @SerializedName("profile_image")
    val profileImage:String,
    @SerializedName("live_stream_url")
    val liveStreamUrl:String
)
