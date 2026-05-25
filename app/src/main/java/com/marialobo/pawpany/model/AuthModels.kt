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


// Modifica el que ya tenías para que también recoja el nombre y el rol
data class UsuarioOut(
    val id: Int,
    val email: String,
    val nombre: String,
    val rol: String,
    val ubicacion: String? = "Ubicación desconocida"
)

// Añade estos dos nuevos para leer el Feed
data class MascotaOut(
    val id: Int,
    val nombre: String,
    val especie: String?, // Pueden ser nulos en tu BD
    val raza: String?,
    val id_usuario: Int
)

data class PerfilCuidadorOut(
    val id: Int,
    val id_usuario: Int,
    val estudios: String,
    val tarifa: Double
)

data class MensajeOut(
    val id: Int,
    val id_remitente: Int,
    val id_destinatario: Int,
    val contenido: String,
    val fecha_envio: String
)