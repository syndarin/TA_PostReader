package com.vtiahotenkov.gorillastestassignment.repository

data class PostsPage(
    val posts: List<Post>,
    val nextPage: NextPage?
)

data class Post(
    val id: String,
    val title: String,
    val content: String,
    val author: Author?
)

data class Author(val name: String, val username: String)

data class NextPage(val page: Int, val limit: Int)