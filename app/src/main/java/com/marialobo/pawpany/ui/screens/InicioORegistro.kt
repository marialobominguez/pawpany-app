package com.marialobo.pawpany.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marialobo.pawpany.R
import com.marialobo.pawpany.ui.components.BackgroundWrapper

@Composable
fun InicioORegistro() {
    BackgroundWrapper {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // logo
            Image(
                painter = painterResource(id = R.drawable.pawpany_logo_ovalado),
                contentDescription = "Logo",
                modifier = Modifier.size(250.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // cajita de bienvenida
            Box(
                modifier = Modifier
                    .border(2.dp, Color.White, RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Bienvenido a PawPany",
                    color = Color.DarkGray,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.weight(1.2f))

            // botones de iniciar sesión o registraar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { /* TODO */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333333))
                ) {
                    Text("Iniciar sesión")
                }

                Button(
                    onClick = { /* TODO */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333333))
                ) {
                    Text("Registrarse")
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}