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

// modelos de datos
data class Cuidador(val nombre: String, val estrellas: Int, val distancia: String)
data class MascotaFeed(
    val nombre: String,
    val especie: String,
    val raza: String,
    val distancia: String
)

// pantalla de búsqueda a la que le paso el rol (por defecto dueño para pruebas)
@Composable
fun PantallaFeed(rolUsuario: String = "cuidador", onVerPerfilClick: (String, String) -> Unit) {

    val context = LocalContext.current
    // Variables reactivas para guardar los datos de AWS
    var listaCuidadores by remember { mutableStateOf<List<Cuidador>>(emptyList()) }
    var listaMascotas by remember { mutableStateOf<List<MascotaFeed>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var mensajeError by remember { mutableStateOf("") }

    // leer el id del usuario desde el token para no aparecer en tu propio feed
    val miId = remember {
        val prefs = context.getSharedPreferences("PawPanyPrefs", Context.MODE_PRIVATE)
        val token = prefs.getString("JWT_TOKEN", "") ?: ""
        try {
            val partes = token.split(".")
            if (partes.size == 3) {
                // El payload está en la segunda parte del token (índice 1)
                val payload = String(Base64.decode(partes[1], Base64.DEFAULT))
                JSONObject(payload).getInt("id") // Sacamos tu ID de la base de datos
            } else -1
        } catch (e: Exception) { -1 } // Si hay error, asignamos -1
    }

    // esto se ejecuta automáticamente al abrir la pantalla
    LaunchedEffect(rolUsuario) {
        // 1 - descargamos todos los usuarios
        RetrofitClient.apiService.obtenerUsuarios().enqueue(object : Callback<List<UsuarioOut>> {
            override fun onResponse(call: Call<List<UsuarioOut>>, resUsuarios: Response<List<UsuarioOut>>) {
                if (resUsuarios.isSuccessful && resUsuarios.body() != null) {
                    val usuariosBD = resUsuarios.body()!!

                    // 2a - si es dueño, descargamos cuidadores
                    if (rolUsuario == "dueño") {
                        RetrofitClient.apiService.obtenerCuidadores().enqueue(object : Callback<List<PerfilCuidadorOut>> {
                            override fun onResponse(call: Call<List<PerfilCuidadorOut>>, resCuidadores: Response<List<PerfilCuidadorOut>>) {
                                if (resCuidadores.isSuccessful && resCuidadores.body() != null) {
                                    val cuidadoresBD = resCuidadores.body()!!

                                    // buscamos el nombre del usuario que coincide con el perfil
                                    val combinados = cuidadoresBD.filter { it.id_usuario != miId }.mapNotNull { perfil ->
                                        val usuario = usuariosBD.find { it.id == perfil.id_usuario }
                                        if (usuario != null) {
                                            Cuidador(
                                                nombre = usuario.nombre,
                                                estrellas = 5, // dato simulado
                                                distancia = "A 2 km de distancia" // dato simulado
                                            )
                                        } else null
                                    }
                                    listaCuidadores = combinados
                                }
                                isLoading = false
                            }
                            override fun onFailure(call: Call<List<PerfilCuidadorOut>>, t: Throwable) {
                                isLoading = false; mensajeError = "Error al cargar cuidadores"
                            }
                        })
                    }
                    // 2b - si es cuidador, descargamos mascotas
                    else {
                        RetrofitClient.apiService.obtenerMascotas().enqueue(object : Callback<List<MascotaOut>> {
                            override fun onResponse(call: Call<List<MascotaOut>>, resMascotas: Response<List<MascotaOut>>) {
                                if (resMascotas.isSuccessful && resMascotas.body() != null) {
                                    val mascotasBD = resMascotas.body()!!

                                    val combinados = mascotasBD.filter { it.id_usuario != miId }.map { mascota ->
                                        MascotaFeed(
                                            nombre = mascota.nombre,
                                            especie = mascota.especie ?: "Desconocido",
                                            raza = mascota.raza ?: "Sin raza",
                                            distancia = "A 1.5 km de distancia" // dato simulado
                                        )
                                    }
                                    listaMascotas = combinados
                                }
                                isLoading = false
                            }
                            override fun onFailure(call: Call<List<MascotaOut>>, t: Throwable) {
                                isLoading = false; mensajeError = "Error al cargar mascotas"
                            }
                        })
                    }
                } else {
                    isLoading = false
                    mensajeError = "Error al cargar usuarios base"
                }
            }
            override fun onFailure(call: Call<List<UsuarioOut>>, t: Throwable) {
                isLoading = false
                mensajeError = "Fallo de conexión con AWS: ${t.localizedMessage}"
            }
        })
    }

    BackgroundWrapper {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // header fijo
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFDE0C4))
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = "Ubicación",
                        tint = Color(0xFFB55D3E)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Cerca de ti",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB55D3E)
                    )
                }
            }

            // subtítulo que cambia dependiendo del rol
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFCD0A1))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                val subtitulo =
                    if (rolUsuario == "dueño") "¿Quién cuidará de tu mascota hoy?" else "¿A qué peludo cuidarás hoy?"
                Text(
                    text = subtitulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray
                )
            }

            // lista con las tarjetas
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (rolUsuario == "dueño") {
                    items(listaCuidadores) { cuidador ->
                        TarjetaCuidador(
                            cuidador = cuidador,
                            onClick = {
                                onVerPerfilClick(cuidador.nombre, "cuidador")
                            }
                        )
                    }
                } else {
                    items(listaMascotas) { mascota ->
                        TarjetaMascota(
                            mascota = mascota,
                            onClick = {
                                onVerPerfilClick(mascota.nombre, "dueño")
                            }
                        )
                    }
                }
            }
        }
    }
}

// diseño tarjetas

@Composable
fun TarjetaCuidador(
    cuidador: Cuidador,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color(0xFFE2F0D9), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cuidador.nombre,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(cuidador.estrellas) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    repeat(5 - cuidador.estrellas) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            Text(
                text = cuidador.distancia,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Top)
            )
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color(0xFFFDE0C4), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Pets,
                    contentDescription = null,
                    tint = Color(0xFFB55D3E),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mascota.nombre,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = mascota.especie, fontSize = 14.sp, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = mascota.raza, fontSize = 12.sp, color = Color.DarkGray)
            }
            Text(
                text = mascota.distancia,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Top)
            )
        }
    }
}