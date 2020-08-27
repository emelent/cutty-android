package io.github.emelent.cutty

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavHost
import com.example.cutty.R
import io.github.emelent.cutty.net.CuttyRepository
import com.microsoft.signalr.HubConnectionBuilder
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val host = "https://avoguardo.herokuapp.com"


    private val sharedViewModel: SharedViewModel by viewModels {
        SharedViewModel.Factory(
            CuttyRepository(host),
            HubConnectionBuilder.create("$host/videoHub")
                .build()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navController = (nav_host_fragment as NavHost).navController


        sharedViewModel.navigationEvent.observe(this, Observer {
            navController.navigate(it) })

    }


}