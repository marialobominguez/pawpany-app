package com.marialobo.pawpany.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Inicio : NavItem("feed", "Inicio", Icons.Filled.Home, Icons.Outlined.Home)
    object Buscar : NavItem("search", "Búsqueda", Icons.Filled.Search, Icons.Outlined.Search)
    object Chat : NavItem("chat", "Chat", Icons.Filled.Chat, Icons.Outlined.Chat)
    object Perfil : NavItem("profile", "Perfil", Icons.Filled.Person, Icons.Outlined.Person)
}

val itemsNavegacion = listOf(
    NavItem.Inicio,
    NavItem.Buscar,
    NavItem.Chat,
    NavItem.Perfil
)