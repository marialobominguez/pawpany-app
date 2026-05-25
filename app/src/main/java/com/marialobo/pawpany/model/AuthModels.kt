package com.marialobo.pawpany.model

// lo que envío al servidor
data class LoginRequest(
    val email: String,
    val password: String
)

// lo que el servidor responde
data class LoginResponse(
    val mensaje: String,
    val token: String
)