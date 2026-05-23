package com.marialobo.pawpany.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// modelos de datos
data class Cuidador(val nombre: String, val estrellas: Int, val distancia: String)
data class MascotaFeed(val nombre: String, val especie: String, val raza: String, val distancia: String)

// pantalla de búsqueda a la que le paso el rol (por defecto dueño para pruebas)
@Composable
fun PantallaFeed(rolUsuario: String = "cuidador") {

    // Lista si eres DUEÑO
    val listaCuidadores = listOf(
        Cuidador("Thiago Salmón", 5, "A 2 km de distancia"),
        Cuidador("María González", 4, "A 1.5 km de distancia"),
        Cuidador("Luis Martínez", 2, "A 3 km de distancia")
    )

    // Lista si eres CUIDADOR
    val listaMascotas = listOf(
        MascotaFeed("Toby", "Gato","Bosque de Noruega", "A 1 km de distancia"),
        MascotaFeed("Luna", "Perro","Bichón Maltés", "A 2.5 km de distancia"),
        MascotaFeed("Rocky", "Hámster","Ruso", "A 0.5 km de distancia")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF7EE))
    ) {
        // header fijo
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFDE0C4))
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.LocationOn, contentDescription = "Ubicación", tint = Color(0xFFB55D3E))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Cerca de ti", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB55D3E))
            }
        }

        // subtítulo que cambia dependiendo del rol
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFCD0A1).copy(alpha = 0.5f))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            val subtitulo = if (rolUsuario == "dueño") "¿Quién cuidará de tu mascota hoy?" else "¿A qué peludo cuidarás hoy?"
            Text(text = subtitulo, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
        }

        // lista con las tarjetas
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (rolUsuario == "dueño") {
                items(listaCuidadores) { cuidador ->
                    TarjetaCuidador(
                        cuidador = cuidador,
                        onClick = {
                            /* TODO: Navegar al perfil de ${cuidador.nombre} */
                        }
                    )
                }
            } else {
                items(listaMascotas) { mascota ->
                    TarjetaMascota(
                        mascota = mascota,
                        onClick = {
                            /* TODO: Navegar al perfil de ${mascota.nombre} */
                        }
                    )
                }
            }
        }
    }
}

// --- 3. DISEÑO DE LAS TARJETAS ---

@Composable
fun TarjetaCuidador(cuidador: Cuidador, onClick: () -> Unit) { // <-- ¡Aquí estaba el error! Cambiado '=' por ':'
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(50.dp).background(Color(0xFFE2F0D9), CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = cuidador.nombre, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(cuidador.estrellas) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                    }
                    repeat(5 - cuidador.estrellas) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                    }
                }
            }
            Text(text = cuidador.distancia, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.align(Alignment.Top))
        }
    }
}

@Composable
fun TarjetaMascota(mascota: MascotaFeed, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(50.dp).background(Color(0xFFFDE0C4), CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.Pets, contentDescription = null, tint = Color(0xFFB55D3E), modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = mascota.nombre, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = mascota.especie, fontSize = 14.sp, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = mascota.raza, fontSize = 12.sp, color = Color.DarkGray)
            }
            Text(text = mascota.distancia, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.align(Alignment.Top))
        }
    }
}