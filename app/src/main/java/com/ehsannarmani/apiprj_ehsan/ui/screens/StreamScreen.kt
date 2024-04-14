package com.ehsannarmani.apiprj_ehsan.ui.screens

import android.media.session.PlaybackState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import com.ehsannarmani.apiprj_ehsan.AppData
import com.ehsannarmani.apiprj_ehsan.ui.theme.LocalCustomColors


@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun StreamScreen(url: String = AppData.streamUrl, navController: NavHostController) {
    val context = LocalContext.current
    val loading = remember {
        mutableStateOf(true)
    }
    val player = remember {
        ExoPlayer.Builder(context)
            .build().also {
                it.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        if (playbackState == PlaybackState.STATE_PLAYING) {
                            loading.value = false
                        }
                    }
                })
            }
    }
    DisposableEffect(Unit) {
        val dataSourceFactory = DefaultHttpDataSource.Factory()
        val hlsMediaSource =
            HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(url))
        player.also {
            it.setMediaSource(hlsMediaSource)
            it.prepare()
            it.play()
        }

        onDispose {
            player.release()
        }

    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalCustomColors.current.background)
    ) {

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { PlayerView(context).apply { this.player = player } }) {
            it.useController = false
            it.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            it.hideController()
        }
        if (loading.value) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 82.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .align(Alignment.BottomCenter)
                    .clip(CircleShape)
                    .background(Color(0xFFF44336))
                    .clickable {
                        navController.popBackStack()
                    }, contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null
                )
            }
        }
    }

}