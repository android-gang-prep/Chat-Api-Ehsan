package com.ehsannarmani.apiprj_ehsan.ui.screens

import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.ehsannarmani.apiprj_ehsan.AppData
import com.ehsannarmani.apiprj_ehsan.R
import com.ehsannarmani.apiprj_ehsan.models.toError
import com.ehsannarmani.apiprj_ehsan.navigation.Routes
import com.ehsannarmani.apiprj_ehsan.utils.postRequest
import com.ehsannarmani.apiprj_ehsan.utils.putRequest
import com.ehsannarmani.apiprj_ehsan.utils.shared
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivationScreen(navController: NavHostController) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val activationCode = remember {
        mutableStateOf("")
    }

    val loading = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit){
        val codes = context.resources.getStringArray(R.array.CountryCodes)
        val savedPhone = AppData.phone
        var phone = ""
        var countryCode = ""
        codes.forEach {
            val (code,country) = it.split(",")
            if (code in savedPhone){
                countryCode = code
                phone = savedPhone.replace(code,"")
            }
        }

        println("phone: $phone, code: $countryCode")


        val body = JSONObject()
            .apply {
                put("countryCode", countryCode)
                put("phone", phone)
            }
        postRequest(
            url = "http://wsk2019.mad.hakta.pro/api/user/smsCode",
            body = body
                .toString()
                .toRequestBody(),
            onFail = {
                loading.value = false
                scope.launch(Dispatchers.Main) {
                    Toast
                        .makeText(context, it.toError().error, Toast.LENGTH_SHORT)
                        .show()
                }
            },
            onSuccess = {
                loading.value = false
            }
        )
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
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
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
            }
            Column {
                Text(
                    text = "Activation Code",
                    color = Color(0xff3D4A7A),
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.ExtraBold
                )
                TextField(
                    value = activationCode.value,
                    onValueChange = { activationCode.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        disabledContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    textStyle = TextStyle(color = Color(0xff313131)),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
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
                        val body = JSONObject()
                            .apply {
                                put("code",activationCode.value)
                            }
                        putRequest(
                            url = "http://wsk2019.mad.hakta.pro/api/user/activation",
                            body = body
                                .toString()
                                .toRequestBody(),
                            onFail = {
                                scope.launch(Dispatchers.Main) {
                                    Toast
                                        .makeText(context, it.toError().error, Toast.LENGTH_SHORT)
                                        .show()
                                }
                            },
                            onSuccess = {
                                scope.launch(Dispatchers.Main) {
                                    context.shared()
                                        .edit()
                                        .putBoolean("loggedIn",true)
                                        .apply()
                                    navController.navigate(Routes.Home.route){
                                        popUpTo(0){
                                            inclusive = true
                                        }
                                    }
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Activate Account",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}