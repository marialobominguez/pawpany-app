package com.marialobo.pawpany.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marialobo.pawpany.ui.components.BackgroundWrapper

// mensajes posibles
enum class TipoMensaje { TEXTO, CONTRATO }

data class Mensaje(
    val texto: String = "",
    val esMio: Boolean,
    val tipo: TipoMensaje = TipoMensaje.TEXTO,
    // datos opcionales contrato
    val tipoContrato: String = "",
    val ubicacion: String = "",
    val fecha: String = "",
    val duracion: String = "",
    val salario: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMensajePrivado(nombreContacto: String, onBackClick: () -> Unit) {
    var textoEscrito by remember { mutableStateOf("") }

    // simulo conver
    val historialMensajes = listOf(
        Mensaje("Holaaaa!!!! ¿Podrías cuidar de mi michi?", esMio = true),
        Mensaje("Hola! Estaría encantado de hacerlo.", esMio = false),
        Mensaje(
            esMio = false,
            tipo = TipoMensaje.CONTRATO,
            tipoContrato = "A tiempo completo.",
            ubicacion = "Toledo",
            fecha = "2026-06-22",
            duracion = "3 meses",
            salario = "1900€"
        )
    )

    BackgroundWrapper {
        Column(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFDE0C4))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp, start = 8.dp)) {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color(0xFFB55D3E)) }
                    Text("Chat", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB55D3E))
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Box(modifier = Modifier.size(40.dp).background(Color(0xFFE2F0D9), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = nombreContacto, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                }
                HorizontalDivider(color = Color(0xFFB55D3E).copy(alpha = 0.3f), thickness = 1.dp)
            }

            // columna con mensajes
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(historialMensajes) { msj ->
                    if (msj.tipo == TipoMensaje.TEXTO) {
                        BocadilloTexto(msj)
                    } else {
                        TarjetaContratoChat(msj)
                    }
                }
            }

            // barra escribir
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .imePadding() // Sube con el teclado
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // botón adjuntar contrato
                IconButton(onClick = { /* TODO: abrir formulario de crear contrato */ }) {
                    Icon(Icons.Filled.AddCircleOutline, contentDescription = "Crear contrato", tint = Color.Gray, modifier = Modifier.size(28.dp))
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = textoEscrito,
                    onValueChange = { textoEscrito = it },
                    placeholder = { Text("Escribe un mensaje...", color = Color.Gray) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF5F5F5),
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
        }
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