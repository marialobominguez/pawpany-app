package com.marialobo.pawpany.ui.screens

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marialobo.pawpany.ui.components.BackgroundWrapper
import com.marialobo.pawpany.ui.components.CampoFormulario
import com.marialobo.pawpany.network.RetrofitClient
import com.marialobo.pawpany.model.UsuarioCreate
import com.marialobo.pawpany.model.UsuarioOut
import com.marialobo.pawpany.model.LoginRequest
import com.marialobo.pawpany.model.LoginResponse
import com.marialobo.pawpany.model.MascotaCreate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroMascota(onBackClick: () -> Unit, onRegistroExitoso: () -> Unit) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // usuario
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nombreUsuario by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }

    // mascota
    var nombreMascota by remember { mutableStateOf("") }
    var especieRaza by remember { mutableStateOf("") }
    var personalidad by remember { mutableStateOf("") }

    // estados de control
    var mensajeError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

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
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás")
                }
                Text("Registro Mascota", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB55D3E))
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

                Box(modifier = Modifier.size(80.dp).background(Color.LightGray, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Person, contentDescription = "Foto", tint = Color.White, modifier = Modifier.size(40.dp))
                }
                Text("Añadir foto de perfil", color = Color.Gray, fontSize = 14.sp)

                CampoFormulario("Nombre de usuario", "Ej: marialobo18", username) { username = it; mensajeError = "" }
                CampoFormulario("Correo electrónico", "Introduce tu email", email) { email = it; mensajeError = "" }
                CampoFormulario("Contraseña", "Crea una contraseña", password, esPassword = true) { password = it; mensajeError = "" }
                CampoFormulario("Tu Nombre", "Introduce tu nombre", nombreUsuario) { nombreUsuario = it; mensajeError = "" }
                CampoFormulario("Ciudad (Ubicación)", "Ej: Madrid", ubicacion) { ubicacion = it; mensajeError = "" }

                HorizontalDivider(color = Color.White, thickness = 2.dp)

                CampoFormulario("Nombre de la mascota", "Introduce el nombre", nombreMascota) { nombreMascota = it; mensajeError = "" }
                CampoFormulario("Especie / Raza", "Ej: Perro Labrador", especieRaza) { especieRaza = it; mensajeError = "" }
                CampoFormulario("Personalidad y Cuidados", "Describe a tu mascota", personalidad) { personalidad = it; mensajeError = "" }

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

                    Button(
                        onClick = {
                            if (username.isBlank() || email.isBlank() || password.isBlank() || nombreMascota.isBlank()) {
                                mensajeError = "Por favor, rellena los campos obligatorios."
                                return@Button
                            }

                            isLoading = true

                            // 1- crear Usuario
                            val reqUsuario = UsuarioCreate(
                                username = username,
                                email = email,
                                password_hash = password,
                                nombre = nombreUsuario,
                                ubicacion = ubicacion,
                                rol = "dueño"
                            )

                            RetrofitClient.apiService.crearUsuario(reqUsuario)
                                .enqueue(object : Callback<UsuarioOut> {
                                    override fun onResponse(call: Call<UsuarioOut>, resUser: Response<UsuarioOut>) {
                                        if (resUser.isSuccessful && resUser.body() != null) {
                                            val userId = resUser.body()!!.id

                                            // 2- iniciar sesión para obtener el Token
                                            val reqLogin = LoginRequest(email = email, password = password)
                                            RetrofitClient.apiService.hacerLogin(reqLogin)
                                                .enqueue(object : Callback<LoginResponse> {
                                                    override fun onResponse(call: Call<LoginResponse>, resLogin: Response<LoginResponse>) {
                                                        if (resLogin.isSuccessful && resLogin.body() != null) {
                                                            val token = resLogin.body()!!.token

                                                            // guardar token
                                                            context.getSharedPreferences("PawPanyPrefs", Context.MODE_PRIVATE)
                                                                .edit().putString("JWT_TOKEN", token).apply()

                                                            // 3 - registrar la mascota vinculada a ese usuario
                                                            val reqMascota = MascotaCreate(
                                                                nombre = nombreMascota,
                                                                especie = especieRaza,
                                                                raza = "",
                                                                personalidad_libre = personalidad,
                                                                id_usuario = userId
                                                            )

                                                            RetrofitClient.apiService.crearMascota(token, reqMascota).enqueue(object : Callback<Void> {
                                                                override fun onResponse(call: Call<Void>, resMascota: Response<Void>) {
                                                                    isLoading = false
                                                                    if (resMascota.isSuccessful) {
                                                                        onRegistroExitoso()
                                                                    } else {
                                                                        mensajeError = "Usuario creado, pero falló el registro de mascota."
                                                                    }
                                                                }

                                                                override fun onFailure(call: Call<Void>, t: Throwable) {
                                                                    isLoading = false; mensajeError = "Error de red al crear mascota."
                                                                }
                                                            })
                                                        } else {
                                                            isLoading = false; mensajeError = "Fallo en autologin."
                                                        }
                                                    }

                                                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                                        isLoading = false; mensajeError = "Error de red en autologin."
                                                    }
                                                })
                                        } else {
                                            isLoading = false
                                            mensajeError = "No se pudo registrar el usuario. Comprueba si el email o username ya existen."
                                        }
                                    }

                                    override fun onFailure(call: Call<UsuarioOut>, t: Throwable) {
                                        isLoading = false; mensajeError = "Error de conexión: ${t.localizedMessage}"
                                    }
                                })
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        enabled = !isLoading
                    ) {
                        Text(if (isLoading) "Registrando..." else "Registrar", color = Color.White, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}