package com.example.criticaltech.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.example.criticaltech.presentation.biometric.BiometricAuthManager
import com.example.criticaltech.presentation.biometric.BiometricAuthResult
import kotlinx.coroutines.launch

@Composable
fun BiometricAuthScreen(
    onAuthenticationSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as FragmentActivity
    val scope = rememberCoroutineScope()
    val biometricAuthManager = remember { BiometricAuthManager(context) }

    var authState by remember { mutableStateOf<BiometricAuthResult?>(null) }
    var isAuthenticating by remember { mutableStateOf(false) }

    // Automatically start authentication when screen appears
    LaunchedEffect(Unit) {
        if (biometricAuthManager.isBiometricAvailable()) {
            isAuthenticating = true
            val result = biometricAuthManager.authenticate(activity)
            authState = result
            isAuthenticating = false

            if (result is BiometricAuthResult.Success) {
                onAuthenticationSuccess()
            }
        } else {
            // If biometric is not available, proceed normally
            onAuthenticationSuccess()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.AddCircle,
            contentDescription = "Fingerprint",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Biometric Authentication",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isAuthenticating -> {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Please authenticate using your fingerprint",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }

            authState is BiometricAuthResult.Error -> {
                Text(
                    text = "Authentication Error",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        scope.launch {
                            isAuthenticating = true
                            val result = biometricAuthManager.authenticate(activity)
                            authState = result
                            isAuthenticating = false

                            if (result is BiometricAuthResult.Success) {
                                onAuthenticationSuccess()
                            }
                        }
                    }
                ) {
                    Text("Try Again")
                }
            }

            authState is BiometricAuthResult.Failed -> {
                Text(
                    text = "Authentication failed. Please try again.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        scope.launch {
                            isAuthenticating = true
                            val result = biometricAuthManager.authenticate(activity)
                            authState = result
                            isAuthenticating = false

                            if (result is BiometricAuthResult.Success) {
                                onAuthenticationSuccess()
                            }
                        }
                    }
                ) {
                    Text("Try Again")
                }
            }
        }
    }
}