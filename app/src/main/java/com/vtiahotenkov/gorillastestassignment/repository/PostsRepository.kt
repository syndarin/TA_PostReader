package com.vtiahotenkov.gorillastestassignment.repository

import AllPostsQuery
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.toInput
import com.apollographql.apollo.coroutines.toFlow
import com.vtiahotenkov.gorillastestassignment.api.toDto
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import type.PageQueryOptions
import type.PaginateOptions

class PostsRepository
@Inject constructor(
    private val apolloClient: ApolloClient
) {

    fun queryPosts(page: Int, limit: Int): Flow<PostsPage> {
        val options = PageQueryOptions(
            paginate = PaginateOptions(
                page = page.toInput(),
                limit = limit.toInput()
            ).toInput()
        ).toInput()

        return apolloClient.query(AllPostsQuery(options))
            .toFlow()
            .mapNotNull {
                it.data?.toDto()
            }
    }
}

