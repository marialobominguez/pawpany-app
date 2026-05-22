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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marialobo.pawpany.ui.components.BackgroundWrapper
import com.marialobo.pawpany.ui.components.CampoFormulario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroMascota(onBackClick: () -> Unit) {
    val scrollState = rememberScrollState()

    // para la tabla Usuario
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nombreUsuario by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }

    // para la tabla Mascota
    var nombreMascota by remember { mutableStateOf("") }
    var especieRaza by remember { mutableStateOf("") }
    var personalidad by remember { mutableStateOf("") }

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
                IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás") }
                Text("Registro Mascota", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB55D3E))
            }

            // formulario con scroll
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // ocupa el espacio intermedio
                    .padding(horizontal = 24.dp)
                    .imePadding() // sube el contenido cuando se abre el teclado
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                Box(modifier = Modifier.size(80.dp).background(Color.LightGray, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Person, contentDescription = "Foto", tint = Color.White, modifier = Modifier.size(40.dp))
                }
                Text("Añadir foto de perfil", color = Color.Gray, fontSize = 14.sp)

                CampoFormulario("Username (Único)", "Ej: marialobo99", username) { username = it }
                CampoFormulario("Correo electrónico", "Introduce tu email", email) { email = it }
                CampoFormulario("Contraseña", "Crea una contraseña", password, esPassword = true) { password = it }
                CampoFormulario("Tu Nombre", "Introduce tu nombre", nombreUsuario) { nombreUsuario = it }
                CampoFormulario("Ciudad (Ubicación)", "Ej: Madrid", ubicacion) { ubicacion = it }

                HorizontalDivider(color = Color.White, thickness = 2.dp)

                CampoFormulario("Nombre de la mascota", "Introduce el nombre", nombreMascota) { nombreMascota = it }
                CampoFormulario("Especie / Raza", "Ej: Perro Labrador", especieRaza) { especieRaza = it }
                CampoFormulario("Personalidad y Cuidados", "Describe a tu mascota", personalidad) { personalidad = it }

                Spacer(modifier = Modifier.height(20.dp)) // Espacio de desahogo al final del scroll
            }

            // botón registrar fijo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .navigationBarsPadding() // evita que se tape con la barra del sistema
                    .padding(bottom = 16.dp)
            ) {
                Button(
                    onClick = { /* TODO: POST a la API */ },
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


