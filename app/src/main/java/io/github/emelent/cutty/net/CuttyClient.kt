package io.github.emelent.cutty.net

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface CuttyClient {




//    @FormUrlEncoded
//    @POST("/connect/token")
//    suspend fun getToken(
//        @Field("client_id") clientID: String,
//        @Field("client_secret") clientSecret: String,
//        @Field("grant_type") grantType: String,
//        @Field("username") username: String,
//        @Field("password") password: String,
//        @Field("scope") scope: String
//    ): Response<OAuthToken


    @GET("/api/wakeup")
    suspend fun wakeup(): Response<Void>

    companion object{
        fun create(host : String): Retrofit {

            return Retrofit.Builder()
                .baseUrl(host)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}
