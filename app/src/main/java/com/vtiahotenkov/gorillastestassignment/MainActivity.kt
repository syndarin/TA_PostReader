package com.vtiahotenkov.gorillastestassignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vtiahotenkov.gorillastestassignment.feature.posts.PostListFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            with(supportFragmentManager.beginTransaction()) {
                replace(R.id.container, PostListFragment())
                commit()
            }
        }
    }
}