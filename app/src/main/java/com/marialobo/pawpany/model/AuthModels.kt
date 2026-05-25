package com.marialobo.pawpany.model

// --- MOLDES DE LOGIN ---
data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val mensaje: String,
    val token: String
)

// --- MOLDES DE USUARIO ---
data class UsuarioCreate(
    val username: String,
    val email: String,
    val password_hash: String,
    val nombre: String,
    val apellido1: String = "",
    val apellido2: String = "",
    val ubicacion: String,
    val rol: String
)

data class UsuarioOut(
    val id: Int,
    val email: String
)

// --- MOLDE DE MASCOTA ---
data class MascotaCreate(
    val nombre: String,
    val especie: String,
    val raza: String,
    val personalidad_libre: String,
    val id_usuario: Int
)

// --- MOLDE DE PERFIL CUIDADOR ---
data class PerfilCuidadorCreate(
    val estudios: String,
    val sobre_mi: String,
    val tarifa: Double, // Double porque en tu base de datos es un DECIMAL
    val id_usuario: Int
)