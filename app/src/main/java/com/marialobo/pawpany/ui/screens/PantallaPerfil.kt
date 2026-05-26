package com.marialobo.pawpany.ui.screens

import android.content.Context
import android.util.Base64
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

enum class TipoTabPerfil { SOBRE_MI, SECUNDARIA, RESENAS }

@Composable
fun PantallaPerfil(rolUsuario: String = "dueño", onEditarClick: () -> Unit) {
    var tabSeleccionada by remember { mutableStateOf(TipoTabPerfil.SOBRE_MI) }
    val context = LocalContext.current

    // estados reactivos para la base de datos
    var miUsuario by remember { mutableStateOf<UsuarioOut?>(null) }
    var miMascota by remember { mutableStateOf<MascotaOut?>(null) }
    var miPerfilCuidador by remember { mutableStateOf<PerfilCuidadorOut?>(null) }
    var rolReal by remember { mutableStateOf("dueño") }
    var isLoading by remember { mutableStateOf(true) }

    // sacamos el ID del token
    val miId = remember {
        val prefs = context.getSharedPreferences("PawPanyPrefs", Context.MODE_PRIVATE)
        val token = prefs.getString("JWT_TOKEN", "") ?: ""
        try {
            val partes = token.split(".")
            JSONObject(String(Base64.decode(partes[1], Base64.DEFAULT))).getInt("id")
        } catch (e: Exception) { -1 }
    }

    // descargar mis datos
    LaunchedEffect(Unit) {
        RetrofitClient.apiService.obtenerUsuarios().enqueue(object : Callback<List<UsuarioOut>> {
            override fun onResponse(call: Call<List<UsuarioOut>>, res: Response<List<UsuarioOut>>) {
                val usuarioEncontrado = res.body()?.find { it.id == miId }
                if (usuarioEncontrado != null) {
                    miUsuario = usuarioEncontrado
                    rolReal = usuarioEncontrado.rol

                    // si soy dueño, busco mi mascota
                    if (rolReal == "dueño") {
                        RetrofitClient.apiService.obtenerMascotas().enqueue(object : Callback<List<MascotaOut>> {
                            override fun onResponse(c: Call<List<MascotaOut>>, r: Response<List<MascotaOut>>) {
                                miMascota = r.body()?.find { it.id_usuario == miId }
                                isLoading = false
                            }
                            override fun onFailure(c: Call<List<MascotaOut>>, t: Throwable) { isLoading = false }
                        })
                    }
                    // si soy cuidador, busco mi perfil
                    else {
                        RetrofitClient.apiService.obtenerCuidadores().enqueue(object : Callback<List<PerfilCuidadorOut>> {
                            override fun onResponse(c: Call<List<PerfilCuidadorOut>>, r: Response<List<PerfilCuidadorOut>>) {
                                miPerfilCuidador = r.body()?.find { it.id_usuario == miId }
                                isLoading = false
                            }
                            override fun onFailure(c: Call<List<PerfilCuidadorOut>>, t: Throwable) { isLoading = false }
                        })
                    }
                } else { isLoading = false }
            }
            override fun onFailure(call: Call<List<UsuarioOut>>, t: Throwable) { isLoading = false }
        })
    }

    BackgroundWrapper {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                // header fijo
                Column(modifier = Modifier.fillMaxWidth().background(Color(0xFFFDE0C4)).padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Mi perfil", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB55D3E))
                        Icon(Icons.Default.Settings, contentDescription = "Ajustes", tint = Color.Gray)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(70.dp).background(if (rolReal == "dueño") Color(0xFFE2F0D9) else Color.White, CircleShape), contentAlignment = Alignment.Center) {
                            Icon(if (rolReal == "dueño") Icons.Default.Pets else Icons.Default.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(35.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(miUsuario?.nombre ?: "Sin nombre", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(miUsuario?.ubicacion ?: "Sin ubicación", fontSize = 14.sp, color = Color.Gray)
                        }
                        IconButton(onClick = onEditarClick, modifier = Modifier.background(Color.White, RoundedCornerShape(8.dp)).border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        BotonTab("Sobre mí", tabSeleccionada == TipoTabPerfil.SOBRE_MI) { tabSeleccionada = TipoTabPerfil.SOBRE_MI }
                        BotonTab(if (rolReal == "dueño") "Mi Mascota" else "Mis tarifas", tabSeleccionada == TipoTabPerfil.SECUNDARIA) { tabSeleccionada = TipoTabPerfil.SECUNDARIA }
                        BotonTab("Reseñas", tabSeleccionada == TipoTabPerfil.RESENAS) { tabSeleccionada = TipoTabPerfil.RESENAS }
                    }
                }

                // contenido del menú
                Column(modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    when (tabSeleccionada) {
                        TipoTabPerfil.SOBRE_MI -> {
                            if (rolReal == "dueño") {
                                TarjetaInfo("Personalidad de la mascota", miMascota?.personalidad_libre ?: "Aún no has descrito a tu mascota.")
                            } else {
                                TarjetaInfo("Sobre mí", miPerfilCuidador?.estudios ?: "Sin estudios descritos.") // Ajustado a tu BD
                            }
                        }
                        TipoTabPerfil.SECUNDARIA -> {
                            if (rolReal == "dueño") {
                                TarjetaInfo("Nombre", miMascota?.nombre ?: "Sin nombre")
                                TarjetaInfo("Especie", miMascota?.especie ?: "No especificada")
                                TarjetaInfo("Raza", miMascota?.raza ?: "No especificada")
                            } else {
                                TarjetaInfo("Tarifa por servicio", "${miPerfilCuidador?.tarifa ?: 0.0} €")
                            }
                        }
                        TipoTabPerfil.RESENAS -> {
                            Text("Las reseñas se implementarán próximamente.", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaInfo(titulo: String, descripcion: String) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(titulo, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text(descripcion, color = Color.DarkGray, fontSize = 14.sp)
        }
    }
}

@Composable
fun RowScope.BotonTab(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.weight(1f).height(40.dp), shape = RoundedCornerShape(8.dp), contentPadding = PaddingValues(0.dp), colors = ButtonDefaults.buttonColors(containerColor = if (seleccionado) Color.Black else Color.White, contentColor = if (seleccionado) Color.White else Color.Black), elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)) {
        Text(texto, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}