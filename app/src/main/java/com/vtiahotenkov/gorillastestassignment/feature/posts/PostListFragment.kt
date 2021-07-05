package com.vtiahotenkov.gorillastestassignment.feature.posts

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.DiffResult
import com.airbnb.epoxy.OnModelBuildFinishedListener
import com.airbnb.epoxy.SimpleEpoxyController
import com.vtiahotenkov.gorillastestassignment.PostPreviewCharsLimit
import com.vtiahotenkov.gorillastestassignment.R
import com.vtiahotenkov.gorillastestassignment.databinding.PostsFragmentBinding
import com.vtiahotenkov.gorillastestassignment.feature.posts.epoxy.ModelsMapper
import com.vtiahotenkov.gorillastestassignment.routing.Destination
import com.vtiahotenkov.gorillastestassignment.routing.Router
import dagger.hilt.android.AndroidEntryPoint
import java.util.Optional
import kotlinx.coroutines.flow.collect
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class PostListFragment : Fragment(R.layout.posts_fragment) {

    @PostPreviewCharsLimit
    @Inject
    lateinit var postPreviewCharsLimit: Provider<Int>

    private val viewModel: PostListViewModel by viewModels()
    private var binding: PostsFragmentBinding? = null

    private val controller = SimpleEpoxyController()
    private lateinit var lm: LinearLayoutManager
    private var optionalRecyclerSavedState: Optional<Parcelable> = Optional.empty()

    private val modelsMapper by lazy {
        ModelsMapper(viewModel, postPreviewCharsLimit.get())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PostsFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.contentRecycler?.run {
            lm = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            layoutManager = lm
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = controller.adapter
        }

        optionalRecyclerSavedState = Optional.ofNullable(
            savedInstanceState?.getParcelable(RECYCLER_SAVED_STATE)
        )

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.viewStateFlow.collect(::updateViewState)
        }
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.navigationFlow.collect(::onNavigationEvent)
        }
    }

    private fun onNavigationEvent(destination: Destination) {
        (requireActivity() as? Router)?.routeTo(destination)
    }

    private fun updateViewState(state: PostListState) {
        with(controller) {
            if (optionalRecyclerSavedState.isPresent) {
                val listener = object : OnModelBuildFinishedListener {
                    override fun onModelBuildFinished(result: DiffResult) {
                        removeModelBuildListener(this)
                        lm.onRestoreInstanceState(optionalRecyclerSavedState.get())
                        optionalRecyclerSavedState = Optional.empty()
                    }

                }
                addModelBuildListener(listener)
            }

            setModels(modelsMapper.map(state))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(RECYCLER_SAVED_STATE, lm.onSaveInstanceState())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        private const val RECYCLER_SAVED_STATE = "RECYCLER_SAVED_STATE"
    }
}