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
import androidx.compose.material.icons.filled.Pets
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

@Composable
fun PantallaPerfilAjeno(
    idUsuario: Int,
    nombrePerfil: String,
    rolPerfil: String,
    onBackClick: () -> Unit,
    onContactarClick: (Int, String) -> Unit
) {
    var tabSeleccionada by remember { mutableStateOf(0) }

    BackgroundWrapper {
        Column(modifier = Modifier.fillMaxSize()) {
            // header fijo
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFDE0C4))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp, start = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color(0xFFB55D3E))
                    }
                    Text(
                        text = if (rolPerfil == "cuidador") "Perfil del Cuidador" else "Perfil de la Mascota",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB55D3E)
                    )
                }

                // foto y datos
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(70.dp).background(if (rolPerfil == "cuidador") Color.White else Color(0xFFE2F0D9), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(if (rolPerfil == "cuidador") Icons.Default.Person else Icons.Default.Pets, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(35.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(nombrePerfil, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Móstoles, España", fontSize = 14.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BotonTabAjeno("Sobre mí", tabSeleccionada == 0) { tabSeleccionada = 0 }
                    BotonTabAjeno(if (rolPerfil == "cuidador") "Servicios" else "Requisitos", tabSeleccionada == 1) { tabSeleccionada = 1 }
                    BotonTabAjeno("Reseñas", tabSeleccionada == 2) { tabSeleccionada = 2 }
                }
            }

            // contenido perfil
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (tabSeleccionada == 0) {
                    TarjetaInfo("Ubicación", "Móstoles, Madrid")
                    TarjetaInfo(if (rolPerfil == "cuidador") "Estudios" else "Raza", if (rolPerfil == "cuidador") "Técnico Auxiliar de Veterinaria" else "Bichón Maltés")
                    TarjetaInfo("Gustos / Personalidad", "Amante de los animales por vocación. Me especializo en el fascinante mundo de las mascotas...")
                } else if (tabSeleccionada == 1) {
                    TarjetaInfo(if (rolPerfil == "cuidador") "Tarifas" else "Requisitos", if (rolPerfil == "cuidador") "Paseo: 10€/h\nAlojamiento: 20€/noche" else "Vehículo propio y paciencia.")
                } else if (tabSeleccionada == 2) {
                    val listaResenas = listOf(
                        Resena("Carlos M.", 5, "Un trato espectacular, muy recomendable. Sin duda repetiremos."),
                        Resena("Laura G.", 4, "Todo muy bien, mucha comunicación en todo momento.")
                    )
                    listaResenas.forEach { resena ->
                        TarjetaResena(resena = resena)
                    }
                }
            }

            // botón contactar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .padding(24.dp)
                    .navigationBarsPadding()
            ) {
                Button(
                    onClick = { onContactarClick(idUsuario, nombrePerfil) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("Contactar", color = Color.White, fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun RowScope.BotonTabAjeno(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
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

data class Resena(val autor: String, val estrellas: Int, val comentario: String)

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
            Box(
                modifier = Modifier.size(36.dp).background(Color.LightGray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = resena.autor, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
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
                Text(text = resena.comentario, color = Color.DarkGray, fontSize = 14.sp)
            }
        }
    }
}