package com.marialobo.pawpany.ui.screens

import android.content.Context
import android.util.Base64
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marialobo.pawpany.ui.components.BackgroundWrapper

import com.marialobo.pawpany.network.RetrofitClient
import com.marialobo.pawpany.model.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPerfil(rolUsuario: String = "dueño", onBackClick: () -> Unit) {
    val context = LocalContext.current

    // variables del formulario
    var nombre by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var especie by remember { mutableStateOf("") }
    var raza by remember { mutableStateOf("") }
    var tarifa by remember { mutableStateOf("") }

    var rolReal by remember { mutableStateOf("dueño") }
    var isSaving by remember { mutableStateOf(false) }

    var idMascotaReal by remember { mutableStateOf(-1) }
    var idCuidadorReal by remember { mutableStateOf(-1) }

    val prefs = context.getSharedPreferences("PawPanyPrefs", Context.MODE_PRIVATE)
    val token = prefs.getString("JWT_TOKEN", "") ?: ""

    val miId = remember {
        try {
            val partes = token.split(".")
            JSONObject(String(Base64.decode(partes[1], Base64.DEFAULT))).getInt("id")
        } catch (e: Exception) { -1 }
    }

    // descargar datos actuales para rellenar el formulario
    LaunchedEffect(Unit) {
        RetrofitClient.apiService.obtenerUsuarios().enqueue(object : Callback<List<UsuarioOut>> {
            override fun onResponse(call: Call<List<UsuarioOut>>, res: Response<List<UsuarioOut>>) {
                val miUser = res.body()?.find { it.id == miId }
                if (miUser != null) {
                    nombre = miUser.nombre
                    ubicacion = miUser.ubicacion ?: ""
                    rolReal = miUser.rol

                    if (rolReal == "dueño") {
                        RetrofitClient.apiService.obtenerMascotas().enqueue(object : Callback<List<MascotaOut>> {
                            override fun onResponse(c: Call<List<MascotaOut>>, r: Response<List<MascotaOut>>) {
                                val miMascota = r.body()?.find { it.id_usuario == miId }
                                if (miMascota != null) {
                                    idMascotaReal = miMascota.id
                                    especie = miMascota.especie ?: ""
                                    raza = miMascota.raza ?: ""
                                    descripcion = miMascota.personalidad_libre ?: ""
                                }
                            }
                            override fun onFailure(c: Call<List<MascotaOut>>, t: Throwable) {}
                        })
                    } else {
                        RetrofitClient.apiService.obtenerCuidadores().enqueue(object : Callback<List<PerfilCuidadorOut>> {
                            override fun onResponse(c: Call<List<PerfilCuidadorOut>>, r: Response<List<PerfilCuidadorOut>>) {
                                val miCuidador = r.body()?.find { it.id_usuario == miId }
                                if (miCuidador != null) {
                                    idCuidadorReal = miCuidador.id
                                    tarifa = miCuidador.tarifa.toString()
                                    descripcion = miCuidador.estudios ?: ""
                                }
                            }
                            override fun onFailure(c: Call<List<PerfilCuidadorOut>>, t: Throwable) {}
                        })
                    }
                }
            }
            override fun onFailure(call: Call<List<UsuarioOut>>, t: Throwable) {}
        })
    }

    BackgroundWrapper {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth().background(Color(0xFFFDE0C4)).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás", tint = Color(0xFFB55D3E))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text("Editar perfil", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB55D3E))
            }

            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.size(100.dp).background(Color(0xFFE2F0D9), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = "Cambiar foto", tint = Color.Gray, modifier = Modifier.size(40.dp))
                }
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Tu nombre") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = ubicacion, onValueChange = { ubicacion = it }, label = { Text("Ciudad") }, modifier = Modifier.fillMaxWidth())

                if (rolReal == "dueño") {
                    OutlinedTextField(value = especie, onValueChange = { especie = it }, label = { Text("Especie") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = raza, onValueChange = { raza = it }, label = { Text("Raza") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Personalidad de tu mascota") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                } else {
                    OutlinedTextField(value = tarifa, onValueChange = { tarifa = it }, label = { Text("Tarifa (€/hora)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Estudios y experiencia") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                }
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        isSaving = true
                        val reqUser = UsuarioUpdate(nombre, ubicacion)
                        // actualizar datos
                        RetrofitClient.apiService.actualizarUsuario(token, miId, reqUser).enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, res: Response<Void>) {
                                if (res.isSuccessful) {
                                    if (rolReal == "dueño") {
                                        val reqMascota = MascotaUpdate(especie, raza, descripcion)
                                        RetrofitClient.apiService.actualizarMascota(token, idMascotaReal, reqMascota).enqueue(object: Callback<Void> {
                                            override fun onResponse(c: Call<Void>, r: Response<Void>) {
                                                isSaving = false
                                                if (r.isSuccessful) {
                                                    Toast.makeText(context, "¡Datos actualizados correctamente!", Toast.LENGTH_SHORT).show()
                                                    onBackClick()
                                                } else {
                                                    Toast.makeText(context, "Fallo mascota: ${r.code()}", Toast.LENGTH_LONG).show()
                                                }
                                            }
                                            override fun onFailure(c: Call<Void>, t: Throwable) { isSaving = false }
                                        })
                                    } else {
                                        val reqCuidador = PerfilCuidadorUpdate(tarifa.toDoubleOrNull() ?: 0.0, descripcion)
                                        RetrofitClient.apiService.actualizarCuidador(token, idCuidadorReal, reqCuidador).enqueue(object: Callback<Void> {
                                            override fun onResponse(c: Call<Void>, r: Response<Void>) {
                                                isSaving = false
                                                if (r.isSuccessful) {
                                                    Toast.makeText(context, "¡Datos actualizados correctamente!", Toast.LENGTH_SHORT).show()
                                                    onBackClick()
                                                } else {
                                                    Toast.makeText(context, "Fallo cuidador: ${r.code()}", Toast.LENGTH_LONG).show()
                                                }
                                            }
                                            override fun onFailure(c: Call<Void>, t: Throwable) { isSaving = false }
                                        })
                                    }
                                } else {
                                    isSaving = false
                                    Toast.makeText(context, "Fallo usuario: ${res.code()}", Toast.LENGTH_LONG).show()
                                }
                            }
                            override fun onFailure(call: Call<Void>, t: Throwable) { isSaving = false }
                        })
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    enabled = !isSaving
                ) {
                    Text(if (isSaving) "Guardando..." else "Actualizar datos", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}