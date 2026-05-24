package com.marialobo.pawpany.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marialobo.pawpany.ui.components.BackgroundWrapper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPerfil(rolUsuario: String = "dueño", onBackClick: () -> Unit) {
    // variables compartidas
    var nombre by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    // variables dueño
    var raza by remember { mutableStateOf("") }

    // variables cuidador
    var tarifa by remember { mutableStateOf("") }

    BackgroundWrapper {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().background(Color(0xFFFDE0C4)).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás", tint = Color(0xFFB55D3E))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text("Editar perfil", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB55D3E))
            }
            // formulario
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // foto
                Box(
                    modifier = Modifier.size(100.dp).background(Color(0xFFE2F0D9), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = "Cambiar foto", tint = Color.Gray, modifier = Modifier.size(40.dp))
                }
                // campos comunes
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text(if (rolUsuario == "dueño") "Nombre de la mascota" else "Tu nombre") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = ubicacion, onValueChange = { ubicacion = it }, label = { Text("Ciudad") }, modifier = Modifier.fillMaxWidth())
                // campos dinámicos
                if (rolUsuario == "dueño") {
                    OutlinedTextField(value = raza, onValueChange = { raza = it }, label = { Text("Especie / Raza") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Personalidad") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                } else {
                    OutlinedTextField(value = tarifa, onValueChange = { tarifa = it }, label = { Text("Tarifa (€/hora)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Estudios y experiencia") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                }
                Spacer(modifier = Modifier.height(20.dp))
                // botón guardar
                Button(
                    onClick = { /* TODO: enviar a la bbdd */ },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("Actualizar datos", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}