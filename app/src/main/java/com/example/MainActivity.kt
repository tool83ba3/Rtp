package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.RatibHalakTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel: MainViewModel = viewModel()

            val isLoggedIn by mainViewModel.isLoggedIn.collectAsStateWithLifecycle()
            val isDarkMode by mainViewModel.isDarkMode.collectAsStateWithLifecycle()
            val isRtl by mainViewModel.isRtl.collectAsStateWithLifecycle()
            val selectedModule by mainViewModel.selectedModule.collectAsStateWithLifecycle()

            var isAiAssistantOpen by remember { mutableStateOf(false) }

            RatibHalakTheme(
                darkTheme = isDarkMode,
                isRtl = isRtl
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (!isLoggedIn) {
                        AuthScreen(viewModel = mainViewModel)
                    } else {
                        AnimatedContent(
                            targetState = selectedModule,
                            label = "ModuleSwitch"
                        ) { targetModule ->
                            when (targetModule) {
                                "dashboard" -> DashboardScreen(
                                    viewModel = mainViewModel,
                                    onOpenAiAssistant = { isAiAssistantOpen = true }
                                )
                                "photos" -> PhotosScreen(viewModel = mainViewModel)
                                "bookmarks" -> BookmarksScreen(viewModel = mainViewModel)
                                "notes" -> NotesScreen(viewModel = mainViewModel)
                                "subscriptions" -> SubscriptionsScreen(viewModel = mainViewModel)
                                "youtube", "social_media" -> SocialMediaScreen(viewModel = mainViewModel)
                                "bank" -> BankAccountsScreen(viewModel = mainViewModel)
                                "donations" -> DonationsScreen(viewModel = mainViewModel)
                                "aitools" -> AiToolsScreen(viewModel = mainViewModel)
                                "passwords" -> PasswordVaultScreen(viewModel = mainViewModel)
                                "tasks" -> TasksScreen(viewModel = mainViewModel)
                                "admin" -> AdminDashboardScreen(viewModel = mainViewModel)
                                else -> DashboardScreen(
                                    viewModel = mainViewModel,
                                    onOpenAiAssistant = { isAiAssistantOpen = true }
                                )
                            }
                        }

                        if (isAiAssistantOpen) {
                            AiAssistantDialog(
                                viewModel = mainViewModel,
                                onDismiss = { isAiAssistantOpen = false }
                            )
                        }
                    }
                }
            }
        }
    }
}
