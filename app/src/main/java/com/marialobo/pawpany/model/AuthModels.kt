package com.marialobo.pawpany.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val mensaje: String,
    val token: String
)

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

data class MascotaCreate(
    val nombre: String,
    val especie: String,
    val raza: String,
    val personalidad_libre: String,
    val id_usuario: Int
)

data class PerfilCuidadorCreate(
    val estudios: String,
    val sobre_mi: String,
    val tarifa: Double,
    val id_usuario: Int
)

data class UsuarioOut(
    val id: Int,
    val email: String,
    val nombre: String,
    val rol: String,
    val ubicacion: String? = "Ubicación desconocida"
)

data class MascotaOut(
    val id: Int,
    val nombre: String,
    val especie: String?,
    val raza: String?,
    val personalidad_libre: String?,
    val id_usuario: Int,
    val requisitos_tags: List<String>?
)
data class PerfilCuidadorOut(
    val id: Int,
    val id_usuario: Int,
    val estudios: String?,
    val tarifa: Double,
    val sobre_mi: String?,
    val cualidades_tags: List<String>?
)


data class MensajeOut(
    val id: Int,
    val id_remitente: Int,
    val id_destinatario: Int,
    val contenido: String,
    val fecha_envio: String
)

data class MensajeCreate(
    val id_remitente: Int,
    val id_destinatario: Int,
    val contenido: String
)

// actualizar perfil
data class UsuarioUpdate(
    val nombre: String,
    val ubicacion: String
)

data class MascotaUpdate(
    val especie: String,
    val raza: String,
    val personalidad_libre: String
)

data class PerfilCuidadorUpdate(
    val tarifa: Double,
    val sobre_mi: String
)

