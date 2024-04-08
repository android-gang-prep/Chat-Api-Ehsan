package com.ehsannarmani.apiprj_ehsan.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ehsannarmani.apiprj_ehsan.R
import com.ehsannarmani.apiprj_ehsan.navigation.Routes

@Composable
fun AuthTypeScreen(navController:NavHostController) {
    Image(
        painter = painterResource(id = R.drawable.gradient_bg),
        contentDescription = null,
        modifier=Modifier.fillMaxSize(),
        contentScale = ContentScale.FillBounds
    )
    Column(modifier= Modifier
        .fillMaxSize()
        .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(text = "Connect friends easily & quickly", fontSize = 70.sp, lineHeight = 80.sp)
        Spacer(modifier = Modifier.height(18.dp))
        Text(text = "Our chat app is the perfect way to stay connected with friends and family.", fontSize = 16.sp, color = Color.White.copy(.5f))
        Spacer(modifier = Modifier.height(32.dp))
        Row {
            AuthButton(image = R.drawable.facebook)
            Spacer(modifier = Modifier.width(8.dp))
            AuthButton(image = R.drawable.google)
            Spacer(modifier = Modifier.width(8.dp))
            AuthButton(image = R.drawable.apple)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier= Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Divider(modifier=Modifier.weight(1f),color = Color.White.copy(.5f))
            Text(text = "OR", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            Divider(modifier=Modifier.weight(1f), color = Color.White.copy(.5f))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { navController.navigate(Routes.SignUp.route) },modifier=Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(
            containerColor = Color.White.copy(.37f)
        ), shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Sign up withn phone", fontSize = 18.sp, color = Color.White)
        }
        Spacer(modifier = Modifier.height(22.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = "Existing account?", fontSize = 18.sp)
            TextButton(onClick = {
                navController.navigate(Routes.SignIn.route)
            }) {
                Text(text = "Log in", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun AuthButton(image:Int) {
    Box(modifier = Modifier
        .size(50.dp)
        .clip(CircleShape)
        .background(Color(0xffffffff).copy(alpha = .2f)), contentAlignment = Alignment.Center){
        Image(
            painter = painterResource(id = image),
            contentDescription = null,
            modifier=Modifier.size(20.dp)
        )
    }
}