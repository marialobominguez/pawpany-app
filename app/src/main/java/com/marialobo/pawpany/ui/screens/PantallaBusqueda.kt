package com.marialobo.pawpany.ui.screens

import android.content.Context
import android.util.Base64
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
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

data class ResultadoBusqueda(val nombre: String, val ubicacion: String, val etiquetaSecundaria: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaBusqueda(rolUsuario: String = "dueño", onVerPerfilClick: (String, String) -> Unit) {
    var textoBusqueda by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Estados para la carga desde AWS
    var todosLosDatos by remember { mutableStateOf<List<ResultadoBusqueda>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val miId = remember {
        val prefs = context.getSharedPreferences("PawPanyPrefs", Context.MODE_PRIVATE)
        val token = prefs.getString("JWT_TOKEN", "") ?: ""
        try {
            val partes = token.split(".")
            if (partes.size == 3) JSONObject(String(Base64.decode(partes[1], Base64.DEFAULT))).getInt("id") else -1
        } catch (e: Exception) { -1 }
    }

    // Carga inicial
    LaunchedEffect(rolUsuario) {
        RetrofitClient.apiService.obtenerUsuarios().enqueue(object : Callback<List<UsuarioOut>> {
            override fun onResponse(call: Call<List<UsuarioOut>>, resU: Response<List<UsuarioOut>>) {
                val usuarios = resU.body() ?: emptyList()
                if (rolUsuario == "dueño") {
                    RetrofitClient.apiService.obtenerCuidadores().enqueue(object : Callback<List<PerfilCuidadorOut>> {
                        override fun onResponse(call: Call<List<PerfilCuidadorOut>>, resC: Response<List<PerfilCuidadorOut>>) {
                            val cuidadores = resC.body() ?: emptyList()
                            todosLosDatos = cuidadores.filter { it.id_usuario != miId }.mapNotNull { perfil ->
                                val user = usuarios.find { it.id == perfil.id_usuario }
                                user?.let { ResultadoBusqueda(it.nombre, it.ubicacion?: "Ubicación desconocida", "Cuidador") }
                            }
                            isLoading = false
                        }
                        override fun onFailure(call: Call<List<PerfilCuidadorOut>>, t: Throwable) { isLoading = false }
                    })
                } else {
                    RetrofitClient.apiService.obtenerMascotas().enqueue(object : Callback<List<MascotaOut>> {
                        override fun onResponse(call: Call<List<MascotaOut>>, resM: Response<List<MascotaOut>>) {
                            todosLosDatos = (resM.body() ?: emptyList())
                                .filter { it.id_usuario != miId }
                                .map { ResultadoBusqueda(it.nombre, "Ubicación genérica", it.especie ?: "Mascota") }
                            isLoading = false
                        }
                        override fun onFailure(call: Call<List<MascotaOut>>, t: Throwable) { isLoading = false }
                    })
                }
            }
            override fun onFailure(call: Call<List<UsuarioOut>>, t: Throwable) { isLoading = false }
        })
    }

    // Búsqueda en tiempo real
    val resultadosFiltrados by remember(textoBusqueda, todosLosDatos) {
        derivedStateOf {
            if (textoBusqueda.isEmpty()) todosLosDatos
            else todosLosDatos.filter {
                it.nombre.contains(textoBusqueda, true) || it.ubicacion.contains(textoBusqueda, true)
            }
        }
    }

    BackgroundWrapper {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxWidth().background(Color(0xFFFDE0C4)).padding(16.dp)) {
                Text(text = if (rolUsuario == "dueño") "Buscador de cuidadores" else "Buscador de Mascotas", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB55D3E))
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = textoBusqueda,
                    onValueChange = { textoBusqueda = it },
                    placeholder = { Text("Escribe la ubicación o nombre...", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                )
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(resultadosFiltrados) { resultado ->
                        FilaResultado(resultado, rolUsuario) {
                            onVerPerfilClick(resultado.nombre, if (rolUsuario == "dueño") "cuidador" else "dueño")
                        }
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)
                    }
                }
            }
        }
    }
}

@Composable
fun FilaResultado(resultado: ResultadoBusqueda, rolUsuario: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.background(Color.White).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(45.dp).background(if (rolUsuario == "dueño") Color(0xFFE2F0D9) else Color(0xFFFDE0C4), CircleShape), contentAlignment = Alignment.Center) {
            Icon(if (rolUsuario == "dueño") Icons.Filled.Person else Icons.Filled.Pets, null, tint = if (rolUsuario == "dueño") Color.Gray else Color(0xFFB55D3E), modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(resultado.nombre, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(resultado.ubicacion, fontSize = 13.sp, color = Color.Gray)
        }
        Text(resultado.etiquetaSecundaria, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
    }
}