package com.ehsannarmani.apiprj_ehsan.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.ehsannarmani.apiprj_ehsan.AppData
import com.ehsannarmani.apiprj_ehsan.HomeViewModel
import com.ehsannarmani.apiprj_ehsan.R
import com.ehsannarmani.apiprj_ehsan.models.socket.Chat
import com.ehsannarmani.apiprj_ehsan.models.socket.OnlineUsers
import com.ehsannarmani.apiprj_ehsan.models.socket.Type
import com.ehsannarmani.apiprj_ehsan.utils.toReadableTime
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalFoundationApi::class, ExperimentalEncodingApi::class)
@Composable
fun ChatScreen(viewModel: HomeViewModel = HomeViewModel.instanse) {

    val context = LocalContext.current

    val chats by viewModel.chats.collectAsState()

    val isRecording by viewModel.isRecording.collectAsState()
    val voiceDuration by viewModel.voiceDuration.collectAsState()

    val mediaPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            it?.let { uri->
                viewModel.sendImage(image = uri,to = AppData.sendMessageToUsername,context)
            }
        }
    )
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {

        }
    )
    LaunchedEffect(Unit){
        permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
    }
    Column(modifier=Modifier.fillMaxSize()) {

        LazyColumn(modifier= Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .weight(1f)
            .padding(8.dp), verticalArrangement = Arrangement.Bottom) {
            val filteredChats = chats.filter {
                (it.to == viewModel.username && it.from == AppData.sendMessageToUsername) || (it.from == viewModel.username && it.to == AppData.sendMessageToUsername)
            }

            items(filteredChats){ chat->
                Column {
                    Row(modifier= Modifier
                        .fillMaxWidth()
                        .animateItemPlacement(), horizontalArrangement = if (chat.from == viewModel.username) Arrangement.Start else Arrangement.End) {
                        Card {
                            Column(modifier=Modifier.padding(8.dp)) {
                                Text(text = chat.from, fontSize = 11.sp)

                                when(chat.type){
                                    "image"->{
                                        val image = remember {
                                            mutableStateOf<Bitmap?>(null)
                                        }
                                        LaunchedEffect(chat.content) {
                                            val byteArray = Base64.decode(chat.content,Base64.DEFAULT)
                                            Log.i("TAG", "ChatScreen: "+chat.content)
                                            image.value =  BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                                        }
                                        image.value?.let{
                                            Image(
                                                bitmap = it.asImageBitmap(),
                                                contentDescription = null,
                                                modifier= Modifier
                                                    .sizeIn(maxWidth = 300.dp, maxHeight = 300.dp)
                                                    .clip(
                                                        RoundedCornerShape(8.dp)
                                                    ),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                    }
                                    else->{
                                        Text(text = chat.content)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
        val messageInputHeight = 50.dp
        Row(modifier= Modifier
            .fillMaxWidth()
            .height(messageInputHeight)
            .padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {

            val message = remember{
                mutableStateOf("")
            }
            Row(modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xff313131))
                .padding(start = 4.dp)){
                AnimatedContent(targetState = isRecording,modifier= Modifier
                    .fillMaxWidth()
                    .weight(1f)) {
                    if (it){
                        Row(verticalAlignment = Alignment.CenterVertically,modifier= Modifier
                            .fillMaxSize()
                            .weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Color(0xFFF44336)
                                    )
                            )
                            Spacer(modifier = Modifier.width(8.dp))

                            Text(text = (voiceDuration ?: 0).toReadableTime())
                        }
                    }else{
                        TextField(value = message.value, onValueChange = {message.value = it}, placeholder = {
                            Text(text = "Write")
                        },modifier= Modifier
                            .fillMaxWidth()
                            .weight(1f),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                errorContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                errorIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                    }
                }
                AnimatedVisibility(visible = isRecording) {
                    TextButton(onClick = {
                        viewModel.stopRecord()
                    }) {
                        Text(text = "CANCEL",color = Color(0xFFF44336))
                    }
                }
                Box(modifier= Modifier
                    .fillMaxHeight()
                    .width(messageInputHeight)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isRecording) Color(0xFFEC407A) else Color(0xff414141))
                    .clickable {
                        if (viewModel.isRecording.value) {
                            viewModel.stopRecord()
                        } else {
                            viewModel.startRecord(context)
                        }
                    }
                    ,
                    contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_mic),
                        tint = Color.White,
                        contentDescription = null
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Box(modifier= Modifier
                    .fillMaxHeight()
                    .width(messageInputHeight)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xff414141))
                    .clickable {
                        if (message.value.isNotEmpty()) {
                            viewModel.sendMessage(message.value, AppData.sendMessageToUsername)
                            message.value = ""
                        }
                    }
                    ,
                    contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_chat2),
                        tint = Color.White,
                        contentDescription = null
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(modifier= Modifier
                .fillMaxHeight()
                .width(messageInputHeight)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xff00ac83))
                .clickable {
                    mediaPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
                ,
                contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_camera),
                    tint = Color.White,
                    contentDescription = null
                )
            }
        }
        Spacer(modifier=Modifier.height(12.dp))
    }
}