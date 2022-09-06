@file:Suppress("UNCHECKED_CAST")

package com.raymuko.util

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.raymuko.data.UserRepository
import com.raymuko.user.UserDetailViewModel
import com.raymuko.user.UserListViewModel

val userRepository = UserRepository()

@Composable
fun userListViewModel() = viewModel(UserListViewModel::class.java, factory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserListViewModel(userRepository) as T
    }
})

@Composable
fun userDetailViewModel(userId: Long) = viewModel(UserDetailViewModel::class.java, factory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserDetailViewModel(userId, userRepository) as T
    }
})
