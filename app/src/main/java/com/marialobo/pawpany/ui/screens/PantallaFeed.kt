package com.marialobo.pawpany.ui.screens

import android.content.Context
import android.util.Base64
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marialobo.pawpany.ui.components.BackgroundWrapper

import com.marialobo.pawpany.network.RetrofitClient
import com.marialobo.pawpany.model.MascotaOut
import com.marialobo.pawpany.model.PerfilCuidadorOut
import com.marialobo.pawpany.model.UsuarioOut
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class Cuidador(val idUsuario: Int, val nombre: String, val estrellas: Int, val distancia: String)
data class MascotaFeed(
    val idUsuario: Int,
    val nombre: String,
    val especie: String,
    val raza: String,
    val distancia: String
)
@Composable
fun PantallaFeed(rolUsuario: String = "cuidador", onVerPerfilClick: (Int, String, String) -> Unit) {

    val context = LocalContext.current
    var listaCuidadores by remember { mutableStateOf<List<Cuidador>>(emptyList()) }
    var listaMascotas by remember { mutableStateOf<List<MascotaFeed>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var mensajeError by remember { mutableStateOf("") }

    val miId = remember {
        val prefs = context.getSharedPreferences("PawPanyPrefs", Context.MODE_PRIVATE)
        val token = prefs.getString("JWT_TOKEN", "") ?: ""
        try {
            val partes = token.split(".")
            if (partes.size == 3) {
                val payload = String(Base64.decode(partes[1], Base64.DEFAULT))
                JSONObject(payload).getInt("id")
            } else -1
        } catch (e: Exception) { -1 }
    }

    LaunchedEffect(rolUsuario) {
        RetrofitClient.apiService.obtenerUsuarios().enqueue(object : Callback<List<UsuarioOut>> {
            override fun onResponse(call: Call<List<UsuarioOut>>, resUsuarios: Response<List<UsuarioOut>>) {
                if (resUsuarios.isSuccessful && resUsuarios.body() != null) {
                    val usuariosBD = resUsuarios.body()!!

                    if (rolUsuario == "dueño") {
                        RetrofitClient.apiService.obtenerCuidadores().enqueue(object : Callback<List<PerfilCuidadorOut>> {
                            override fun onResponse(call: Call<List<PerfilCuidadorOut>>, resCuidadores: Response<List<PerfilCuidadorOut>>) {
                                if (resCuidadores.isSuccessful && resCuidadores.body() != null) {
                                    val cuidadoresBD = resCuidadores.body()!!

                                    val combinados = cuidadoresBD.filter { it.id_usuario != miId }.mapNotNull { perfil ->
                                        val usuario = usuariosBD.find { it.id == perfil.id_usuario }
                                        if (usuario != null) {
                                            Cuidador(
                                                idUsuario = usuario.id,
                                                nombre = usuario.nombre,
                                                estrellas = 5,
                                                distancia = "A 2 km de distancia"
                                            )
                                        } else null
                                    }
                                    listaCuidadores = combinados
                                }
                                isLoading = false
                            }
                            override fun onFailure(call: Call<List<PerfilCuidadorOut>>, t: Throwable) { isLoading = false }
                        })
                    } else {
                        RetrofitClient.apiService.obtenerMascotas().enqueue(object : Callback<List<MascotaOut>> {
                            override fun onResponse(call: Call<List<MascotaOut>>, resMascotas: Response<List<MascotaOut>>) {
                                if (resMascotas.isSuccessful && resMascotas.body() != null) {
                                    val mascotasBD = resMascotas.body()!!

                                    val combinados = mascotasBD.filter { it.id_usuario != miId }.map { mascota ->
                                        MascotaFeed(
                                            idUsuario = mascota.id_usuario,
                                            nombre = mascota.nombre,
                                            especie = mascota.especie ?: "Desconocido",
                                            raza = mascota.raza ?: "Sin raza",
                                            distancia = "A 1.5 km de distancia"
                                        )
                                    }
                                    listaMascotas = combinados
                                }
                                isLoading = false
                            }
                            override fun onFailure(call: Call<List<MascotaOut>>, t: Throwable) { isLoading = false }
                        })
                    }
                } else { isLoading = false }
            }
            override fun onFailure(call: Call<List<UsuarioOut>>, t: Throwable) { isLoading = false }
        })
    }

    BackgroundWrapper {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxWidth().background(Color(0xFFFDE0C4)).padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, "Ubicación", tint = Color(0xFFB55D3E))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cerca de ti", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB55D3E))
                }
            }

            Box(modifier = Modifier.fillMaxWidth().background(Color(0xFFFCD0A1)).padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(text = if (rolUsuario == "dueño") "¿Quién cuidará de tu mascota hoy?" else "¿A qué peludo cuidarás hoy?", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
            }

            LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), contentPadding = PaddingValues(vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (rolUsuario == "dueño") {
                    items(listaCuidadores) { cuidador ->
                        TarjetaCuidador(cuidador = cuidador, onClick = {
                            onVerPerfilClick(cuidador.idUsuario, cuidador.nombre, "cuidador")
                        })
                    }
                } else {
                    items(listaMascotas) { mascota ->
                        TarjetaMascota(mascota = mascota, onClick = {
                            onVerPerfilClick(mascota.idUsuario, mascota.nombre, "dueño")
                        })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TarjetaCuidador(cuidador: Cuidador, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(50.dp).background(Color(0xFFE2F0D9), CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.Person, null, tint = Color.Gray, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(cuidador.nombre, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(cuidador.estrellas) { Icon(Icons.Filled.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp)) }
                    repeat(5 - cuidador.estrellas) { Icon(Icons.Filled.Star, null, tint = Color.LightGray, modifier = Modifier.size(16.dp)) }
                }
            }
            Text(cuidador.distancia, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.align(Alignment.Top))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TarjetaMascota(mascota: MascotaFeed, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(50.dp).background(Color(0xFFFDE0C4), CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.Pets, null, tint = Color(0xFFB55D3E), modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(mascota.nombre, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text(mascota.especie, fontSize = 14.sp, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(4.dp))
                Text(mascota.raza, fontSize = 12.sp, color = Color.DarkGray)
            }
            Text(mascota.distancia, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.align(Alignment.Top))
        }
    }
}