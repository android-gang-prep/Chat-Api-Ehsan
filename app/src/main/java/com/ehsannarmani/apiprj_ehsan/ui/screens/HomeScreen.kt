package com.ehsannarmani.apiprj_ehsan.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.ehsannarmani.apiprj_ehsan.AppData
import com.ehsannarmani.apiprj_ehsan.HomeViewModel
import com.ehsannarmani.apiprj_ehsan.R
import com.ehsannarmani.apiprj_ehsan.models.Favourite
import com.ehsannarmani.apiprj_ehsan.models.Lives
import com.ehsannarmani.apiprj_ehsan.models.Post
import com.ehsannarmani.apiprj_ehsan.models.Story
import com.ehsannarmani.apiprj_ehsan.navigation.Routes
import com.ehsannarmani.apiprj_ehsan.ui.theme.LocalCustomColors
import com.ehsannarmani.apiprj_ehsan.utils.shared
import com.ehsannarmani.apiprj_ehsan.viewModels.LocalThemeViewModel
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket
import java.util.concurrent.TimeUnit
import kotlin.io.encoding.Base64

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel:HomeViewModel = viewModel()
) {

    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    val themeViewModel = LocalThemeViewModel.current

    val darkMode by themeViewModel.darkMode.collectAsState()

    val favourites by viewModel.favorites.collectAsState()
    val messages by viewModel.messages.collectAsState()
    
    val onlineUsers by viewModel.onlineUsers.collectAsState()

    val connectedToSocket by viewModel.isConnected.collectAsState()

    val loading = remember {
        mutableStateOf(true)
    }
    val storyLoadings = remember {
        mutableStateOf(true)
    }

    val stories = remember {
        mutableStateListOf<Story>()
    }

    val hostDialogOpen = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        if (!connectedToSocket){
            hostDialogOpen.value = true
        }
    }

    val userLabel = remember {
        val shared = context.shared()
        shared.getString("name", null) ?: shared.getString("email", "Unknown")
    }

    LaunchedEffect(Unit) {
        viewModel.username = (userLabel.orEmpty())
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        sensorManager.registerListener(object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.values?.firstOrNull() != null) {
                    val light = event.values.first()
                    if (light >= 500) {
                        if (darkMode) {
                            themeViewModel.setDarkMode(false)
                        }
                    } else {
                        if (!darkMode) {
                            themeViewModel.setDarkMode(true)
                        }
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

            }

        }, sensor, SensorManager.SENSOR_DELAY_NORMAL)


        viewModel.getFavorites(
            onError = {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            },
            onSuccess = {
                loading.value = false
            }
        )
        viewModel.getStories(
            onError = {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            },
            onSuccess = {
                storyLoadings.value = false
            }
        )
    }


    if (hostDialogOpen.value) {
        val host = remember {
            mutableStateOf("")
        }
        Dialog(onDismissRequest = { hostDialogOpen.value = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(12.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = host.value,
                    onValueChange = {
                        host.value = it
                    }, placeholder = {
                        Text(text = "Host IP")
                    })
                Spacer(modifier = Modifier.height(6.dp))
                Button(modifier=Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),onClick = {
                    if (host.value.isNotEmpty()) {
                        viewModel.connectToSocket(
                            host = host.value,
                            onConnect = {
                                viewModel.changeStatus(true)
                                Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show()
                            },
                            onError = {
                                Toast.makeText(context, "You couldn't connect ${host.value}", Toast.LENGTH_SHORT).show()
                            }
                        )
                        hostDialogOpen.value = false

                    }
                }) {
                    Text(text = "Connect")
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalCustomColors.current.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.FillBounds
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = userLabel.orEmpty(), color = LocalCustomColors.current.textColor)
            }
            Spacer(modifier = Modifier.height(22.dp))
            //******** Story Section ******** //
            if (storyLoadings.value) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val storySize = 60.dp
                stories.forEachIndexed { index, story ->
                    Column {
                        if (index % 2 == 0) {
                            Spacer(modifier = Modifier.height((storySize.value / (2.5)).dp))
                        }
                        val image = remember {
                            mutableStateOf<ImageBitmap?>(null)
                        }
                        loadBitmap(story.profileImage, onLoad = {
                            image.value = it.asImageBitmap()
                        })

                        AnimatedContent(image.value != null, label = "") { shouldShow ->
                            if (shouldShow) {
                                Box(modifier = Modifier
                                    .size(storySize)
                                    .clip(CircleShape)
                                    .border(
                                        3.dp,
                                        if (story.liveStreamUrl.isEmpty()) Color.Gray else Color(
                                            0xFF9C27B0
                                        ),
                                        CircleShape
                                    )
                                    .clickable {
                                        if (story.liveStreamUrl.isNotEmpty()) {
                                            AppData.streamUrl = story.liveStreamUrl
                                            navController.navigate(Routes.Stream.route)
                                        }
                                    }) {
                                    image.value?.let {
                                        Image(
                                            bitmap = it,
                                            contentDescription = null,
                                            contentScale = ContentScale.FillBounds,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier.size(storySize),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(25.dp))
                                }
                            }
                        }

                    }
                }
            }
            //******** Story Section ******** //
            Spacer(modifier = Modifier.height(22.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .weight(1f)
                        .clip(
                            RoundedCornerShape(12.dp)
                        )
                        .background(LocalCustomColors.current.darkBackground)
                ) {
                    TextField(value = "", onValueChange = {}, modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), colors = TextFieldDefaults.colors(
                        disabledContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ), placeholder = {
                        Text(text = "Search...", fontSize = 13.sp)
                    })
                    Box(
                        modifier = Modifier
                            .size(55.dp)
                            .clip(
                                RoundedCornerShape(8.dp)
                            )
                            .background(LocalCustomColors.current.lightBackground)
                            .clickable {

                            }, contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(55.dp)
                        .clip(
                            RoundedCornerShape(8.dp)
                        )
                        .background(Color(0xff03A9F1))
                        .clickable {

                        }, contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Favourites:",
                color = LocalCustomColors.current.textColor,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            if (loading.value) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val pagerState = rememberPagerState {
                    favourites.count()
                }
                HorizontalPager(
                    state = pagerState,
                    pageSize = PageSize.Fixed(115.dp),
                    pageSpacing = 12.dp
                ) {
                    val item = favourites[it]
                    Box(
                        modifier = Modifier
                            .width(115.dp)
                            .height(180.dp)
                            .clip(RoundedCornerShape(40.dp))

                    ) {
                        val image = remember {
                            mutableStateOf<ImageBitmap?>(null)
                        }
                        LaunchedEffect(Unit) {
                            loadBitmap(item.image, onLoad = {
                                image.value = it.asImageBitmap()
                            })
                        }
                        image.value?.let {
                            Image(
                                bitmap = it,
                                contentScale = ContentScale.Crop,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(horizontal = 14.dp, vertical = 22.dp)
                        ) {
                            Text(text = item.name, color = Color.White, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.heart),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
                       Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(favourites.count()) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(
                                    if (it == pagerState.currentPage) LocalCustomColors.current.active else LocalCustomColors.current.notActive
                                )
                        ) {

                        }
                        Spacer(modifier = Modifier.width(3.dp))
                    }
                }

            }
            Spacer(modifier=Modifier.height(8.dp))
            LazyColumn (verticalArrangement = Arrangement.spacedBy(8.dp)){
                items(onlineUsers){ user->
                    val isOnline = user.status == "1"
                    val color = animateColorAsState(targetValue = if (!isOnline) Color(0xFFF44336) else Color(0xFF43A047), animationSpec = tween(500))
                    Card(modifier= Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            interactionSource = remember{
                                MutableInteractionSource()
                            },
                            indication = null,
                            onClick = {},
                            onLongClick = {
                                viewModel.sendMessage("kirkhar", "mehran")
                            }
                        ), colors = CardDefaults.cardColors(
                        containerColor = color.value
                    ), onClick = {
                        AppData.sendMessageToUsername = user.name
                        navController.navigate(Routes.Chat.route)
                    }) {
                        Column(modifier=Modifier.padding(12.dp)) {
                            Text(text = user.name, fontWeight = FontWeight.Bold,color = Color.White)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = if (user.status == "1") "Online" else "Offline", fontSize = 12.sp ,color = Color.White)
                        }
                    }
                }
            }

        }
    }
}




fun loadBitmap(
    url: String,
    onLoad: (Bitmap) -> Unit,
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    scope.launch {
        try {
            val client = OkHttpClient().newBuilder()
                .callTimeout(1, TimeUnit.MINUTES)
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES).build()
            val request = Request.Builder().get().url(url).build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {

                }

                override fun onResponse(call: Call, response: Response) {
                    val bytes = response.body!!.bytes()
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    onLoad(bitmap)
                }

            })

        } catch (e: Exception) {
        }
    }
}

