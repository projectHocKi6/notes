package com.example.mynotes.ui.Auth

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mynotes.AppViewModelProvider
import com.example.mynotes.R
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.internal.FacebookSignatureValidator
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateBack: () -> Unit,
    navigateToForgotPassword: () -> Unit,
    callbackManager: CallbackManager
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult>{
                override fun onSuccess(result: LoginResult) {
                    Log.d("FacebookLogin","Login success, token: ${result.accessToken}")
                    viewModel.handleFacebookAccessToken(result.accessToken)
                }

                override fun onCancel() {
                    Log.d("FacebookLogin","Facebook login cancelled")
                }

                override fun onError(error: FacebookException) {
                    Log.e("FacebookLogin", "Facebook login error: ${error.message}")
                }
            })
    }
    Login(
        viewModel, context = context, modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
                keyboardController?.hide()
            })
        },
        navigateBack = navigateBack,
        navigateToForgotPassword = navigateToForgotPassword
    )
    val errorMessage by viewModel.errorMessage.collectAsState()
    errorMessage?.let { error ->
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
    }

    val user by viewModel.SignInState.collectAsState()
    LaunchedEffect(user) {
        if (user != null) {
            Toast.makeText(context, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
            navigateBack()
        }
    }
}

@Composable
fun Login(
    viewModel: AuthViewModel,
    context: Context,
    navigateBack: () -> Unit,
    navigateToForgotPassword: () -> Unit,
    modifier: Modifier = Modifier
) {

    val activity = LocalContext.current
    var isLoginScreen by rememberSaveable { mutableStateOf(true) }
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            viewModel.handleSignInResult(task)
        }
    )

    var passVisible by rememberSaveable { mutableStateOf(false) }
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = if (isSystemInDarkTheme()) painterResource(R.drawable.login_bg_night) else painterResource(
                R.drawable.login_bg_day
            ),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        IconButton(
            onClick = navigateBack,
            modifier = Modifier.padding(WindowInsets.systemBars.asPaddingValues())
        ) {
            Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "")
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Box(
                modifier = Modifier
                    .padding(24.dp)
                    .background(
                        color = if (isSystemInDarkTheme()) colorResource(R.color.loginNight)
                        else colorResource(R.color.loginDay),
                        shape = RoundedCornerShape(10)
                    )
                    .clip(RoundedCornerShape(10))
                    .wrapContentHeight()
                    .fillMaxWidth()
            ) {
                var email by rememberSaveable { mutableStateOf("") }
                var password by rememberSaveable { mutableStateOf("") }

                Column(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            Box(contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "Login",
                                        color = if (isLoginScreen) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.onPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp,
                                        modifier = Modifier.clickable { isLoginScreen = true }
                                    )
                                    if (isLoginScreen) {
                                        Divider(
                                            color = MaterialTheme.colorScheme.tertiaryContainer,
                                            modifier = Modifier
                                                .width(50.dp)
                                                .height(2.dp)
                                        )
                                    }
                                }
                            }
                            Box(contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "Register",
                                        color = if (isLoginScreen) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.tertiaryContainer,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.clickable { isLoginScreen = false }
                                    )
                                    if (!isLoginScreen) {
                                        Divider(
                                            color = MaterialTheme.colorScheme.tertiaryContainer,
                                            modifier = Modifier
                                                .width(60.dp)
                                                .height(2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (isLoginScreen) {
                            OutlinedTextField(
                                value = email,
                                onValueChange = {
                                    email = it
                                },
                                modifier = Modifier.width(280.dp),

                                label = { Text("Email") },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.MailOutline,
                                        contentDescription = ""
                                    )
                                },
                                supportingText = {
                                    if (!(email.isNotEmpty() && !email.contains("@"))) null else Text(
                                        "Vui lòng nhập email hợp lệ"
                                    )
                                },
                                isError = (email.isNotEmpty() && !email.contains("@")),
                                singleLine = true,
                                maxLines = 1,
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary
                                )

                            )
                            OutlinedTextField(
                                value = password,
                                onValueChange = {
                                    password = it
                                },
                                modifier = Modifier.width(280.dp),
                                label = { Text("Mật khẩu") },
                                trailingIcon = {
                                    IconButton(onClick = { passVisible = !passVisible }) {
                                        Icon(
                                            painter = if (passVisible) painterResource(R.drawable.visible_off) else painterResource(
                                                R.drawable.visibility
                                            ), contentDescription = "",
                                            Modifier.size(20.dp)
                                        )
                                    }
                                },
                                supportingText = {
                                    if (password.isNotEmpty() && password.length < 6) Text(
                                        "Mật khẩu phải nhiều hơn 6 ký tự"
                                    )
                                },
                                isError = password.isNotEmpty() && password.length < 6,
                                singleLine = true,
                                maxLines = 1,
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                                ),
                                visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation()
                            )
                            Text(
                                "Quên mật khẩu?",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .clickable { navigateToForgotPassword() }
                                    .align(Alignment.Start),

                                )

                            Button(
                                onClick = {
                                    viewModel.signIn(email, password)
                                },
                                enabled = if (email.isEmpty() || password.isEmpty() || password.length < 6) false else true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Text("Đăng nhập", color = MaterialTheme.colorScheme.onPrimary)

                            }
                        } else {
                            var reEnterPass by rememberSaveable { mutableStateOf("") }
                            OutlinedTextField(
                                value = email,
                                onValueChange = {
                                    email = it
                                },
                                modifier = Modifier.width(280.dp),

                                label = { Text("Email") },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.MailOutline,
                                        contentDescription = ""
                                    )
                                },
                                supportingText = {
                                    if (email.isNotEmpty() && !email.contains("@")) Text(
                                        "Vui lòng nhập email hợp lệ"
                                    )
                                },
                                isError = (email.isNotEmpty() && !email.contains("@")),
                                singleLine = true,
                                maxLines = 1,
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = RoundedCornerShape(12)
                            )
                            OutlinedTextField(
                                value = password,
                                onValueChange = {
                                    password = it
                                },
                                modifier = Modifier.width(280.dp),

                                label = { Text("Mật khẩu") },
                                trailingIcon = {
                                    IconButton(onClick = { passVisible = !passVisible }) {
                                        Icon(
                                            painter = if (passVisible) painterResource(R.drawable.visible_off) else painterResource(
                                                R.drawable.visibility
                                            ), contentDescription = "",
                                            Modifier.size(20.dp)
                                        )
                                    }
                                },

                                supportingText = {
                                    if (password.isNotEmpty() && password.length < 6) Text(
                                        "Mật khẩu phải nhiều hơn 6 ký tự"
                                    )
                                },
                                isError = password.isNotEmpty() && password.length < 6,
                                singleLine = true,
                                maxLines = 1,
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                                ),
                                shape = RoundedCornerShape(12),
                                visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation()
                            )
                            OutlinedTextField(
                                value = reEnterPass,
                                onValueChange = {
                                    reEnterPass = it
                                },
                                modifier = Modifier.width(280.dp),

                                label = { Text("Nhập lại Mật khẩu") },
                                trailingIcon = {
                                    IconButton(onClick = { passVisible = !passVisible }) {
                                        Icon(
                                            painter = if (passVisible) painterResource(R.drawable.visible_off) else painterResource(
                                                R.drawable.visibility
                                            ), contentDescription = "",
                                            Modifier.size(20.dp)
                                        )
                                    }
                                },
                                supportingText = {
                                    if (reEnterPass.isNotEmpty() && password != reEnterPass) Text(
                                        "Mật khẩu phải nhiều hơn 6 ký tự"
                                    )
                                },
                                isError = if (reEnterPass.isNotEmpty() && password != reEnterPass) true else false,
                                singleLine = true,
                                maxLines = 1,
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                                ),
                                shape = RoundedCornerShape(12),
                                visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation()
                            )

                            Button(
                                onClick = {
                                    viewModel.register(email, password)
                                },
                                enabled = if (password != reEnterPass || password.length < 6) false else true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Text("Đăng ký", color = MaterialTheme.colorScheme.onPrimary)

                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Hoặc sử dụng tài khoảng:")
                        Row(horizontalArrangement = Arrangement.Center) {
                            IconButton(onClick = {
                                Log.d("FacebookLogin", "Clicked Facebook login")
                                LoginManager.getInstance().logInWithReadPermissions(
                                    activity = activity as Activity,
                                    listOf("email", "public_profile")
                                )
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.facebook),
                                    contentDescription = "",
                                    Modifier.size(32.dp),
                                    tint = Color.Unspecified
                                )
                            }
                            IconButton(onClick = {
                                val gso =
                                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                        .requestIdToken(context.getString(R.string.default_web_client_id))
                                        .requestEmail()
                                        .build()
                                val googleSignInClient = GoogleSignIn.getClient(context, gso)

                                googleSignInLauncher.launch(googleSignInClient.signInIntent)
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.google),
                                    contentDescription = "",
                                    Modifier.size(32.dp),
                                    tint = Color.Unspecified
                                )
                            }
                        }
                    }


                }
            }

        }

    }
}
