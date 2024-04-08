package com.ehsannarmani.apiprj_ehsan.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.ehsannarmani.apiprj_ehsan.R
import com.ehsannarmani.apiprj_ehsan.navigation.Routes
import com.ehsannarmani.apiprj_ehsan.utils.shared
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController:NavHostController) {
    val context = LocalContext.current
    LaunchedEffect(Unit){
        delay(2000)
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
    Image(
        painter = painterResource(id = R.drawable.splash_bg),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier=Modifier.fillMaxSize()
    )
}