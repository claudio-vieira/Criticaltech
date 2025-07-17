package com.example.criticaltech.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.fragment.app.FragmentActivity
import com.example.criticaltech.R
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
            .padding(dimensionResource(R.dimen.biometric_screen_padding)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.AddCircle,
            contentDescription = stringResource(R.string.fingerprint_icon_description),
            modifier = Modifier.size(dimensionResource(R.dimen.biometric_icon_size)),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_large)))

        Text(
            text = stringResource(R.string.biometric_auth_title),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))

        when {
            isAuthenticating -> {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))
                Text(
                    text = stringResource(R.string.biometric_auth_instruction),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }

            authState is BiometricAuthResult.Error -> {
                Text(
                    text = stringResource(R.string.auth_error_title),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))

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
                    Text(stringResource(R.string.try_again_button))
                }
            }

            authState is BiometricAuthResult.Failed -> {
                Text(
                    text = stringResource(R.string.auth_failed_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))

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
                    Text(stringResource(R.string.try_again_button))
                }
            }
        }
    }
}