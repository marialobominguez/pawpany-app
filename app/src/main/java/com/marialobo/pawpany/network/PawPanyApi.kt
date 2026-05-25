package com.marialobo.pawpany.network

import com.marialobo.pawpany.model.LoginRequest
import com.marialobo.pawpany.model.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface PawPanyApi {

    // ruta para iniciar sesión
    @POST("login")
    fun hacerLogin(@Body credenciales: LoginRequest): Call<LoginResponse>

    // Poco a poco iremos añadiendo aquí el resto (obtener usuarios, mascotas, etc.)
}