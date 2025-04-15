package com.example.mynotes.ui.Auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mynotes.AppViewModelProvider

@Composable
fun ForgotPasswordScreen(
    navigateBack: () -> Unit,
    viewModel: AuthViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var email by rememberSaveable { mutableStateOf("") }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 18.dp, end = 18.dp)
            .background(color = MaterialTheme.colorScheme.background)
    ) {


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(WindowInsets.systemBars.asPaddingValues())
                .background(color = MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.Start,
        ) {
            IconButton(onClick = { navigateBack() }, Modifier.align(Alignment.Start)) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
            }
            Box(
                Modifier
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.background)
                ,
                ){
                Column(verticalArrangement = Arrangement.spacedBy(32.dp), modifier = Modifier.padding(top = 48.dp)) {
                    Row( horizontalArrangement = Arrangement.Start, ) {
                        Text(
                            "Quên mật khẩu",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "?", color = MaterialTheme.colorScheme.primary, fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        "Nhập địa chỉ email gắn với tài khoản của bạn.", fontSize = 26.sp,
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontWeight = FontWeight.Bold
                    )
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        isError = email.isNotEmpty()&& !email.contains("@"),
                        supportingText = {if(email.isNotEmpty()&&!email.contains("@")) Text("Vui lòng nhập đúng định dạng Email") },
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
                            focusedContainerColor = MaterialTheme.colorScheme.background
                        )
                    )
                    Button(
                        onClick = { viewModel.forgetPassword(email) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.secondaryContainer,
                        ),
                        elevation = ButtonDefaults.buttonElevation()
                    ) {
                        Text("XÁC NHẬN")
                    }
                }

            }

        }
    }

}