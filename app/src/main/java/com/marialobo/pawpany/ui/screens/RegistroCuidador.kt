package com.marialobo.pawpany.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marialobo.pawpany.ui.components.BackgroundWrapper
import com.marialobo.pawpany.ui.components.CampoFormulario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroCuidador(onBackClick: () -> Unit, onRegistroExitoso: () -> Unit) {
    val scrollState = rememberScrollState()

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }

    // Perfil Cuidador
    var estudios by remember { mutableStateOf("") }
    var tarifa by remember { mutableStateOf("") }
    var sobreMi by remember { mutableStateOf("") }

    var mensajeError by remember { mutableStateOf("") }

    BackgroundWrapper {
        Column(modifier = Modifier.fillMaxSize()) {

            // header fijo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFDE0C4))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        "Atrás"
                    )
                }
                Text(
                    "Registro Cuidador",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB55D3E)
                )
            }

            // formulario con scroll
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 24.dp)
                    .imePadding()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier.size(80.dp).background(Color.LightGray, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = "Foto",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Text("Añadir foto de perfil", color = Color.Gray, fontSize = 14.sp)

                CampoFormulario("Nombre de usuario", "Ej: marialobo18", username) { username = it }
                CampoFormulario("Correo electrónico", "Introduce tu email", email) { email = it }
                CampoFormulario(
                    "Contraseña",
                    "Crea una contraseña",
                    password,
                    esPassword = true
                ) { password = it }
                CampoFormulario("Nombre Completo", "Ej: María Lobo", nombre) { nombre = it }
                CampoFormulario("Ciudad", "Ej: Madrid", ubicacion) { ubicacion = it }

                HorizontalDivider(color = Color.White, thickness = 2.dp)

                CampoFormulario(
                    "Estudios / Experiencia",
                    "Ej: ATV, Veterinaria...",
                    estudios
                ) { estudios = it }
                CampoFormulario("Tarifa por hora (€)", "Ej: 15.50", tarifa) { tarifa = it }
                CampoFormulario("Sobre mí", "Preséntate a los dueños", sobreMi) { sobreMi = it }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // mensaje de error + botón
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // si hay un error, mensaje en rojo
                    if (mensajeError.isNotEmpty()) {
                        Text(
                            text = mensajeError,
                            color = Color.Red,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    // TODO: revisar validaciones para ver si tengo que añadir más
                    Button(
                        onClick = {
                            // validación
                            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                                mensajeError = "Por favor, rellena los campos obligatorios."
                            } else if (username == "admin") {
                                // simulo que el usuario ya existe
                                mensajeError = "Ese nombre de usuario ya está en uso."
                            } else {
                                // si t0do está bien, borro el error y pasa a la siguiente pantalla
                                mensajeError = ""
                                onRegistroExitoso()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text("Registrar", color = Color.White, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}