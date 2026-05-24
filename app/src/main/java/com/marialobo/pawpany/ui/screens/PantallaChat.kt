package com.marialobo.pawpany.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marialobo.pawpany.ui.components.BackgroundWrapper

// modelo de datos sencillo para la lista de chats
data class ChatItem(val nombre: String)

@Composable
fun PantallaChat(onChatClick: (String) -> Unit) {
    // simulo los datos que ves en tu figma
    val listaChats = listOf(
        ChatItem("Thiago Salmón"),
        ChatItem("María Lobo"),
        ChatItem("Sócrates Todorroca"),
        ChatItem("Susana Azul"),
        ChatItem("Ana Torres")
    )

    BackgroundWrapper {
        Column(modifier = Modifier.fillMaxSize()) {

            // header fijo
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFDE0C4))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Chats",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB55D3E)
                )
            }

            // lista de chats
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
            ) {
                items(listaChats) { chat ->
                    FilaChat(
                        chat = chat,
                        onClick = { onChatClick(chat.nombre) }
                    )
                    HorizontalDivider(
                        color = Color.LightGray.copy(alpha = 0.5f),
                        thickness = 1.dp
                    )
                }
            }
        }
    }
}

// diseño de cada fila de chat
@Composable
fun FilaChat(chat: ChatItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(Color.White.copy(alpha = 0.95f))
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // avatar
        Box(
            modifier = Modifier
                .size(45.dp)
                .background(Color(0xFFE2F0D9), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Avatar de ${chat.nombre}",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = chat.nombre,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
}