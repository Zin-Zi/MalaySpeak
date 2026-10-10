package com.malayspeak.app.ui.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import com.malayspeak.app.ui.theme.MalaySpeakTheme
import com.malayspeak.app.ui.theme.ThemeViewModel

@Composable
fun MainScreen(
    translationViewModel: TranslationViewModel,
    progressViewModel: ProgressViewModel,
    themeViewModel: ThemeViewModel
) {
    val systemInDark = isSystemInDarkTheme()
    LaunchedEffect(systemInDark) {
        themeViewModel.syncWithSystemTheme(systemInDark)
    }

    val isDarkTheme = themeViewModel.isDarkTheme

    MalaySpeakTheme(isDarkTheme = isDarkTheme) {
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
                        onToggleTheme = { themeViewModel.toggleTheme() }
                    )
                }
                composable(Screen.Progress.route) {
                    ProgressScreen(viewModel = progressViewModel)
                }
            }
        }
    }
}
