package com.ehsannarmani.apiprj_ehsan

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ehsannarmani.apiprj_ehsan.navigation.Routes
import com.ehsannarmani.apiprj_ehsan.ui.screens.ActivationScreen
import com.ehsannarmani.apiprj_ehsan.ui.screens.AuthTypeScreen
import com.ehsannarmani.apiprj_ehsan.ui.screens.ChatScreen
import com.ehsannarmani.apiprj_ehsan.ui.screens.HomeScreen
import com.ehsannarmani.apiprj_ehsan.ui.screens.SignInScreen
import com.ehsannarmani.apiprj_ehsan.ui.screens.SignUpScreen
import com.ehsannarmani.apiprj_ehsan.ui.screens.SplashScreen
import com.ehsannarmani.apiprj_ehsan.ui.screens.StreamScreen
import com.ehsannarmani.apiprj_ehsan.ui.theme.ApiPrjEhsanTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApiPrjEhsanTheme(true) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = Routes.Splash.route){
                        composable(Routes.Splash.route){
                            SplashScreen(navController = navController)
                        }
                        composable(Routes.AuthType.route){
                            AuthTypeScreen(navController = navController)
                        }
                        composable(Routes.SignUp.route){
                            SignUpScreen(navController = navController)
                        }
                        composable(Routes.Activation.route){
                            ActivationScreen(navController = navController)
                        }
                        composable(Routes.SignIn.route){
                            SignInScreen(navController = navController)
                        }
                        composable(Routes.Home.route){
                            HomeScreen(navController = navController)
                        }
                        composable(Routes.Chat.route){
                            ChatScreen()
                        }
                        composable(
                            Routes.Stream.route,
                            enterTransition = {
                                slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(500))
                            },
                            popEnterTransition = {
                                slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(500))
                            },
                            exitTransition = {
                                slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(500))
                            }
                        ){
                            StreamScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (HomeViewModel.initialized){
            val vm = HomeViewModel.instanse
            if(vm.isConnected.value){
                vm.changeStatus(false)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (HomeViewModel.initialized){
            val vm = HomeViewModel.instanse
            if(vm.isConnected.value){
                vm.changeStatus(true)
            }
            if(!vm.socket.isConnected || vm.socket.isClosed){
                vm.connectToSocket(host = vm.latestHost, onConnect = {
                    vm.changeStatus(true)
                }, onError = {
                    Toast.makeText(this, "Reconnection Failed", Toast.LENGTH_SHORT).show()
                })
            }
        }
    }
}

