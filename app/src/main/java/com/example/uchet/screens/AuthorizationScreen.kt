package com.example.uchet.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uchet.activity.MainActivity
import com.example.uchet.viewModels.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun AuthorizationScreen(
    context: Context
) {
    val authViewModel = viewModel(modelClass = AuthViewModel::class.java)
    Column {
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f)
                .background(color = Color.LightGray),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RFID("auth",context)
        }
        AuthorizationMenu(authViewModel,context)
    }
}

@Composable
fun AuthorizationMenu(authViewModel: AuthViewModel, context: Context){
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var f by remember { mutableStateOf(false) }
    if (showDialog) {
        if(f) {
            Alert(
                onConfirm = {
                    showDialog = false
                },
                text = "Введен неверный логин или пароль"
            )
        }else{
            Alert(
                onConfirm = {
                    showDialog = false
                },
                text = "Не введен логин или пароль"
            )
        }
    }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    )
    {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .background(color = Color.LightGray)
        ) {
            Text(text = "Авторизация",
                modifier = Modifier.padding(vertical = 16.dp)
            )
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Логин") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            var passwordVisibility by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        coroutineScope.launch {
                            if(authViewModel.auth(username, password)){
                                context.startActivity(Intent(context, MainActivity::class.java))
                            }else{
                                if(username=="" || password=="") f = false
                                else f = true
                                showDialog = true
                            }
                        }
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                trailingIcon = {
                    IconButton(
                        onClick = { passwordVisibility = !passwordVisibility }
                    ) {
                        Icon(
                            imageVector = if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisibility) "Скрыть пароль" else "Показать пароль"
                        )
                    }
                }
            )
            Button(
                onClick = {
                    coroutineScope.launch {
                            if(authViewModel.auth(username, password)){
                                context.startActivity(Intent(context, MainActivity::class.java))
                            }else{
                                if(username=="" || password=="") f = false
                                else f = true
                                showDialog = true
                            }
                        }
                      },
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .fillMaxWidth(0.5f)
            ) {
                Text("Войти")
            }
        }
    }
}

@Composable
fun Alert(
    onConfirm: () -> Unit,
    text:String
) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text(text) },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                }
            ) {
                Text("Ок")
            }
        }
    )
}
