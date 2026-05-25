package com.marialobo.pawpany.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.marialobo.pawpany.ui.navigation.itemsNavegacion

@Composable
fun PantallaPrincipal(onChatPrivadoClick: (String) -> Unit, onEditarPerfilClick: () -> Unit, onVerPerfilAjenoClick: (String, String) -> Unit) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                // obtengo la ruta actual para saber qué icono rellenar
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                itemsNavegacion.forEach { item ->
                    val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(item.route) {
                                // evita acumular pantallas iguales en el historial
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = { Text(item.label) },
                        icon = {
                            Icon(
                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // donde se intercambian las pantallas centrales
        NavHost(
            navController = navController,
            startDestination = "feed",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("feed") {
                PantallaFeed(
                    rolUsuario = "dueño", // O "cuidador"
                    onVerPerfilClick = { nombre, rol -> onVerPerfilAjenoClick(nombre, rol) }
                )
            }
            composable("search") {
                PantallaBusqueda(
                    rolUsuario = "dueño",
                    onVerPerfilClick = { nombre, rol -> onVerPerfilAjenoClick(nombre, rol) }
                )
            }
            composable("chat") {
                PantallaChat(
                    onChatClick = { nombreContacto ->
                        onChatPrivadoClick(nombreContacto)
                    }
                )
            }
            composable("profile") {
                PantallaPerfil(
                    rolUsuario = "dueño",
                    onEditarClick = onEditarPerfilClick
                )
            }
        }
    }
}

// pantallas provisionales. TODO: cambiar a las pantallas reales
//@Composable fun PantallaFeed() { Text("Pantalla de Inicio") }
//@Composable fun PantallaBusqueda() { Text("Pantalla de Búsqueda") }
//@Composable fun PantallaChat() { Text("Pantalla de Chat") }
//@Composable fun PantallaPerfil() { Text("Pantalla de Perfil") }