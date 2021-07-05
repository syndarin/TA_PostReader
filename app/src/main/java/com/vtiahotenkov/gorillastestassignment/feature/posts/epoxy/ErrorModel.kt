package com.vtiahotenkov.gorillastestassignment.feature.posts.epoxy

import android.view.View
import android.view.ViewParent
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.apollographql.apollo.exception.ApolloNetworkException
import com.vtiahotenkov.gorillastestassignment.R
import com.vtiahotenkov.gorillastestassignment.databinding.PostsItemErrorItemBinding
import com.vtiahotenkov.gorillastestassignment.feature.EventListener
import com.vtiahotenkov.gorillastestassignment.feature.posts.RetryOnError
import java.io.IOException

data class ErrorModel constructor(
    private val eventListener: EventListener,
    private val th: Throwable,
    private val allowRetry: Boolean
) : EpoxyModelWithHolder<ErrorModel.Holder>() {

    init {
        id(javaClass.name)
    }

    override fun bind(holder: Holder) {
        super.bind(holder)

        with(holder.binding) {
            textErrorMessage.setText(
                when (th) {
                    is IOException, is ApolloNetworkException -> R.string.message_io_exception
                    else -> R.string.message_other_exception
                }
            )

            if (allowRetry) {
                buttonOnErrorAction.visibility = View.VISIBLE
                buttonOnErrorAction.setOnClickListener {
                    eventListener.onEvent(RetryOnError)
                }
            } else {
                buttonOnErrorAction.visibility = View.GONE
            }
        }
    }

    override fun unbind(holder: Holder) {
        super.unbind(holder)
        holder.binding.buttonOnErrorAction.setOnClickListener(null)
    }

    override fun getDefaultLayout(): Int = R.layout.posts_item_error_item

    override fun createNewHolder(parent: ViewParent): Holder = Holder()

    class Holder : EpoxyHolder() {
        lateinit var binding: PostsItemErrorItemBinding
        override fun bindView(itemView: View) {
            binding = PostsItemErrorItemBinding.bind(itemView)
        }
    }
}