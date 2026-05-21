package com.marialobo.pawpany.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.marialobo.pawpany.R

@Composable
fun PantallaBienvenida(onTimeout: () -> Unit) {
    // variable para controlar la visibilidad
    var isVisible by remember { mutableStateOf(true) }

    // animación de la opacidad
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 1000), // el difuminado dura 1 segundo
        label = "animacion_difuminado"
    )

    // el temporizador
    LaunchedEffect(Unit) {
        delay(3000) // espera 3 segundos
        isVisible = false // activa el difuminado
        delay(1000) // espera a que termine el difuminado
        onTimeout() // le avisamos a la app de que cambie de pantalla
    }


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.fondo_huellas),
            contentDescription = "Fondo de huellas",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Image(
            painter = painterResource(id = R.drawable.pawpany_logo_ovalado),
            contentDescription = "Logo PawPany",
            modifier = Modifier
                .size(280.dp)
                .alpha(alpha) // efecto de difuminado
        )
    }
}