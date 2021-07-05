package com.vtiahotenkov.gorillastestassignment.feature.postDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vtiahotenkov.gorillastestassignment.repository.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow


class PostDetailsViewModel(post: Post) : ViewModel() {

    private val _viewStateFlow = MutableStateFlow(PostDetailsViewState(post))
    val viewStateFlow: Flow<PostDetailsViewState> = _viewStateFlow

    class Factory constructor(
        val post: Post
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            if (modelClass == PostDetailsViewModel::class.java) {
                PostDetailsViewModel(post) as T
            } else {
                error("Unexpected class ${modelClass.name}")
            }
    }
}

data class PostDetailsViewState(val post: Post)