package com.toloknov.summerschool.todoapp.ui.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthSdk

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    loginSuccess: () -> Unit,
    tokenSpoiled: Boolean
) {
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val authSuccess by viewModel.authSuccess.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = authSuccess) {
        if (authSuccess) {
            loginSuccess()
        }
    }

    val context = LocalContext.current

    val yaSdk = remember {
        YandexAuthSdk.create(YandexAuthOptions(context))
    }

    val launcher =
        rememberLauncherForActivityResult(yaSdk.contract) { result -> viewModel.handleResult(result) }
    val loginOptions = remember {
        YandexAuthLoginOptions()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            errorMessage?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Button(onClick = { launcher.launch(loginOptions) }) {
                Text(
                    text = "Войти через Яндекс ID",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}