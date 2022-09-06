package com.raymuko.data

import android.util.Log
import com.raymuko.models.User
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

//Remote and local data sources might be injected here as constructor parameters
const val ID_CREATE_USER = -1L
@Suppress("RedundantSuspendModifier")
class UserRepository() {
    //this might be pulled from a local data source (like room) or a remote one (like retrofit service or firebase database connection)
    private val _users = mutableMapOf<Long, User>()
    private val _usersFlow = MutableStateFlow(_users.toMap())


    val users = _usersFlow.map {
        _users.values.toList()
    }

    suspend fun addOrUpdateUser(user: User) {
        val userToCreateOrUpdate = if(user.id == ID_CREATE_USER) {
            val newId = if(_users.isEmpty()) 1 else _users.keys.maxOf { it } + 1
            user.copy(id = newId, user.name, user.bio, user.avatar)
        }
        else
            user

        if (_users.put(userToCreateOrUpdate.id, userToCreateOrUpdate) != user) {
            _usersFlow.emit(_users.toMap())
        }
    }

    suspend fun removeUser(id: Long) {
        _users.remove(id)
        _usersFlow.emit(_users.toMap())
    }

    suspend fun getUser(id: Long) = _users[id]
}