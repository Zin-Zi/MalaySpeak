package com.malayspeak.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val titleMm: String,
    val titleEn: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Translate : Screen(
        route = "translate",
        titleMm = "ဘာသာပြန်",
        titleEn = "Translate",
        selectedIcon = Icons.Filled.AutoAwesome,
        unselectedIcon = Icons.Outlined.AutoAwesome
    )

    object Progress : Screen(
        route = "progress",
        titleMm = "တိုးတက်မှု",
        titleEn = "Progress",
        selectedIcon = Icons.Filled.BarChart,
        unselectedIcon = Icons.Outlined.BarChart
    )

    companion object {
        // 2-tab Bottom Navigation: Translate (Home) and Progress
        val items = listOf(Translate, Progress)
    }
}
