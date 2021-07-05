package com.vtiahotenkov.gorillastestassignment.feature.posts.usecase

import com.vtiahotenkov.gorillastestassignment.repository.NextPage
import com.vtiahotenkov.gorillastestassignment.repository.PostsPage
import com.vtiahotenkov.gorillastestassignment.repository.PostsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class GetPosts
@Inject constructor(
    private val repository: PostsRepository
) {

    fun execute(argPageFlow: Flow<NextPage>, retryFlow: Flow<Unit>): Flow<Result<PostsPage>> =
        retryFlow.onStart { emit(Unit) }.flatMapLatest {
            argPageFlow.flatMapLatest {
                println("ZZZ request for $it")
                repository.queryPosts(it.page, it.limit).map { page ->
                    Result.Success(page) as Result<PostsPage>
                }
            }.catch {
                emit(Result.Error(it))
            }
        }
}