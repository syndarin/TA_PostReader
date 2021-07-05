package com.vtiahotenkov.gorillastestassignment.feature.posts

import com.vtiahotenkov.gorillastestassignment.feature.Event
import com.vtiahotenkov.gorillastestassignment.repository.NextPage
import com.vtiahotenkov.gorillastestassignment.repository.Post

data class LoadNextPage(val page: NextPage): Event

data class ShowPostDetails(val post: Post): Event

object RetryOnError : Event