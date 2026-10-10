package com.malayspeak.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.malayspeak.app.ui.navigation.Screen
import com.malayspeak.app.ui.screens.progress.ProgressScreen
import com.malayspeak.app.ui.screens.progress.ProgressViewModel
import com.malayspeak.app.ui.screens.translate.TranslationScreen
import com.malayspeak.app.ui.screens.translate.TranslationViewModel
import com.malayspeak.app.ui.screens.vocabulary.VocabularyScreen
import com.malayspeak.app.ui.screens.vocabulary.VocabularyViewModel
import com.malayspeak.app.ui.theme.MalaySpeakTheme

class MainActivity : ComponentActivity() {

    private val repository by lazy { (application as MalaySpeakApp).repository }

    private val translationViewModel: TranslationViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return TranslationViewModel(repository) as T
            }
        }
    }

    private val vocabularyViewModel: VocabularyViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return VocabularyViewModel(repository) as T
            }
        }
    }

    private val themePreferencesRepository by lazy { (application as MalaySpeakApp).themePreferencesRepository }

    private val themeViewModel: com.malayspeak.app.ui.theme.ThemeViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return com.malayspeak.app.ui.theme.ThemeViewModel(themePreferencesRepository) as T
            }
        }
    }

    private val progressViewModel: ProgressViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ProgressViewModel(repository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            com.malayspeak.app.ui.screens.MainScreen(
                translationViewModel = translationViewModel,
                progressViewModel = progressViewModel,
                themeViewModel = themeViewModel
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MalaySpeakAppScaffold(
    translationViewModel: TranslationViewModel,
    progressViewModel: ProgressViewModel,
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {}
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Screen.items.forEach { screen ->
                    val selected = currentDestination?.route == screen.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.titleEn
                            )
                        },
                        label = {
                            Text(
                                text = screen.titleMm,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Translate.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Translate.route) {
                TranslationScreen(
                    viewModel = translationViewModel,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme
                )
            }
            composable(Screen.Progress.route) {
                ProgressScreen(viewModel = progressViewModel)
            }
        }
    }
}
