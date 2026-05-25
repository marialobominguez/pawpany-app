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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

// <-- MODIFICADO: Imports necesarios para Retrofit y modelos
import com.marialobo.pawpany.network.RetrofitClient
import com.marialobo.pawpany.model.MensajeCreate
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import com.marialobo.pawpany.model.MensajeOut

// mensajes posibles
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
fun PantallaMensajePrivado(
    idDestinatario: Int,
    nombreContacto: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("PawPanyPrefs", Context.MODE_PRIVATE)
    val token = prefs.getString("JWT_TOKEN", "") ?: ""

    // saco el ID del token para el remitente
    val miId = remember {
        try {
            val partes = token.split(".")
            JSONObject(String(Base64.decode(partes[1], Base64.DEFAULT))).getInt("id")
        } catch (e: Exception) { -1 }
    }

    var textoEscrito by remember { mutableStateOf("") }
    val historialMensajes = remember { mutableStateListOf<Mensaje>() }

    //para los mensajes antiguos
    LaunchedEffect(idDestinatario) {
        RetrofitClient.apiService.obtenerMensajes(token).enqueue(object : Callback<List<MensajeOut>> {
            override fun onResponse(call: Call<List<MensajeOut>>, res: Response<List<MensajeOut>>) {
                if (res.isSuccessful && res.body() != null) {
                    val todosLosMensajes = res.body()!!
                    // 1 - filtramos solo la conversación entre el usuario y el contacto
                    val chatFiltrado = todosLosMensajes.filter {
                        (it.id_remitente == miId && it.id_destinatario == idDestinatario) ||
                                (it.id_remitente == idDestinatario && it.id_destinatario == miId)
                    }

                    // 2 - transformamos los datos de la bbdd al template visual de las burbujas
                    val burbujas = chatFiltrado.map { msjBD ->
                        Mensaje(
                            texto = msjBD.contenido,
                            esMio = (msjBD.id_remitente == miId),
                            tipo = TipoMensaje.TEXTO
                        )
                    }
                    // 3 - metemos las burbujas en la lista para que aparezcan en pantalla
                    historialMensajes.clear()
                    historialMensajes.addAll(burbujas)
                }
            }

            override fun onFailure(call: Call<List<MensajeOut>>, t: Throwable) {
                // si falla la red, no hago nada de momento
            }
        })
    }

    BackgroundWrapper {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxWidth().background(Color(0xFFFDE0C4))) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp, start = 8.dp)) {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás", tint = Color(0xFFB55D3E)) }
                    Text("Chat con $nombreContacto", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB55D3E))
                }
            }

            LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(historialMensajes) { msj -> BocadilloTexto(msj) }
            }

            // barra escribir
            Row(modifier = Modifier.fillMaxWidth().background(Color.White).imePadding().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = textoEscrito,
                    onValueChange = { textoEscrito = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Escribe un mensaje...") }
                )

                // enviar
                TextButton(
                    onClick = {
                        if (textoEscrito.isNotBlank()) {
                            val nuevoMensaje = MensajeCreate(miId, idDestinatario, textoEscrito)

                            // POST
                            RetrofitClient.apiService.enviarMensaje(token, nuevoMensaje).enqueue(object : Callback<Void> {
                                override fun onResponse(call: Call<Void>, res: Response<Void>) {
                                    if (res.isSuccessful) {
                                        historialMensajes.add(Mensaje(textoEscrito, true))
                                        textoEscrito = ""
                                    }
                                }
                                override fun onFailure(call: Call<Void>, t: Throwable) { /* manejo de error */ }
                            })
                        }
                    }
                ) {
                    Text("Enviar", color = Color(0xFFB55D3E), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun BocadilloTexto(mensaje: Mensaje) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (mensaje.esMio) Arrangement.End else Arrangement.Start) {
        Box(modifier = Modifier.widthIn(max = 280.dp).background(if (mensaje.esMio) Color(0xFFFCD0A1) else Color.White, RoundedCornerShape(16.dp)).padding(16.dp)) {
            Text(text = mensaje.texto, fontSize = 15.sp, color = Color.Black)
        }
    }
}