package com.ehsannarmani.apiprj_ehsan.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavHostController) {


    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val name = remember {
        mutableStateOf("")
    }
    val email = remember {
        mutableStateOf("")
    }
    val password = remember {
        mutableStateOf("")
    }
    val phone = remember {
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
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Sign up with Phone",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xff3D4A7A),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Get chatting with friends and family today by signing up for our chat app!",
                    color = Color(0xff797C7B),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 22.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Your Name",
                    color = Color(0xff3D4A7A),
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.ExtraBold
                )
                TextField(
                    value = name.value,
                    onValueChange = { name.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(text = "Name")
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

                Text(
                    text = "Phone Number",
                    color = Color(0xff3D4A7A),
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.ExtraBold
                )
                TextField(
                    value = phone.value,
                    onValueChange = { phone.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(text = "+989146478614")
                    },
                    colors = TextFieldDefaults.colors(
                        disabledContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    textStyle = TextStyle(color = Color(0xff313131))
                )
            }
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
                        if (email.value.isNotEmpty() && name.value.isNotEmpty() && password.value.isNotEmpty() && phone.value.isNotEmpty()) {
                            if (phone.value.isValidPhone(context)) {
                                val body = JSONObject()
                                    .apply {
                                        put("email", email.value)
                                        put("nickName", name.value)
                                        put("password", password.value)
                                        put("phone", phone.value)
                                    }
                                postRequest(
                                    url = "http://wsk2019.mad.hakta.pro/api/users",
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
                                            if (it.contains("Success")) {
                                                AppData.phone = phone.value
                                                context
                                                    .shared()
                                                    .edit()
                                                    .putString("email", email.value)
                                                    .putString("nickName", name.value)
                                                    .putString("phone", phone.value)
                                                    .apply()
                                                navController.navigate(Routes.Activation.route)
                                            } else {
                                                println("UnHandled Response: $it")
                                            }
                                        }
                                    }
                                )
                            } else {
                                Toast
                                    .makeText(context, "Phone is not valid.", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Send Activation Code",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}