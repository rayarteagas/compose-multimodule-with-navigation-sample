package com.raymuko.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raymuko.data.UserRepository
import com.raymuko.models.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class UserListViewModel(
    private val userRepository: UserRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): ViewModel(){

    sealed class GetUsersListState {
        object Loading : GetUsersListState()
        object Empty : GetUsersListState()
        class Success(val usersList: List<User> = listOf()) : GetUsersListState()
    }

    val users = flow {
        emit(GetUsersListState.Loading)
        emitAll(userRepository.users.map {
            if(it.isEmpty()) GetUsersListState.Empty else GetUsersListState.Success(it)
        })
    }

    fun removeUser(userId: Long) = viewModelScope.launch(dispatcher){
        userRepository.removeUser(userId)
    }
}