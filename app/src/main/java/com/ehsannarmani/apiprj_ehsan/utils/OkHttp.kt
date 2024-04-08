package com.ehsannarmani.apiprj_ehsan.utils

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

fun postRequest(url:String,body: RequestBody,onFail:(String)->Unit,onSuccess:(String)->Unit){
    val client = OkHttpClient()
        .newBuilder()
        .callTimeout(1,TimeUnit.MINUTES)
        .connectTimeout(1,TimeUnit.MINUTES)
        .readTimeout(1,TimeUnit.MINUTES)
        .writeTimeout(1,TimeUnit.MINUTES)
        .build()

    val request = Request.Builder()
        .url(url)
        .post(body)
        .build()

    client.newCall(request)
        .enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
                onFail(e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful){
                    onSuccess(response.body?.string().toString())
                }else{
                    onFail(response.body?.string().toString())
                }
            }

        })
}
fun putRequest(url:String,body: RequestBody,onFail:(String)->Unit,onSuccess:(String)->Unit){
    val client = OkHttpClient()
        .newBuilder()
        .callTimeout(1,TimeUnit.MINUTES)
        .connectTimeout(1,TimeUnit.MINUTES)
        .readTimeout(1,TimeUnit.MINUTES)
        .writeTimeout(1,TimeUnit.MINUTES)
        .build()

    val request = Request.Builder()
        .url(url)
        .put(body)
        .build()

    client.newCall(request)
        .enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
                onFail(e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful){
                    onSuccess(response.body?.string().toString())
                }else{
                    onFail(response.body?.string().toString())
                }
            }

        })
}