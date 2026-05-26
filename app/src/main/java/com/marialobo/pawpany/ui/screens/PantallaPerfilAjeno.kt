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

import com.marialobo.pawpany.network.RetrofitClient
import com.marialobo.pawpany.model.MascotaOut
import com.marialobo.pawpany.model.PerfilCuidadorOut
import com.marialobo.pawpany.model.UsuarioOut
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun PantallaPerfilAjeno(
    idUsuario: Int,
    nombrePerfil: String,
    rolPerfil: String,
    onBackClick: () -> Unit,
    onContactarClick: (Int, String) -> Unit
) {
    var tabSeleccionada by remember { mutableStateOf(0) }

    var usuarioBD by remember { mutableStateOf<UsuarioOut?>(null) }
    var mascotaBD by remember { mutableStateOf<MascotaOut?>(null) }
    var cuidadorBD by remember { mutableStateOf<PerfilCuidadorOut?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(idUsuario) {
        RetrofitClient.apiService.obtenerUsuarios().enqueue(object : Callback<List<UsuarioOut>> {
            override fun onResponse(call: Call<List<UsuarioOut>>, resU: Response<List<UsuarioOut>>) {
                usuarioBD = resU.body()?.find { it.id == idUsuario }

                if (rolPerfil == "dueño") {
                    RetrofitClient.apiService.obtenerMascotas().enqueue(object : Callback<List<MascotaOut>> {
                        override fun onResponse(c: Call<List<MascotaOut>>, resM: Response<List<MascotaOut>>) {
                            mascotaBD = resM.body()?.find { it.id_usuario == idUsuario }
                            isLoading = false
                        }
                        override fun onFailure(c: Call<List<MascotaOut>>, t: Throwable) { isLoading = false }
                    })
                } else {
                    RetrofitClient.apiService.obtenerCuidadores().enqueue(object : Callback<List<PerfilCuidadorOut>> {
                        override fun onResponse(c: Call<List<PerfilCuidadorOut>>, resC: Response<List<PerfilCuidadorOut>>) {
                            cuidadorBD = resC.body()?.find { it.id_usuario == idUsuario }
                            isLoading = false
                        }
                        override fun onFailure(c: Call<List<PerfilCuidadorOut>>, t: Throwable) { isLoading = false }
                    })
                }
            }
            override fun onFailure(call: Call<List<UsuarioOut>>, t: Throwable) { isLoading = false }
        })
    }

    BackgroundWrapper {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFB55D3E))
            }
        } else {
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
                            Text(usuarioBD?.ubicacion ?: "Ubicación desconocida", fontSize = 14.sp, color = Color.Gray)
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
                        if (rolPerfil == "cuidador") {
                            TarjetaInfo("Estudios", cuidadorBD?.estudios ?: "No especificado")
                            TarjetaInfo("Sobre mí", cuidadorBD?.sobre_mi ?: "Sin descripción todavía.")

                            // si tiene cualidades, las muestro separadas por un puntito
                            if (!cuidadorBD?.cualidades_tags.isNullOrEmpty()) {
                                TarjetaInfo("Cualidades", cuidadorBD?.cualidades_tags?.joinToString(" • ") ?: "")
                            }
                        } else {
                            TarjetaInfo("Especie", mascotaBD?.especie ?: "No especificada")
                            TarjetaInfo("Raza", mascotaBD?.raza ?: "No especificada")
                            // <-- AQUÍ ESTÁ LA MAGIA: LEEMOS LA PERSONALIDAD REAL -->
                            TarjetaInfo("Personalidad", mascotaBD?.personalidad_libre ?: "Sin descripción todavía.")

                            if (!mascotaBD?.requisitos_tags.isNullOrEmpty()) {
                                TarjetaInfo("Requisitos", mascotaBD?.requisitos_tags?.joinToString(" • ") ?: "")
                            }
                        }
                    } else if (tabSeleccionada == 1) {
                        if (rolPerfil == "cuidador") {
                            TarjetaInfo("Tarifa", "${cuidadorBD?.tarifa ?: 0.0} €/hora")
                        } else {
                            // Si en la base de datos hay requisitos_tags, los mostramos aquí también o dejamos esto fijo por ahora
                            TarjetaInfo("Requisitos extra", "A concretar en el chat.")
                        }
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