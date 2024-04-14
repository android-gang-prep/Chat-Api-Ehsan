package com.ehsannarmani.apiprj_ehsan.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.ehsannarmani.apiprj_ehsan.AppData
import com.ehsannarmani.apiprj_ehsan.HomeViewModel
import com.ehsannarmani.apiprj_ehsan.models.socket.Chat
import com.ehsannarmani.apiprj_ehsan.models.socket.OnlineUsers
import com.ehsannarmani.apiprj_ehsan.models.socket.Type
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(viewModel: HomeViewModel = HomeViewModel.instanse) {
    val chats by viewModel.chats.collectAsState()
    val messages by viewModel.messages.collectAsState()

    Column(modifier=Modifier.fillMaxSize()) {

        LazyColumn(modifier= Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .weight(1f)
            .padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val filteredChats = chats.filter {
                (it.to == viewModel.username && it.from == AppData.sendMessageToUsername) || (it.from == viewModel.username && it.to == AppData.sendMessageToUsername)
            }

            items(filteredChats){ chat->
                Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = if (chat.from == viewModel.username) Arrangement.Start else Arrangement.End) {
                    Card {
                        Column(modifier=Modifier.padding(8.dp)) {
                            Text(text = chat.from, fontSize = 11.sp)
                            Text(text = chat.content)
                        }
                    }
                }
            }
        }
        Row(modifier= Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)) {
            val message = remember{
                mutableStateOf("")
            }
            OutlinedTextField(value = message.value, onValueChange = {message.value = it},modifier= Modifier
                .fillMaxWidth()
                .weight(1f))
            IconButton(onClick = {
                if (message.value.isNotEmpty()){
                    viewModel.sendMessage(message.value,AppData.sendMessageToUsername)
                    message.value = ""
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null
                )
            }
        }
    }
}