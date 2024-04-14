package com.ehsannarmani.apiprj_ehsan.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ehsannarmani.apiprj_ehsan.R
import com.ehsannarmani.apiprj_ehsan.navigation.Routes
import com.ehsannarmani.apiprj_ehsan.utils.shared
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController:NavHostController) {
    val context = LocalContext.current
    val scale = remember {
        Animatable(3f)
    }
    LaunchedEffect(Unit){
        scale.animateTo(1f, tween(1300))
        delay(300)
        context
            .shared()
            .getBoolean("loggedIn",false)
            .also {
                val destination = if (it){
                    Routes.Home.route
                }else{
                    Routes.AuthType.route
                }
                navController.navigate(destination){
                    popUpTo(0){
                        inclusive = true
                    }
                }
            }
    }
    Box(modifier = Modifier.fillMaxSize()){
        Image(
            painter = painterResource(id = R.drawable.gradient_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier=Modifier.fillMaxSize()
        )
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(top = 22.dp)){
            Icon(
                painter = painterResource(id = R.drawable.ic_chat),
                contentDescription = null,
                modifier= Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .scale(scale.value),
            )
        }
        Image(
            painter = painterResource(id = R.drawable.text_it), contentDescription = null,modifier= Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        )
    }
}