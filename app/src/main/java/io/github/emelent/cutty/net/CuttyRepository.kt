package io.github.emelent.cutty.net

import java.io.IOException


class CuttyRepository(host: String) {

    //    private val cuttyHub:
    private val cuttyClient by lazy {
        CuttyClient.create(host).create(CuttyClient::class.java)
    }

    suspend fun wakeServer(){
        try {
            cuttyClient.wakeup()
        } catch (e : IOException) { // network call error
            TODO("IOException not implemented")
        } catch (e : Exception) { // server error
            TODO("Server exception not implemented")
        }
    }

}