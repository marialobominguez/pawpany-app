package com.marialobo.pawpany.network

import com.marialobo.pawpany.model.LoginRequest
import com.marialobo.pawpany.model.LoginResponse
import com.marialobo.pawpany.model.MascotaCreate
import com.marialobo.pawpany.model.MascotaOut
import com.marialobo.pawpany.model.MascotaUpdate
import com.marialobo.pawpany.model.MensajeCreate
import com.marialobo.pawpany.model.MensajeOut
import com.marialobo.pawpany.model.PerfilCuidadorCreate
import com.marialobo.pawpany.model.PerfilCuidadorOut
import com.marialobo.pawpany.model.PerfilCuidadorUpdate
import com.marialobo.pawpany.model.UsuarioCreate
import com.marialobo.pawpany.model.UsuarioOut
import com.marialobo.pawpany.model.UsuarioUpdate
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

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

    @GET("usuarios")
    fun obtenerUsuarios(): Call<List<UsuarioOut>>

    @GET("mascotas")
    fun obtenerMascotas(): Call<List<MascotaOut>>

    @GET("perfiles-cuidadores")
    fun obtenerCuidadores(): Call<List<PerfilCuidadorOut>>

    @GET("mensajes")
    fun obtenerMensajes(@Header("x-token") token: String): Call<List<MensajeOut>>

    @POST("mensajes")
    fun enviarMensaje(
        @Header("x-token") token: String,
        @Body mensaje: MensajeCreate
    ): Call<Void>

    @PUT("usuarios/{id}")
    fun actualizarUsuario(
        @Header("x-token") token: String,
        @Path("id") id: Int,
        @Body usuario: UsuarioUpdate
    ): Call<Void>

    @PUT("mascotas/{id}") //
    fun actualizarMascota(
        @Header("x-token") token: String,
        @Path("id") idMascota: Int,
        @Body mascota: MascotaUpdate
    ): Call<Void>

    @PUT("perfiles-cuidadores/{id}")
    fun actualizarCuidador(
        @Header("x-token") token: String,
        @Path("id") idCuidador: Int,
        @Body cuidador: PerfilCuidadorUpdate
    ): Call<Void>
}