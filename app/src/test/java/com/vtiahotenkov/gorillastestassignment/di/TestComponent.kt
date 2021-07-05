package com.vtiahotenkov.gorillastestassignment.di

import com.vtiahotenkov.gorillastestassignment.PostListViewModelTest
import dagger.Component

@Component(
    modules = [
        TestApiModule::class
    ]
)
interface TestComponent {
    fun inject(test: PostListViewModelTest)
}

