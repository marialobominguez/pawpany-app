package com.marialobo.pawpany.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marialobo.pawpany.R
import com.marialobo.pawpany.ui.components.BackgroundWrapper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroEligeRol(onBackClick: () -> Unit, onRoleSelected: (String) -> Unit) {
    BackgroundWrapper {
        Column(modifier = Modifier.fillMaxSize()) {

            // header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFDE0C4))
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                }
                Text(
                    text = "¿Cómo quieres usar PawPany?",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB55D3E)
                )
            }

            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Elige tu perfil para empezar",
                    fontSize = 18.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(vertical = 20.dp)
                )

                // contenedor tarjetas
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f).fillMaxHeight(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    // tarjeta mascota (dueño)
                    RolCard(
                        titulo = "Soy dueño de mascota",
                        imagenId = R.drawable.img_mascota,
                        modifier = Modifier.weight(1f),
                        onClick = { onRoleSelected("dueño") }
                    )

                    // tarjeta cuidador
                    RolCard(
                        titulo = "Quiero ser cuidador",
                        imagenId = R.drawable.img_cuidador,
                        modifier = Modifier.weight(1f),
                        onClick = { onRoleSelected("cuidador") }
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun RolCard(titulo: String, imagenId: Int, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .fillMaxHeight(0.5f)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = titulo,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            Image(
                painter = painterResource(id = imagenId),
                contentDescription = titulo,
                modifier = Modifier.size(120.dp)
            )
        }
    }
}