package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.LoginScreen
import com.example.ui.MainDashboardShell
import com.example.ui.SetupWizardScreen
import com.example.ui.VerificationGatewayScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.FishFarmViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: FishFarmViewModel = viewModel()
                val authState by viewModel.authState.collectAsState()

                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Crossfade(targetState = authState, label = "ScreenTransition") { state ->
                        when (state) {
                            "LOGIN_ID_PASS" -> {
                                LoginScreen(viewModel = viewModel)
                            }
                            "FIRST_TIME_WIZARD" -> {
                                SetupWizardScreen(viewModel = viewModel)
                            }
                            "VERIFICATION" -> {
                                VerificationGatewayScreen(viewModel = viewModel)
                            }
                            "DASHBOARD" -> {
                                MainDashboardShell(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
fun Greeting(name: String, modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier) {
    androidx.compose.material3.Text(text = "Hello $name!", modifier = modifier)
}

