package com.vtiahotenkov.gorillastestassignment

import com.vtiahotenkov.gorillastestassignment.di.DaggerTestComponent
import com.vtiahotenkov.gorillastestassignment.feature.posts.LoadNextPage
import com.vtiahotenkov.gorillastestassignment.feature.posts.PostListState
import com.vtiahotenkov.gorillastestassignment.feature.posts.PostListViewModel
import com.vtiahotenkov.gorillastestassignment.feature.posts.RetryOnError
import com.vtiahotenkov.gorillastestassignment.feature.posts.ShowPostDetails
import com.vtiahotenkov.gorillastestassignment.feature.posts.usecase.GetPosts
import com.vtiahotenkov.gorillastestassignment.repository.Author
import com.vtiahotenkov.gorillastestassignment.repository.NextPage
import com.vtiahotenkov.gorillastestassignment.repository.Post
import com.vtiahotenkov.gorillastestassignment.routing.Destination
import com.vtiahotenkov.gorillastestassignment.util.CoroutineTestRule
import com.vtiahotenkov.gorillastestassignment.util.response
import java.net.HttpURLConnection
import java.util.concurrent.CountDownLatch
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.launch
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.Ignore
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class PostListViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @Inject
    lateinit var mockWebServer: MockWebServer

    @Inject
    lateinit var getPosts: GetPosts

    lateinit var viewModel: PostListViewModel

    @Before
    fun setup() {
        DaggerTestComponent.builder().build().inject(this)
    }

    @Test
    fun `test correct GraphQL response results in correct state flow`() {
        mockWebServer.enqueue(MockResponse().apply {
            setResponseCode(HttpURLConnection.HTTP_OK)
            setBody(response)
        })

        viewModel = PostListViewModel(getPosts)

        val updatesCount = 2

        val states = mutableListOf<PostListState>()
        runBlocking {
            viewModel.viewStateFlow
                .onEach { println("next -> $it") }
                .take(updatesCount)
                .toCollection(states)
        }

        with(states) {
            assertEquals(updatesCount, size)
            assertTrue(get(0) is PostListState.Loading)
            assertTrue(get(1) is PostListState.Content)
        }
    }

    @Test
    fun `test LoadMore event causes emission of additional state`() {
        mockWebServer.enqueue(MockResponse().apply {
            setResponseCode(HttpURLConnection.HTTP_OK)
            setBody(response)
        })
        mockWebServer.enqueue(MockResponse().apply {
            setResponseCode(HttpURLConnection.HTTP_OK)
            setBody(response)
        })

        viewModel = PostListViewModel(getPosts)

        val initialUpdatesCount = 2

        val states = mutableListOf<PostListState>()
        runBlocking {
            viewModel.viewStateFlow.take(initialUpdatesCount).toCollection(states)
        }

        viewModel.onEvent(LoadNextPage(NextPage(2, 3)))

        val countUpdatesAfterEvent = 2 // the first will be current cached/replayed value

        runBlocking {
            viewModel.viewStateFlow.take(countUpdatesAfterEvent).toCollection(states)
        }

        with(states) {
            assertEquals(initialUpdatesCount + countUpdatesAfterEvent, size)
            assertTrue(get(0) is PostListState.Loading)
            assertTrue(get(1) is PostListState.Content)
            assertTrue(get(2) is PostListState.Content) // replay
            assertTrue(get(3) is PostListState.Content)
        }
    }

    @Test
    fun `passes error state when experiences connection error`() {
        mockWebServer.enqueue(MockResponse().apply {
            setSocketPolicy(SocketPolicy.DISCONNECT_AT_START)
        })

        viewModel = PostListViewModel(getPosts)

        val updatesCount = 2

        val states = mutableListOf<PostListState>()
        runBlocking {
            viewModel.viewStateFlow.take(updatesCount).toCollection(states)
        }

        with(states) {
            assertEquals(updatesCount, size)
            assertTrue(get(0) is PostListState.Loading)
            assertTrue(get(1) is PostListState.Error)
        }
    }

    @Test
    fun `retry event causes reload`() {
        mockWebServer.enqueue(MockResponse().apply {
            setSocketPolicy(SocketPolicy.DISCONNECT_AT_START)
        })
        mockWebServer.enqueue(MockResponse().apply {
            setResponseCode(HttpURLConnection.HTTP_OK)
            setBody(response)
        })

        viewModel = PostListViewModel(getPosts)

        val initialUpdatesCount = 2

        val states = mutableListOf<PostListState>()
        runBlocking {
            viewModel.viewStateFlow.take(initialUpdatesCount).toCollection(states)
        }

        viewModel.onEvent(RetryOnError)

        val updatesCountAfterEvent = 2 // the first will be current cached/replayed value

        runBlocking {
            viewModel.viewStateFlow.take(updatesCountAfterEvent).toCollection(states)
        }

        with(states) {
            assertEquals(initialUpdatesCount + updatesCountAfterEvent, size)
            assertTrue(get(0) is PostListState.Loading)
            assertTrue(get(1) is PostListState.Error)
            assertTrue(get(2) is PostListState.Error) // replay
            assertTrue(get(3) is PostListState.Content)
        }
    }

    @Test
    fun `ShowPostDetails event causes destination emission`() {
        mockWebServer.enqueue(MockResponse().apply {
            setResponseCode(HttpURLConnection.HTTP_OK)
            setBody(response)
        })

        val post = Post(
            id = "id",
            title = "title",
            content = "content",
            author = Author("name", "username")
        )

        viewModel = PostListViewModel(getPosts)

        val updatesCount = 1
        val destinations = mutableListOf<Destination>()

        runBlocking {
            launch {
                viewModel.navigationFlow.take(updatesCount).toCollection(destinations)
            }

            delay(100)

            viewModel.onEvent(ShowPostDetails(post))
        }


        with(destinations) {
            assertEquals(updatesCount, size)
            assertTrue(destinations[0] is Destination.PostDetails)
            assertEquals(post, (destinations[0] as? Destination.PostDetails)?.post)
        }
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }
}