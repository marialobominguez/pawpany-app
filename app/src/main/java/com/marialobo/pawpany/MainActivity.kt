package com.marialobo.pawpany

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.marialobo.pawpany.ui.screens.PantallaBienvenida
import com.marialobo.pawpany.ui.screens.InicioORegistro

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
                    InicioORegistro()
                }
            }
        }
    }
}