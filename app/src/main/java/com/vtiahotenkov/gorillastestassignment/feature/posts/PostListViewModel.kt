package com.vtiahotenkov.gorillastestassignment.feature.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtiahotenkov.gorillastestassignment.feature.Event
import com.vtiahotenkov.gorillastestassignment.feature.EventListener
import com.vtiahotenkov.gorillastestassignment.feature.posts.usecase.GetPosts
import com.vtiahotenkov.gorillastestassignment.repository.NextPage
import com.vtiahotenkov.gorillastestassignment.repository.Post
import com.vtiahotenkov.gorillastestassignment.routing.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@HiltViewModel
class PostListViewModel
@Inject constructor(
    private val getPosts: GetPosts
) : ViewModel(), EventListener {

    private val _viewStateFlow = MutableStateFlow<PostListState>(PostListState.NoData)
    val viewStateFlow: Flow<PostListState> = _viewStateFlow

    private val _navigationFlow = MutableSharedFlow<Destination>(replay = 0)
    val navigationFlow: Flow<Destination> = _navigationFlow

    private val _requestedPageFlow = MutableStateFlow(NextPage(1, ITEMS_PER_PAGE))

    init {
        viewModelScope.launch {
            getPosts.execute(_requestedPageFlow)
                .onStart {
                    _viewStateFlow.value = PostListState.Loading
                }
                .collect {
                    val currentValue = _viewStateFlow.value
                    _viewStateFlow.value =
                        currentValue.reduce(PostListState.Content(it.posts, it.nextPage))
                }
        }
    }

    override fun onEvent(event: Event) {
        when (event) {
            is LoadNextPage -> _requestedPageFlow.value = event.page
            is ShowPostDetails -> viewModelScope.launch {
                _navigationFlow.emit(Destination.PostDetails(event.post))
            }
            else -> error("Unexpected event: $event")
        }
    }

    companion object {
        private const val ITEMS_PER_PAGE = 10
    }
}

sealed class PostListState {

    open fun reduce(newState: PostListState): PostListState = newState

    object NoData : PostListState()

    object Loading : PostListState()

    data class Content(
        val posts: List<Post>,
        val nextPage: NextPage?
    ) : PostListState() {

        override fun reduce(newState: PostListState): PostListState = when (newState) {
            is Content -> {
                copy(
                    posts = posts + newState.posts,
                    nextPage = newState.nextPage
                )
            }
            else -> newState
        }
    }
}