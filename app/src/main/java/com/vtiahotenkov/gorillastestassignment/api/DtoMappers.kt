package com.vtiahotenkov.gorillastestassignment.api

import AllPostsQuery
import com.vtiahotenkov.gorillastestassignment.repository.Author
import com.vtiahotenkov.gorillastestassignment.repository.NextPage
import com.vtiahotenkov.gorillastestassignment.repository.Post
import com.vtiahotenkov.gorillastestassignment.repository.PostsPage

fun AllPostsQuery.Data.toDto(): PostsPage? = if (posts != null) {

    PostsPage(
        posts = posts.data?.mapNotNull {
            if (it?.id != null) {
                Post(
                    id = it.id,
                    title = it.title.toString(),
                    content = it.body.toString(),
                    author = it.user?.toDto()
                )
            } else {
                null
            }
        } ?: emptyList(),
        nextPage = posts.links?.next?.toDto()
    )
} else {
    null
}

fun AllPostsQuery.Next?.toDto(): NextPage? =
    if (this?.page != null && limit != null) {
        NextPage(page, limit)
    } else {
        null
    }

fun AllPostsQuery.User.toDto() = Author(
    name = this.name.toString(),
    username = this.username.toString()
)