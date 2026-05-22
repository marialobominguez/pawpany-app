package com.marialobo.pawpany

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.marialobo.pawpany.ui.screens.PantallaBienvenida
import com.marialobo.pawpany.ui.screens.InicioORegistro
import com.marialobo.pawpany.ui.screens.InicioSesion

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "bienvenida") {
                composable("bienvenida") {
                    PantallaBienvenida(onTimeout = {
                        navController.navigate("inicio") {
                            popUpTo("bienvenida") { inclusive = true }
                        }
                    })
                }
                composable("inicio") {
                    InicioORegistro(
                        onLoginClick = { navController.navigate("login") },
                        onRegistroClick = { navController.navigate("registro_rol") }
                    )
                }
                composable("login") {
                    InicioSesion(
                        onBackClick = { navController.popBackStack() } // para volver atrás con la flecha
                    )
                }
                composable("registro_rol") {
                    // TODO: la pantalla en la quue eliges el rol con el que te registras
                }
            }
        }
    }
}