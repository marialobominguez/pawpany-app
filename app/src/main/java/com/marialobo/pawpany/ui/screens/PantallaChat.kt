package com.marialobo.pawpany.ui.screens

import android.content.Context
import android.util.Base64
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
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
import com.marialobo.pawpany.model.MensajeOut
import com.marialobo.pawpany.model.UsuarioOut
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class ChatItem(val idUsuario: Int, val nombre: String)

@Composable
fun PantallaChat(onChatClick: (String) -> Unit) {
    val context = LocalContext.current
    var listaChats by remember { mutableStateOf<List<ChatItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val prefs = context.getSharedPreferences("PawPanyPrefs", Context.MODE_PRIVATE)
    val token = prefs.getString("JWT_TOKEN", "") ?: ""
    val miId = try {
        val partes = token.split(".")
        JSONObject(String(Base64.decode(partes[1], Base64.DEFAULT))).getInt("id")
    } catch (e: Exception) { -1 }

    LaunchedEffect(Unit) {
        // 1 - descargar todos los mensajes
        RetrofitClient.apiService.obtenerMensajes(token).enqueue(object : Callback<List<MensajeOut>> {
            override fun onResponse(call: Call<List<MensajeOut>>, resM: Response<List<MensajeOut>>) {
                val mensajes = resM.body() ?: emptyList()

                // 2 - descargar usuarios para obtener nombres
                RetrofitClient.apiService.obtenerUsuarios().enqueue(object : Callback<List<UsuarioOut>> {
                    override fun onResponse(call: Call<List<UsuarioOut>>, resU: Response<List<UsuarioOut>>) {
                        val usuarios = resU.body() ?: emptyList()

                        // 3 - filtrar mensajes donde participo y extraer los IDs de los otros
                        val idsConversaciones = mensajes.filter { it.id_remitente == miId || it.id_destinatario == miId }
                            .map { if (it.id_remitente == miId) it.id_destinatario else it.id_remitente }
                            .distinct()

                        // 4 - mapear a nombres
                        listaChats = idsConversaciones.mapNotNull { id ->
                            val user = usuarios.find { it.id == id }
                            user?.let { ChatItem(it.id, it.nombre) }
                        }
                        isLoading = false
                    }
                    override fun onFailure(call: Call<List<UsuarioOut>>, t: Throwable) { isLoading = false }
                })
            }
            override fun onFailure(call: Call<List<MensajeOut>>, t: Throwable) { isLoading = false }
        })
    }

    BackgroundWrapper {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxWidth().background(Color(0xFFFDE0C4)).padding(16.dp)) {
                Text("Chats", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB55D3E))
            }
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(top = 8.dp)) {
                    items(listaChats) { chat ->
                        FilaChat(chat) { onChatClick(chat.nombre) }
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)
                    }
                }
            }
        }
    }
}

@Composable
fun FilaChat(chat: ChatItem, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.background(Color.White).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(45.dp).background(Color(0xFFE2F0D9), CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.Person, null, tint = Color.Gray, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = chat.nombre, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}