package com.raymuko.user

import android.graphics.Bitmap
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raymuko.data.ID_CREATE_USER
import com.raymuko.data.UserRepository
import com.raymuko.models.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class UserDetailViewModel(
    private val userId: Long,
    private val userRepository: UserRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
): ViewModel(){

    sealed class GetUserState {
        object Loading : GetUserState()
        class Success(val userModel: UserModel) : GetUserState()
        object NotFound : GetUserState()
        class NewUser(val userModel: UserModel) : GetUserState()
    }

    class UserModel {
        var name by mutableStateOf("")
        var bio by mutableStateOf("")
        var avatar by mutableStateOf<Bitmap?>(null)
    }

    private val userModel = UserModel()

    val userStateFlow = flow {
        emit(GetUserState.Loading)
        if(userId == ID_CREATE_USER){
            emit(GetUserState.NewUser(userModel))
        }
        else{
            // A userId was passed to this viewmodel, so we need to fetch the user
            val result = userRepository.getUser(userId)
            if(result == null)
                emit(GetUserState.NotFound)
            else
                emit(GetUserState.Success(userModel.apply {
                    name = result.name
                    bio = result.bio
                    avatar = result.avatar
                }))
        }

    }

    fun save() = viewModelScope.launch(dispatcher){
        userRepository.addOrUpdateUser(
            User(
                id = userId,
                name = userModel.name,
                bio = userModel.bio,
                avatar = userModel.avatar
            )
        )
    }
}