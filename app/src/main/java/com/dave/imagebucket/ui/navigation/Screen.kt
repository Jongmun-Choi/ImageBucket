package com.dave.imagebucket.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Search : Screen("search", "Search", Icons.Default.Search)
    data object Locker : Screen("bucket", "Bucket", Icons.Default.Favorite)
}

val bottomNavItems = listOf(
    Screen.Search,
    Screen.Locker,
)