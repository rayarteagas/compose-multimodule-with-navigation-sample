package com.raymuko

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.raymuko.data.ID_CREATE_USER
import com.raymuko.util.userDetailViewModel
import com.raymuko.util.userListViewModel
import com.raymuko.ui.theme.RaymukoTheme
import com.raymuko.user.*


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RaymukoTheme {
                MainScreen()
            }
        }
    }

    @Composable
    fun MainScreen() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "userList") {

            composable("userList") {
                val model = userListViewModel()
                val usersListState = model.users.collectAsState(UserListViewModel.GetUsersListState.Empty)
                UserListScreen(
                    usersState  = usersListState,
                    onUserClick = { id->
                        navController.navigate("userDetail?userId=$id")
                    },
                    onAddUsersClick = {
                        navController.navigate("userDetail?userId=$ID_CREATE_USER")
                    },
                    onRemoveUser = {
                        model.removeUser(it)
                    }
                )
            }

            composable("userDetail?userId={userId}",
                arguments = listOf(navArgument("userId") { type = NavType.LongType })) {
                val userId = it.arguments?.getLong("userId")?: ID_CREATE_USER
                val model = userDetailViewModel(userId)
                val userState = model.userStateFlow.collectAsState(UserDetailViewModel.GetUserState.Loading)
                UserDetailScreen(userState, onSaveUserClick = {
                    model.save()
                    navController.popBackStack()
                })
            }
        }
    }
}


