package com.marialobo.pawpany.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// datos de las tarjetas que se mostrarán
data class ResultadoBusqueda(
    val nombre: String,
    val ubicacion: String,
    val etiquetaSecundaria: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaBusqueda(rolUsuario: String = "dueño") {
    // variable de estado que guarda lo que el usuario escribe
    var textoBusqueda by remember { mutableStateOf("") }

    // simulación bbdd
    val baseDeDatos = if (rolUsuario == "dueño") {
        listOf(
            ResultadoBusqueda("Thiago Salmón", "Madrid", "Paseador"),
            ResultadoBusqueda("Sócrates Todorroca", "Barcelona", "Visitas"),
            ResultadoBusqueda("Susana Azul", "Sevilla", "Cuidados generales"),
            ResultadoBusqueda("María Lobo", "Móstoles, Madrid", "Visitas")
        )
    } else {
        listOf(
            ResultadoBusqueda("Toby", "Madrid", "Gato"),
            ResultadoBusqueda("Luna", "Sevilla", "Perro"),
            ResultadoBusqueda("Rocky", "Móstoles, Madrid", "Hámster")
        )
    }

    // simulador api
    // filtramos la lista si el nombre o la ubicación contienen lo que hemos escrito
    val resultados = if (textoBusqueda.isEmpty()) {
        baseDeDatos // mostramos tod0
    } else {
        baseDeDatos.filter {
            it.nombre.contains(textoBusqueda, ignoreCase = true) ||
                    it.ubicacion.contains(textoBusqueda, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF7EE))
    ) {
        // header y barra búsqueda
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFDE0C4))
                .padding(16.dp)
        ) {
            Text(
                text = if (rolUsuario == "dueño") "Buscador de cuidadores" else "Buscador de Mascotas",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB55D3E)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // lupa
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { textoBusqueda = it }, // actualiza el texto con cada pulsación
                placeholder = { Text("Escribe la ubicación o nombre...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar", tint = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color(0xFFB55D3E),
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }

        Text(
            text = "Resultados de búsqueda",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(16.dp)
        )

        // lista de resultados
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(resultados) { resultado ->
                FilaResultado(
                    resultado = resultado,
                    rolUsuario = rolUsuario,
                    onClick = { /* TODO: Navegar al perfil de ${resultado.nombre} */ }
                )
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)
            }
        }
    }
}

// disño del resultado de búsqueda
@Composable
fun FilaResultado(resultado: ResultadoBusqueda, rolUsuario: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icono / Avatar
        Box(
            modifier = Modifier
                .size(45.dp)
                .background(if (rolUsuario == "dueño") Color(0xFFE2F0D9) else Color(0xFFFDE0C4), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (rolUsuario == "dueño") Icons.Filled.Person else Icons.Filled.Pets,
                contentDescription = null,
                tint = if (rolUsuario == "dueño") Color.Gray else Color(0xFFB55D3E),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // nombre y ubicación
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = resultado.nombre,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = resultado.ubicacion,
                fontSize = 13.sp,
                color = Color.Gray
            )
        }

        // rol/especie
        Text(
            text = resultado.etiquetaSecundaria,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.DarkGray
        )
    }
}