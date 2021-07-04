package com.vtiahotenkov.gorillastestassignment.feature.posts.epoxy

import android.view.View
import android.view.ViewParent
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.vtiahotenkov.gorillastestassignment.R
import com.vtiahotenkov.gorillastestassignment.feature.EventListener
import com.vtiahotenkov.gorillastestassignment.feature.posts.LoadNextPage
import com.vtiahotenkov.gorillastestassignment.repository.NextPage

data class LoadingMoreItemsModel constructor(
    private val eventListener: EventListener,
    private val nextPage: NextPage
) : EpoxyModelWithHolder<LoadingMoreItemsModel.Holder>() {

    init {
        id(nextPage.limit, nextPage.page)
    }

    override fun bind(holder: Holder) {
        super.bind(holder)
        eventListener.onEvent(LoadNextPage(nextPage))
    }

    override fun getDefaultLayout(): Int = R.layout.posts_item_load_more

    override fun createNewHolder(parent: ViewParent): Holder = Holder()

    class Holder : EpoxyHolder() {
        override fun bindView(itemView: View) {
            // no-op
        }
    }
}