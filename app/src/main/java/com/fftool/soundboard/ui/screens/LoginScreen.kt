package com.fftool.soundboard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fftool.soundboard.data.preferences.SecurePreferences
import com.fftool.soundboard.ui.components.PremiumButton
import com.fftool.soundboard.ui.theme.AccentPrimary
import com.fftool.soundboard.ui.theme.BackgroundPrimary
import com.fftool.soundboard.ui.theme.BackgroundSecondary
import com.fftool.soundboard.ui.theme.BorderSubtle
import com.fftool.soundboard.ui.theme.ErrorRed
import com.fftool.soundboard.ui.theme.TextDisabled
import com.fftool.soundboard.ui.theme.TextPrimary
import com.fftool.soundboard.ui.theme.TextSecondary

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var error by remember { mutableStateOf<String?>(null) }
    val prefs = remember { SecurePreferences(context) }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Sign in to continue",
            fontSize = 14.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it; error = null },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentPrimary,
                unfocusedBorderColor = BorderSubtle,
                cursorColor = AccentPrimary,
                focusedLabelColor = AccentPrimary,
                unfocusedLabelColor = TextSecondary,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; error = null },
            label = { Text("Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (showPassword) VisualTransformation.None
                else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (showPassword) Icons.Filled.VisibilityOff
                            else Icons.Filled.Visibility,
                        contentDescription = if (showPassword) "Hide password" else "Show password",
                        tint = TextSecondary
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentPrimary,
                unfocusedBorderColor = BorderSubtle,
                cursorColor = AccentPrimary,
                focusedLabelColor = AccentPrimary,
                unfocusedLabelColor = TextSecondary,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    validateLogin(username, password, prefs, onLoginSuccess) { error = it }
                }
            )
        )

        if (error != null) {
            Text(
                text = error!!,
                color = ErrorRed,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        PremiumButton(
            text = "Sign In",
            onClick = {
                focusManager.clearFocus()
                validateLogin(username, password, prefs, onLoginSuccess) { error = it }
            }
        )
    }
}

private fun validateLogin(
    username: String,
    password: String,
    prefs: SecurePreferences,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val adminUser = prefs.adminUsername.ifBlank { "admin" }
    val adminPass = prefs.adminPassword.ifBlank { "admin123" }
    val userUser = prefs.userUsername.ifBlank { "user" }
    val userPass = prefs.userPassword.ifBlank { "user123" }

    when {
        username.isBlank() || password.isBlank() -> {
            onError("Please enter both username and password")
        }
        username == adminUser && password == adminPass -> {
            onSuccess()
        }
        username == userUser && password == userPass -> {
            onSuccess()
        }
        else -> {
            onError("Invalid username or password")
        }
    }
}
