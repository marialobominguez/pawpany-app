package com.marialobo.pawpany.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marialobo.pawpany.ui.components.BackgroundWrapper

import com.marialobo.pawpany.network.RetrofitClient
import com.marialobo.pawpany.model.LoginRequest
import com.marialobo.pawpany.model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioSesion(onBackClick: () -> Unit, onLoginSuccess: () -> Unit) {
    // para guardar lo que el usuario escriba
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf("") }

    // para guardar el tokeb eb el móvil
    val context = LocalContext.current

    BackgroundWrapper {
        Column(modifier = Modifier.fillMaxSize()) {
            // barra superior
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFDE0C4))
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Inicio de Sesión",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB55D3E)
                )
            }

            // formulario
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                Text("Correo electrónico", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                OutlinedTextField(
                    value = usuario,
                    onValueChange = {
                        usuario = it
                        mensajeError = "" // Limpiamos el error al escribir
                    },
                    placeholder = { Text("Ingrese su correo") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Text("Contraseña", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                OutlinedTextField(
                    value = contrasena,
                    onValueChange = {
                        contrasena = it
                        mensajeError = "" // Limpiamos el error al escribir
                    },
                    placeholder = { Text("Ingrese su contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                // Mostrar mensaje de error si existe
                if (mensajeError.isNotEmpty()) {
                    Text(text = mensajeError, color = Color.Red, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // BOTÓN DE ENTRAR MODIFICADO
                Button(
                    onClick = {
                        // 1. Evitamos que le den sin escribir nada
                        if (usuario.isBlank() || contrasena.isBlank()) {
                            mensajeError = "Por favor, rellena todos los campos."
                            return@Button
                        }

                        // 2. Mostramos "Cargando..."
                        isLoading = true

                        // 3. Preparamos el paquete de datos
                        val request = LoginRequest(email = usuario, password = contrasena)

                        // 4. Hacemos la llamada a AWS
                        RetrofitClient.apiService.hacerLogin(request).enqueue(object : Callback<LoginResponse> {
                            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                                isLoading = false
                                if (response.isSuccessful && response.body() != null) {
                                    val token = response.body()!!.token
                                    // Guardamos el token en la memoria del móvil
                                    val sharedPreferences = context.getSharedPreferences("PawPanyPrefs", Context.MODE_PRIVATE)
                                    sharedPreferences.edit().putString("JWT_TOKEN", token).apply()
                                    // Pasamos a la siguiente pantalla
                                    onLoginSuccess()
                                } else {
                                    // Error 401: Contraseña mal
                                    mensajeError = "Correo o contraseña incorrectos"
                                }
                            }

                            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                // Error de servidor caído o sin internet
                                isLoading = false
                                mensajeError = "Error de conexión: ${t.localizedMessage}"
                            }
                        })
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    enabled = !isLoading // Se desactiva si está cargando
                ) {
                    Text(
                        text = if (isLoading) "Comprobando..." else "Entrar",
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }

                // plvidé mi contraseña TODO: implementar si da tiempo, borrar si no da
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Olvidé mi contraseña", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Recupera acceso a tu cuenta.", fontSize = 14.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}