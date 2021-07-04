package com.vtiahotenkov.gorillastestassignment.feature.posts.epoxy

import android.view.View
import android.view.ViewParent
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.vtiahotenkov.gorillastestassignment.R
import com.vtiahotenkov.gorillastestassignment.databinding.PostsItemContentItemBinding
import com.vtiahotenkov.gorillastestassignment.feature.EventListener
import com.vtiahotenkov.gorillastestassignment.feature.posts.ShowPostDetails
import com.vtiahotenkov.gorillastestassignment.repository.Post
import java.lang.StringBuilder

class PostPreviewItemModel(
    private val post: Post,
    private val contentCharsLimit: Int,
    private val eventListener: EventListener
) : EpoxyModelWithHolder<PostPreviewItemModel.Holder>() {

    init {
        id(post.id)
    }

    override fun bind(view: Holder) {
        super.bind(view)
        with(view.binding) {
            textPostTitle.text = post.id + ") " + post.title
            textPostContent.text = with(StringBuilder(post.content)) {
                setLength(contentCharsLimit)
                append("\u2026")
                toString()
            }
            root.setOnClickListener {
                eventListener.onEvent(ShowPostDetails(post))
            }
        }
    }

    override fun unbind(holder: Holder) {
        super.unbind(holder)
        holder.binding.root.setOnClickListener(null)
    }

    override fun getDefaultLayout(): Int = R.layout.posts_item_content_item

    override fun createNewHolder(parent: ViewParent): Holder = Holder()

    class Holder : EpoxyHolder() {
        lateinit var binding: PostsItemContentItemBinding
        override fun bindView(itemView: View) {
            binding = PostsItemContentItemBinding.bind(itemView)
        }
    }
}