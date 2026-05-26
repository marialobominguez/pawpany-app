package com.marialobo.pawpany.ui.screens

import android.content.Context
import android.util.Base64
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Person
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
import com.marialobo.pawpany.model.MensajeCreate
import com.marialobo.pawpany.model.MensajeOut
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

enum class TipoMensaje { TEXTO, CONTRATO }

data class Mensaje(
    val texto: String = "",
    val esMio: Boolean,
    val tipo: TipoMensaje = TipoMensaje.TEXTO,
    val tipoContrato: String = "",
    val ubicacion: String = "",
    val fecha: String = "",
    val duracion: String = "",
    val salario: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMensajePrivado(idDestinatario: Int, nombreContacto: String, onBackClick: () -> Unit) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("PawPanyPrefs", Context.MODE_PRIVATE)
    val token = prefs.getString("JWT_TOKEN", "") ?: ""

    val miId = remember {
        try {
            val partes = token.split(".")
            JSONObject(String(Base64.decode(partes[1], Base64.DEFAULT))).getInt("id")
        } catch (e: Exception) { -1 }
    }

    var textoEscrito by remember { mutableStateOf("") }
    var mostrarDialogoContrato by remember { mutableStateOf(false) }
    val historialMensajes = remember { mutableStateListOf<Mensaje>() }

    // Cargar mensajes desde AWS
    LaunchedEffect(idDestinatario) {
        RetrofitClient.apiService.obtenerMensajes(token).enqueue(object : Callback<List<MensajeOut>> {
            override fun onResponse(call: Call<List<MensajeOut>>, res: Response<List<MensajeOut>>) {
                if (res.isSuccessful) {
                    historialMensajes.clear()
                    res.body()?.filter { (it.id_remitente == miId && it.id_destinatario == idDestinatario) ||
                            (it.id_remitente == idDestinatario && it.id_destinatario == miId) }
                        ?.forEach { historialMensajes.add(Mensaje(it.contenido, it.id_remitente == miId)) }
                }
            }
            override fun onFailure(call: Call<List<MensajeOut>>, t: Throwable) {}
        })
    }

    BackgroundWrapper {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxWidth().background(Color(0xFFFDE0C4))) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color(0xFFB55D3E)) }
                    Text("Chat con $nombreContacto", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB55D3E))
                }
                HorizontalDivider(color = Color(0xFFB55D3E).copy(alpha = 0.3f), thickness = 1.dp)
            }

            LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp), contentPadding = PaddingValues(vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(historialMensajes) { msj -> if (msj.tipo == TipoMensaje.TEXTO) BocadilloTexto(msj) else TarjetaContratoChat(msj) }
            }

            Row(modifier = Modifier.fillMaxWidth().background(Color.White).imePadding().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { mostrarDialogoContrato = true }) {
                    Icon(Icons.Filled.AddCircleOutline, null, tint = Color(0xFFB55D3E), modifier = Modifier.size(28.dp))
                }
                OutlinedTextField(
                    value = textoEscrito,
                    onValueChange = { textoEscrito = it },
                    placeholder = { Text("Escribe...", color = Color.Gray) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp)
                )
                IconButton(onClick = {
                    if (textoEscrito.isNotBlank()) {
                        val msg = MensajeCreate(miId, idDestinatario, textoEscrito)
                        android.util.Log.d("API_DEBUG", "Enviando mensaje: $textoEscrito a $idDestinatario")

                        RetrofitClient.apiService.enviarMensaje(token, msg).enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, res: Response<Void>) {
                                if (res.isSuccessful) {
                                    android.util.Log.d("API_DEBUG", "¡Éxito! Mensaje enviado")
                                    historialMensajes.add(Mensaje(textoEscrito, true))
                                    textoEscrito = ""
                                } else {
                                    android.util.Log.e("API_DEBUG", "Error del servidor: ${res.code()} - ${res.message()}")
                                    android.widget.Toast.makeText(context, "Error ${res.code()}", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                android.util.Log.e("API_DEBUG", "Fallo de conexión: ${t.message}")
                            }
                        })
                    }
                }) { Icon(Icons.AutoMirrored.Filled.Send, null, tint = Color(0xFFB55D3E)) }
            }
        }
    }

    if (mostrarDialogoContrato) {
        DialogoCrearContrato(onDismiss = { mostrarDialogoContrato = false }, onEnviar = { historialMensajes.add(it); mostrarDialogoContrato = false })
    }
}


// bocaadillo mensaje
@Composable
fun BocadilloTexto(mensaje: Mensaje) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (mensaje.esMio) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(
                    color = if (mensaje.esMio) Color(0xFFFCD0A1) else Color.White,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (mensaje.esMio) 16.dp else 4.dp,
                        bottomEnd = if (mensaje.esMio) 4.dp else 16.dp
                    )
                )
                .padding(16.dp)
        ) {
            Text(text = mensaje.texto, fontSize = 15.sp, color = Color.Black)
        }
    }
}

// tarjeta de contrato
@Composable
fun TarjetaContratoChat(mensaje: Mensaje) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (mensaje.esMio) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier.width(280.dp).padding(vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Solicitud de Contrato", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(16.dp))

                FilaDatoContrato("Tipo:", mensaje.tipoContrato)
                FilaDatoContrato("Ubicación:", mensaje.ubicacion)
                FilaDatoContrato("Fecha:", mensaje.fecha)
                FilaDatoContrato("Duración:", mensaje.duracion)
                FilaDatoContrato("Salario:", mensaje.salario)

                Spacer(modifier = Modifier.height(20.dp))

                // botones de acción
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { /* TODO: rechazar contrato */ },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black)
                    ) { Text("Rechazar", color = Color.Black) }

                    Button(
                        onClick = { /* TODO: aceptar contrato */ },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) { Text("Aceptar", color = Color.White) }
                }
            }
        }
    }
}

@Composable
fun FilaDatoContrato(titulo: String, valor: String) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(titulo, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
        Text(valor, fontSize = 14.sp, color = Color.DarkGray)
    }
}

// formulario que pide los contratos
@Composable
fun DialogoCrearContrato(
    onDismiss: () -> Unit,
    onEnviar: (Mensaje) -> Unit
) {
    var tipo by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var duracion by remember { mutableStateOf("") }
    var salario by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val nuevoContrato = Mensaje(
                        esMio = true,
                        tipo = TipoMensaje.CONTRATO,
                        tipoContrato = tipo,
                        ubicacion = ubicacion,
                        fecha = fecha,
                        duracion = duracion,
                        salario = salario
                    )
                    onEnviar(nuevoContrato)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = RoundedCornerShape(8.dp)
            ) { Text("Enviar Contrato") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = Color.Gray) }
        },
        title = { Text("Nueva Solicitud", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = tipo, onValueChange = { tipo = it }, label = { Text("Tipo (Ej: Tiempo completo)") })
                OutlinedTextField(value = ubicacion, onValueChange = { ubicacion = it }, label = { Text("Ubicación") })
                OutlinedTextField(value = fecha, onValueChange = { fecha = it }, label = { Text("Fecha (AAAA-MM-DD)") })
                OutlinedTextField(value = duracion, onValueChange = { duracion = it }, label = { Text("Duración") })
                OutlinedTextField(value = salario, onValueChange = { salario = it }, label = { Text("Salario (Ej: 1200€)") })
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}