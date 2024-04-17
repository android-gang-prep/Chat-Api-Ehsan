package com.ehsannarmani.apiprj_ehsan

import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.CountDownTimer
import android.util.Base64
import androidx.annotation.RequiresApi
import androidx.core.net.toFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ehsannarmani.apiprj_ehsan.models.Favourite
import com.ehsannarmani.apiprj_ehsan.models.Lives
import com.ehsannarmani.apiprj_ehsan.models.Post
import com.ehsannarmani.apiprj_ehsan.models.Story
import com.ehsannarmani.apiprj_ehsan.models.socket.Chat
import com.ehsannarmani.apiprj_ehsan.models.socket.OnlineUsers
import com.ehsannarmani.apiprj_ehsan.models.socket.Type
import com.google.common.io.ByteStreams
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.Socket
import java.util.Arrays
import java.util.concurrent.TimeUnit
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
class HomeViewModel : ViewModel() {


    companion object {
        lateinit var instanse: HomeViewModel

        val initialized: Boolean
            get() {
                return this::instanse.isInitialized
            }
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

    private val _isRecording = MutableStateFlow(false)
    val isRecording = _isRecording.asStateFlow()

    private val _voiceDuration = MutableStateFlow<Long?>(null)
    val voiceDuration = _voiceDuration.asStateFlow()

    var latestHost: String = ""

    private lateinit var recorder: MediaRecorder
    private lateinit var voiceFile: File

    private var voiceDurationJob: Job? = null

    private val hashMap: HashMap<String, ByteArray> = hashMapOf()

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

                                        "message" -> {
                                            val chat = gson.fromJson(
                                                incomingMessage,
                                                Chat::class.java
                                            )
                                            println("message received")
                                            _chats.update { it + chat }
                                        }

                                        "image" -> {
//                                            val event =
//                                                gson.fromJson(incomingMessage, Chat::class.java)
//                                            _chats.update {
//                                                it.toMutableList().apply {
//                                                    val getImage =
//                                                        firstOrNull { e -> e.id == event.id }
//                                                    if (getImage == null) {
//                                                        add(event)
//                                                    } else {
//                                                        val index = indexOf(getImage)
//                                                        removeAt(index)
//                                                        val bos = ByteArrayOutputStream()
//                                                        bos.write(
//                                                            Base64.decode(
//                                                                getImage.content,
//                                                                Base64.DEFAULT
//                                                            )
//                                                        )
//                                                        bos.write(
//                                                            Base64.decode(
//                                                                event.content,
//                                                                Base64.DEFAULT
//                                                            )
//                                                        )
//                                                        val newContent = Base64.encodeToString(
//                                                            bos.toByteArray(),
//                                                            Base64.DEFAULT
//                                                        )
//                                                        add(
//                                                            index,
//                                                            event.copy(
//                                                                content = newContent
//                                                            )
//                                                        )
//                                                    }
//                                                }
//                                            }
//                                            return@launch


                                            val chat =
                                                gson.fromJson(incomingMessage, Chat::class.java)
                                            println("receiving: step = ${chat.step}")
                                            val out = ByteArrayOutputStream()
                                            if (hashMap.containsKey(chat.id)) {
                                                out.write(hashMap[chat.id])
                                            }
                                            out.write(
                                                Base64.decode(
                                                    chat.content,
                                                    Base64.DEFAULT
                                                )
                                            )
                                            hashMap.put(chat.id,out.toByteArray())
                                            if (chat.step == "end") {
                                                _chats.update {
                                                    it + chat.copy(
                                                        content = Base64.encodeToString(
                                                            out.toByteArray(),
                                                            Base64.DEFAULT
                                                        )
                                                    )
                                                }
                                                hashMap.remove(chat.id)
                                            }
                                        }
                                    }
                                    if (!incomingMessage.isNullOrEmpty()) {
                                        _messages.update { it + incomingMessage }
                                    }
                                }
                            } catch (e: Exception) {
                            }
                        }
                    }
                }
            }
        }

        viewModelScope.launch {
            isRecording.collect {
                if (it) {
                    voiceDurationJob = launch {
                        while (isActive) {
                            delay(1000)
                            _voiceDuration.update { (it ?: 0) + 1 }
                        }
                    }
                }
                launch {
                    if (!it) {
                        _voiceDuration.update { null }
                        voiceDurationJob?.cancel()
                    }
                }
            }
        }
    }

    fun startRecord(context: Context) {
        voiceFile = File(context.cacheDir, "voice${System.currentTimeMillis()}.mp3")
        voiceFile.createNewFile()
        recorder = MediaRecorder()
            .apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setOutputFile(voiceFile.path)
                setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC)
                setAudioEncodingBitRate(96000)
                setAudioSamplingRate(44100)
                prepare()
            }
        _isRecording.update { true }
        recorder.start()
    }

    fun stopRecord() {
        recorder.stop()
        recorder.release()
        _isRecording.update { false }
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

    fun sendMessage(message: String, to: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val chat = Chat(
                from = username,
                to = to,
                content = message
            )
            runCatching {
                outputStream.writeUTF(gson.toJson(chat))
            }
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun sendImage(image: Uri, to: String, context: Context) {
        val imageId = System.currentTimeMillis().toString()
        var latestImageByteArray: ByteArray = byteArrayOf()
        var lastByteIndex = 0
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                context.contentResolver.openInputStream(image)?.let { inputStream ->
                    val bytes = ByteStreams.toByteArray(inputStream)
                    val partBy = 4096
                    var i = 0
                    while (lastByteIndex < bytes.lastIndex) {
                        delay(100)
                        val byteEndIndex = (lastByteIndex + partBy).coerceAtMost(bytes.size)
                        val partOfBytes = bytes.copyOfRange(lastByteIndex, byteEndIndex)
                        latestImageByteArray += partOfBytes
                        lastByteIndex = byteEndIndex

                        val lastStep = byteEndIndex >= bytes.lastIndex
                        val chat = Chat(
                            id = imageId,
                            from = username,
                            to = to,
                            content = Base64.encodeToString(partOfBytes, Base64.DEFAULT),
                            type = "image",
                            step = if (lastStep) "end" else i.toString()
                        )
                        outputStream.writeUTF(gson.toJson(chat))
                        println("reading: $byteEndIndex, end: ${bytes.lastIndex}, last: $lastStep")
                        if (lastStep) break
                        i++
                    }
                /*    _chats.update {
                        it + Chat(
                            from = username,
                            to = to,
                            type = "image",
                            content = Base64.encodeToString(latestImageByteArray, Base64.DEFAULT)
                        )
                    }*/
                }

            }
                .onFailure {
                    println("send error")
                    it.printStackTrace()
                    println(it.message.toString())
                }
        }
    }

    fun connectToSocket(
        host: String,
        port: Int = 6985,
        onConnect: () -> Unit,
        onError: () -> Unit,
    ) {
        if (host.isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                socket = Socket(host, port)
                inputStream = DataInputStream(socket.getInputStream())
                outputStream = DataOutputStream(socket.getOutputStream())
                _isConnected.update { true }
                withContext(Dispatchers.Main) {
                    latestHost = host
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
            runCatching {
                outputStream.writeUTF(string)
            }
        }
    }
}

data class Message(
    val name: String,
    val type: String,
    val content: String
)