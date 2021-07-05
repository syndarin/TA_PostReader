package com.vtiahotenkov.gorillastestassignment.feature.posts.usecase

import com.vtiahotenkov.gorillastestassignment.repository.NextPage
import com.vtiahotenkov.gorillastestassignment.repository.PostsPage
import com.vtiahotenkov.gorillastestassignment.repository.PostsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest

class GetPosts
@Inject constructor(
    private val repository: PostsRepository
) {

    fun execute(pageFlow: Flow<NextPage>): Flow<PostsPage> =
        pageFlow.flatMapLatest {
            repository.queryPosts(it.page, it.limit)
        }
}