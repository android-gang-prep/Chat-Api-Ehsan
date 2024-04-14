package com.ehsannarmani.apiprj_ehsan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ehsannarmani.apiprj_ehsan.models.Favourite
import com.ehsannarmani.apiprj_ehsan.models.Lives
import com.ehsannarmani.apiprj_ehsan.models.Post
import com.ehsannarmani.apiprj_ehsan.models.Story
import com.ehsannarmani.apiprj_ehsan.models.socket.Chat
import com.ehsannarmani.apiprj_ehsan.models.socket.OnlineUsers
import com.ehsannarmani.apiprj_ehsan.models.socket.Type
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket
import java.util.concurrent.TimeUnit

class HomeViewModel:ViewModel() {


    companion object{
        lateinit var instanse:HomeViewModel
    }

    private var okHttpClient: OkHttpClient? = null

    private val _favorites = MutableStateFlow(emptyList<Post>())
    val favorites = _favorites.asStateFlow()

    private val _stories = MutableStateFlow(emptyList<Story>())
    val stories = _stories.asStateFlow()

    lateinit var inputStream: DataInputStream
    lateinit var outputStream: DataOutputStream

    lateinit var socket: Socket

    var gson: Gson = Gson()

    var username = "Unknown User"

    private val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected.asStateFlow()

    private val _messages = MutableStateFlow<List<String>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _onlineUsers = MutableStateFlow(emptyList<OnlineUsers.User>())
    val onlineUsers = _onlineUsers.asStateFlow()

    private val _chats = MutableStateFlow(emptyList<Chat>())
    val chats = _chats.asStateFlow()

    init {
        instanse = this
        okHttpClient = OkHttpClient()
            .newBuilder()
            .callTimeout(1, TimeUnit.MINUTES)
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .build()

        viewModelScope.launch {
            isConnected.collect { connected ->
                if (connected) {
                    viewModelScope.launch(Dispatchers.IO) {

                        while (_isConnected.value) {
                            try {
                                inputStream.readUTF().also { incomingMessage ->
                                    val type = gson.fromJson(incomingMessage, Type::class.java)
                                    _messages.update { it + incomingMessage }
                                    when (type.type) {
                                        "users" -> {
                                            val onlineUsers = gson.fromJson(
                                                incomingMessage,
                                                OnlineUsers::class.java
                                            )
                                            _onlineUsers.update {
                                                onlineUsers.content.filter {
                                                    it.name != username
                                                }
                                            }
                                        }
                                        "message"->{
                                            val chat = gson.fromJson(
                                                incomingMessage,
                                                Chat::class.java
                                            )
                                            println("message received")
                                            _chats.update { it+chat }
                                        }
                                    }
                                    if (!incomingMessage.isNullOrEmpty()) {
                                        _messages.update { it + incomingMessage }
                                    }
                                }
                            } catch (e: Exception) {
                                println(e.message)
                            }
                        }
                    }
                }
            }
        }
    }

    fun getFavorites(
        onError: (String) -> Unit,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val request = Request
                    .Builder()
                    .get()
                    .url("https://test-setare.s3.ir-tbz-sh1.arvanstorage.ir/wsi-lyon%2Ffavourites_avatars1.json")
                    .build()
                okHttpClient?.newCall(request)
                    ?.enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            viewModelScope.launch(Dispatchers.Main) {
                                onError(e.message.toString())
                            }
                        }

                        override fun onResponse(call: Call, response: Response) {
                            if (response.isSuccessful) {
                                viewModelScope.launch(Dispatchers.Main) {
                                    onSuccess()
                                }
                                Gson().fromJson(
                                    response.body?.string().toString(),
                                    Favourite::class.java
                                )
                                    .also { result ->
                                        _favorites.update { result.favourites }
                                    }
                            } else {
                                viewModelScope.launch(Dispatchers.Main) {
                                    onError(response.body?.string().toString())
                                }
                            }
                        }

                    })

            }
        }
    }

    fun getStories(
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val getStories = Request
                    .Builder()
                    .get()
                    .url("https://test-setare.s3.ir-tbz-sh1.arvanstorage.ir/profile_lives2.json")
                    .build()
                okHttpClient?.newCall(getStories)?.enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        viewModelScope.launch(Dispatchers.Main) {
                            onError(e.message.toString())
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            onSuccess()
                            _stories.update {
                                Gson().fromJson(
                                    response.body?.string().toString(),
                                    Lives::class.java
                                ).lives
                            }
                        } else {
                            onError(response.body?.string().toString())
                        }
                    }
                })
            }

        }
    }

    fun sendMessage(message:String,to:String){
        viewModelScope.launch(Dispatchers.IO) {
            val chat = Chat(
                from = username,
                to = to,
                content = message
            )
            outputStream.writeUTF(gson.toJson(chat))
        }
    }

    fun connectToSocket(
        host: String,
        port: Int = 6985,
        onConnect: () -> Unit,
        onError: () -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                socket = Socket(host, port)
                inputStream = DataInputStream(socket.getInputStream())
                outputStream = DataOutputStream(socket.getOutputStream())
                _isConnected.update { true }
                withContext(Dispatchers.Main) {
                    onConnect()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError()
                }
            }
        }
    }

    fun changeStatus(status: Boolean) {
        if (!this::socket.isInitialized) return
        val string = gson.toJson(
            Message(
                name = username,
                type = "online",
                content = if (status) "1" else "0"
            )
        )
        viewModelScope.launch(Dispatchers.IO) {
            outputStream.writeUTF(string)
        }
    }
}

data class Message(
    val name: String,
    val type: String,
    val content: String
)