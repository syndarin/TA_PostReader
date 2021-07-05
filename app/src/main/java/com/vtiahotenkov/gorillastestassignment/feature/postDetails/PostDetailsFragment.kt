package com.vtiahotenkov.gorillastestassignment.feature.postDetails

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.vtiahotenkov.gorillastestassignment.R
import com.vtiahotenkov.gorillastestassignment.databinding.PostDetailsFragmentBinding
import com.vtiahotenkov.gorillastestassignment.repository.Post
import kotlinx.coroutines.flow.collect

class PostDetailsFragment : Fragment(R.layout.post_details_fragment) {

    private val viewModel by viewModels<PostDetailsViewModel> {
        val post = (requireArguments().getParcelable(EXTRA_POST) as? Post)
        PostDetailsViewModel.Factory(post!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = PostDetailsFragmentBinding.bind(view)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.viewStateFlow.collect {
                val post = it.post
                with(binding) {
                    textTitle.text = post.title
                    textContent.text = post.content
                    textAuthor.text = getString(
                        R.string.tpl_author,
                        post.author?.name,
                        post.author?.username
                    )
                }
            }
        }
    }

    companion object {
        private const val EXTRA_POST = "EXTRA_POST"

        fun newInstance(post: Post) = PostDetailsFragment().also {
            it.arguments = Bundle().apply {
                putParcelable(EXTRA_POST, post)
            }
        }
    }
}