package com.marialobo.pawpany.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marialobo.pawpany.ui.components.BackgroundWrapper

enum class TipoTabPerfil { SOBRE_MI, SECUNDARIA, RESENAS }

@Composable
fun PantallaPerfil(rolUsuario: String = "dueño", onEditarClick: () -> Unit) {
    var tabSeleccionada by remember { mutableStateOf(TipoTabPerfil.SOBRE_MI) }

    BackgroundWrapper {
        Column(modifier = Modifier.fillMaxSize()) {
            // header fijo
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFDE0C4))
                    .padding(16.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Mi perfil", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB55D3E))
                    Icon(Icons.Default.Settings, contentDescription = "Ajustes", tint = Color.Gray)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(70.dp).background(if (rolUsuario == "dueño") Color(0xFFE2F0D9) else Color.White, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(if (rolUsuario == "dueño") Icons.Default.Pets else Icons.Default.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(35.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(if (rolUsuario == "dueño") "Nano Lobo" else "María Lobo", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Móstoles, España", fontSize = 14.sp, color = Color.Gray)
                    }
                    IconButton(
                        onClick = onEditarClick,
                        modifier = Modifier.background(Color.White, RoundedCornerShape(8.dp)).border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // menú perfil
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BotonTab("Sobre mí", tabSeleccionada == TipoTabPerfil.SOBRE_MI) { tabSeleccionada = TipoTabPerfil.SOBRE_MI }
                    BotonTab(if (rolUsuario == "dueño") "Perfil buscado" else "Mis tarifas", tabSeleccionada == TipoTabPerfil.SECUNDARIA) { tabSeleccionada = TipoTabPerfil.SECUNDARIA }
                    BotonTab("Reseñas", tabSeleccionada == TipoTabPerfil.RESENAS) { tabSeleccionada = TipoTabPerfil.RESENAS }
                }
            }

            // contenido del menú
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (tabSeleccionada) {
                    TipoTabPerfil.SOBRE_MI -> {
                        if (rolUsuario == "dueño") {
                            TarjetaInfo("Especie", "Perro")
                            TarjetaInfo("Raza", "Bichón Maltés")
                            TarjetaInfo("Personalidad", "Su pasión absoluta es la pelota. Muy activo y cariñoso.")
                        } else {
                            TarjetaInfo("Sobre mí", "Soy ATV con 5 años de experiencia en clínicas veterinarias.")
                            TarjetaInfo("Estudios", "Auxiliar Técnico Veterinario (ATV)")
                        }
                    }
                    TipoTabPerfil.SECUNDARIA -> {
                        if (rolUsuario == "dueño") {
                            TarjetaInfo("Requisitos", "Buscamos a alguien con jardín y vehículo propio por si hay urgencias.")
                        } else {
                            TarjetaInfo("Tarifa por paseo", "12€ / hora")
                            TarjetaInfo("Tarifa por alojamiento", "20€ / noche")
                        }
                    }
                    TipoTabPerfil.RESENAS -> {
                        val listaResenas = listOf(
                            Resena("Lucía C.", 5, "Si buscas un perro para hacer maratones de series, es él. No se mueve de tu lado mientras le des mimos. ¡Y cuidado con decir la palabra 'pollo'!"),
                            Resena("Andrea V.", 5, "¡El mejor compañero de teletrabajo! Se pasó la mañana durmiendo a mis pies y solo me pidió jugar un rato a la pelota en el descanso. Un amor."),
                            Resena("Rebeca C.", 4, "Muy tranquilo y educado. Le pongo 4 estrellas solo porque me ganó por cansancio tirándole la pelota, ¡no se cansa de traerla! Pero en cuanto entramos en casa, es un peluche.")
                        )
                        listaResenas.forEach { resena ->
                            TarjetaResena(resena = resena)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaInfo(titulo: String, descripcion: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(titulo, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text(descripcion, color = Color.DarkGray, fontSize = 14.sp)
        }
    }
}

@Composable
fun RowScope.BotonTab(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.weight(1f).height(40.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (seleccionado) Color.Black else Color.White,
            contentColor = if (seleccionado) Color.White else Color.Black
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Text(texto, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

// Modelo para las reseñas
data class Resena(val autor: String, val estrellas: Int, val comentario: String)

// Componente de la tarjeta blanca para cada reseña
@Composable
fun TarjetaResena(resena: Resena) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Avatar de la persona que escribe la reseña
            Box(
                modifier = Modifier.size(36.dp).background(Color.LightGray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Fila superior: Nombre y Estrellas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = resena.autor, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)

                    // Estrellas doradas/grises
                    Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                        repeat(resena.estrellas) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                        }
                        repeat(5 - resena.estrellas) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(14.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Texto de la opinión
                Text(text = resena.comentario, color = Color.DarkGray, fontSize = 14.sp)
            }
        }
    }
}