package com.vtiahotenkov.gorillastestassignment.repository

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class PostsPage(
    val posts: List<Post>,
    val nextPage: NextPage?
)

@Parcelize
data class Post(
    val id: String,
    val title: String,
    val content: String,
    val author: Author?
) : Parcelable

@Parcelize
data class Author(val name: String, val username: String) : Parcelable

data class NextPage(val page: Int, val limit: Int)