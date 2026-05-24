package com.marialobo.pawpany

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.marialobo.pawpany.ui.screens.ExitoRegistro
import com.marialobo.pawpany.ui.screens.PantallaBienvenida
import com.marialobo.pawpany.ui.screens.InicioORegistro
import com.marialobo.pawpany.ui.screens.InicioSesion
import com.marialobo.pawpany.ui.screens.PantallaPrincipal
import com.marialobo.pawpany.ui.screens.RegistroCuidador
import com.marialobo.pawpany.ui.screens.RegistroEligeRol
import com.marialobo.pawpany.ui.screens.RegistroMascota
import android.net.Uri
import com.marialobo.pawpany.ui.screens.EditarPerfil
import com.marialobo.pawpany.ui.screens.PantallaMensajePrivado

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
                        onBackClick = { navController.popBackStack() },
                        onLoginSuccess = {
                            navController.navigate("main") {
                                // borra el historial para no volver al Login dándole atrás
                                popUpTo("inicio") { inclusive = true }
                            }
                        }
                    )
                }
                composable("registro_rol") {
                    RegistroEligeRol(
                        onBackClick = { navController.popBackStack() },
                        onRoleSelected = { rol ->
                            if (rol == "dueño") {
                                navController.navigate("registro_mascota")
                            } else {
                                navController.navigate("registro_cuidador")
                            }
                        }
                    )
                }

                composable("registro_mascota") {
                    RegistroMascota(
                        onBackClick = { navController.popBackStack() },
                        onRegistroExitoso = { navController.navigate("exito_registro") }
                    )
                }
                composable("registro_cuidador") {
                    RegistroCuidador(
                        onBackClick = { navController.popBackStack() },
                        onRegistroExitoso = { navController.navigate("exito_registro") }
                    )
                }
                composable("exito_registro") {
                    ExitoRegistro(
                        onLoginClick = {
                            navController.navigate("login") {
                                // Esto limpia el historial para que si le dan a "Atrás",
                                // no vuelvan a la pantalla de "Éxito" ni al formulario.
                                popUpTo("inicio") { inclusive = false }
                            }
                        }
                    )
                }

                composable("main") {
                    PantallaPrincipal(
                        onChatPrivadoClick = { nombre ->
                            // transformo el nombre a formato seguro antes de viajar
                            val nombreSeguro = Uri.encode(nombre)
                            navController.navigate("chat_privado/$nombreSeguro")
                        },
                        onEditarPerfilClick = {
                            navController.navigate("editar_perfil")
                        }
                    )
                }

                composable("chat_privado/{nombreContacto}") { backStackEntry ->
                    // Sacamos el nombre de la URL
                    val nombre = backStackEntry.arguments?.getString("nombreContacto") ?: "Usuario"

                    PantallaMensajePrivado(
                        nombreContacto = nombre,
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable("editar_perfil") {
                    EditarPerfil(
                        rolUsuario = "dueño", // Aquí en el futuro pasaremos el rol real desde la BD
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}