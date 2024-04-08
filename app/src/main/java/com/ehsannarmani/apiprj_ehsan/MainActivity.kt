package com.ehsannarmani.apiprj_ehsan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ehsannarmani.apiprj_ehsan.navigation.Routes
import com.ehsannarmani.apiprj_ehsan.ui.screens.ActivationScreen
import com.ehsannarmani.apiprj_ehsan.ui.screens.AuthTypeScreen
import com.ehsannarmani.apiprj_ehsan.ui.screens.HomeScreen
import com.ehsannarmani.apiprj_ehsan.ui.screens.SignInScreen
import com.ehsannarmani.apiprj_ehsan.ui.screens.SignUpScreen
import com.ehsannarmani.apiprj_ehsan.ui.screens.SplashScreen
import com.ehsannarmani.apiprj_ehsan.ui.theme.ApiPrjEhsanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            ApiPrjEhsanTheme {
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
                    }
                }
            }
        }
    }
}

