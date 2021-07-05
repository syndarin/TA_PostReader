package com.vtiahotenkov.gorillastestassignment.feature.posts.epoxy

import com.airbnb.epoxy.EpoxyModel
import com.vtiahotenkov.gorillastestassignment.feature.EventListener
import com.vtiahotenkov.gorillastestassignment.feature.posts.PostListState

class ModelsMapper(
    private val eventListener: EventListener,
    private val contentCharsLimit: Int
) {

    fun map(state: PostListState): List<EpoxyModel<*>> = when (state) {
        is PostListState.NoData -> listOf(
            NoDataViewModel_()
                .id(NoDataView::class.java.name)
        )

        is PostListState.Loading -> listOf(
            ContentLoadingViewModel_()
                .id(ContentLoadingView::class.java.name)
        )

        is PostListState.Content -> {
            val contentItems = state.posts.map {
                PostPreviewItemModel(
                    it,
                    contentCharsLimit,
                    eventListener
                )
            }

            if (state.nextPage != null) {
                contentItems.toMutableList() + LoadingMoreItemsModel(eventListener, state.nextPage)
            } else {
                contentItems
            }
        }

        is PostListState.Error -> listOf(ErrorModel(eventListener, state.th, state.allowRetry))
    }
}