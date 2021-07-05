package com.vtiahotenkov.gorillastestassignment.feature.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vtiahotenkov.gorillastestassignment.R
import com.vtiahotenkov.gorillastestassignment.routing.Destination
import com.vtiahotenkov.gorillastestassignment.routing.Router
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), Router {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            routeTo(Destination.PostsList(addToBackStack = false))
        }
    }

    override fun routeTo(destination: Destination) {
        with(supportFragmentManager.beginTransaction()) {
            val fragment = destination.createFragment()
            replace(R.id.container, fragment)
            if (destination.addToBackStack) {
                addToBackStack(fragment.javaClass.name)
            }
            commit()
        }
    }
}