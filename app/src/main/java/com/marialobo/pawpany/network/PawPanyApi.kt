package com.marialobo.pawpany.network

import com.marialobo.pawpany.model.LoginRequest
import com.marialobo.pawpany.model.LoginResponse
import com.marialobo.pawpany.model.MascotaCreate
import com.marialobo.pawpany.model.PerfilCuidadorCreate
import com.marialobo.pawpany.model.UsuarioCreate
import com.marialobo.pawpany.model.UsuarioOut
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PawPanyApi {

    @POST("login")
    fun hacerLogin(@Body credenciales: LoginRequest): Call<LoginResponse>

    @POST("usuarios")
    fun crearUsuario(@Body usuario: UsuarioCreate): Call<UsuarioOut>

    @POST("mascotas")
    fun crearMascota(
        @Header("x-token") token: String,
        @Body mascota: MascotaCreate
    ): Call<Void>

    @POST("perfiles-cuidadores")
    fun crearPerfilCuidador(
        @Header("x-token") token: String,
        @Body perfil: PerfilCuidadorCreate
    ): Call<Void>
}