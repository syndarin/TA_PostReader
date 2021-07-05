package com.vtiahotenkov.gorillastestassignment.routing

import androidx.fragment.app.Fragment
import com.vtiahotenkov.gorillastestassignment.feature.postDetails.PostDetailsFragment
import com.vtiahotenkov.gorillastestassignment.feature.posts.PostListFragment
import com.vtiahotenkov.gorillastestassignment.repository.Post

interface Router {
    fun routeTo(destination: Destination)
}

sealed class Destination {

    abstract fun createFragment(): Fragment
    abstract val addToBackStack: Boolean

    data class PostsList(override val addToBackStack: Boolean = true) : Destination() {
        override fun createFragment(): Fragment = PostListFragment()
    }

    data class PostDetails(val post: Post, override val addToBackStack: Boolean = true) :
        Destination() {
        override fun createFragment(): Fragment = PostDetailsFragment.newInstance(post)
    }
}