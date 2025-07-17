package com.example.criticaltech

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import com.example.criticaltech.presentation.biometric.BiometricAuthManager
import com.example.criticaltech.presentation.navigation.NewsNavigation
import com.example.criticaltech.presentation.screen.BiometricAuthScreen
import com.example.criticaltech.presentation.ui.theme.CriticaltechTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CriticaltechTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    AuthenticationWrapper(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun AuthenticationWrapper(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val biometricAuthManager = remember { BiometricAuthManager(context) }
    var isAuthenticated by rememberSaveable { mutableStateOf(false) }
    var shouldShowBiometric by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        shouldShowBiometric = biometricAuthManager.isBiometricAvailable()
        // If biometric is not available, proceed directly to main content
        if (!shouldShowBiometric) {
            isAuthenticated = true
        }
    }

    when {
        !isAuthenticated && shouldShowBiometric -> {
            BiometricAuthScreen(
                onAuthenticationSuccess = {
                    isAuthenticated = true
                },
                modifier = modifier
            )
        }

        isAuthenticated -> {
            NewsNavigation(
                modifier = modifier
            )
        }
    }
}