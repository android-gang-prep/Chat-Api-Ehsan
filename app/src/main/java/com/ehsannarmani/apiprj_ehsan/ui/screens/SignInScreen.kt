package com.ehsannarmani.apiprj_ehsan.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ehsannarmani.apiprj_ehsan.AppData
import com.ehsannarmani.apiprj_ehsan.R
import com.ehsannarmani.apiprj_ehsan.models.toError
import com.ehsannarmani.apiprj_ehsan.navigation.Routes
import com.ehsannarmani.apiprj_ehsan.utils.isValidPhone
import com.ehsannarmani.apiprj_ehsan.utils.postRequest
import com.ehsannarmani.apiprj_ehsan.utils.shared
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(navController:NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val email = remember {
        mutableStateOf("")
    }
    val password = remember {
        mutableStateOf("")
    }

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(title = { /*TODO*/ }, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = Color.Black
                )
            }
        }, colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = Color.White
        )
        )
    }, containerColor = Color.White) {
        it
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Log in to Chatbox",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xff3D4A7A),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Welcome back! Sign in using your social account or email to continue us",
                    color = Color(0xff797C7B),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 22.dp)
                )
                Spacer(modifier = Modifier.height(28.dp))
                Row {
                    AuthButton(image = R.drawable.facebook)
                    Spacer(modifier = Modifier.width(8.dp))
                    AuthButton(image = R.drawable.google)
                    Spacer(modifier = Modifier.width(8.dp))
                    AuthButton(image = R.drawable.apple_dark)
                }
                Spacer(modifier = Modifier.height(32.dp))
                Row(modifier= Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Divider(modifier=Modifier.weight(1f),color = Color.White.copy(.5f))
                    Text(text = "OR", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    Divider(modifier=Modifier.weight(1f), color = Color.White.copy(.5f))
                }
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "Your Email",
                    color = Color(0xff3D4A7A),
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.ExtraBold
                )
                TextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(text = "Email")
                    },
                    colors = TextFieldDefaults.colors(
                        disabledContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    textStyle = TextStyle(color = Color(0xff313131))
                )
                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Password",
                    color = Color(0xff3D4A7A),
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.ExtraBold
                )
                TextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(text = "Password")
                    },
                    colors = TextFieldDefaults.colors(
                        disabledContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                    textStyle = TextStyle(color = Color(0xff313131))
                )
                Spacer(modifier = Modifier.height(18.dp))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color.Black,
                                    Color(0xff3D4A7A),
                                    Color(0xff3D4A7A),
                                )
                            )
                        )
                        .clickable {
                            if (email.value.isNotEmpty()  && password.value.isNotEmpty()) {
                                val body = JSONObject()
                                    .apply {
                                        put("email", email.value)
                                        put("password", password.value)
                                    }
                                postRequest(
                                    url = "http://wsk2019.mad.hakta.pro/api/user/login",
                                    body = body
                                        .toString()
                                        .toRequestBody(),
                                    onFail = {
                                        scope.launch(Dispatchers.Main) {
                                            Toast
                                                .makeText(
                                                    context,
                                                    it.toError().error,
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                        }
                                    },
                                    onSuccess = {
                                        scope.launch(Dispatchers.Main) {
                                            context
                                                .shared()
                                                .edit()
                                                .putString("email", email.value)
                                                .putBoolean("loggedIn", true)
                                                .apply()
                                            navController.navigate(Routes.Home.route){
                                                popUpTo(0){
                                                    inclusive = true
                                                }
                                            }
                                        }
                                    }
                                )
                            }

                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Login",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                TextButton(onClick = { }) {
                    Text(text = "Forgot password?",color = Color(0xff3D4A7A), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        }
    }
}