package com.raymuko.user

import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.raymuko.R
import com.raymuko.models.User

@Composable
fun UserListScreen(usersState: State<UserListViewModel.GetUsersListState>,
                   onUserClick: (id: Long) -> Unit,
                   onAddUsersClick:() -> Unit,
                   onRemoveUser: (id: Long) -> Unit
) {
    val state = usersState.value
    Box(modifier = Modifier.fillMaxSize()) {
        when (state) {
            UserListViewModel.GetUsersListState.Loading -> {} //maybe add a loading indicator once we retrieve data from backend?
            is UserListViewModel.GetUsersListState.Success -> UsersList(
                users = state.usersList,
                onUserClick = onUserClick,
                onRemoveUser = onRemoveUser
            )
            UserListViewModel.GetUsersListState.Empty -> EmptyScreen()
        }

        FloatingActionButton(modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            onClick = onAddUsersClick
        ){
            Text("+", fontSize = 24.sp)
        }
    }
}

@Composable
fun EmptyScreen(){
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        val emptyImage = AppCompatResources.getDrawable(
            context,
            R.drawable.empty_list
        )!!.toBitmap().asImageBitmap()

        Image(modifier = Modifier.widthIn(max = 253.dp).fillMaxWidth().clip(CircleShape),
            bitmap = emptyImage, contentScale = ContentScale.Crop,
            contentDescription = ""
        )

        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "No users registered yet :/")
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UsersList(users: List<User>, onUserClick: (id: Long) -> Unit, onRemoveUser: (id: Long) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(items = users, key = { it.id }) { user ->
            Row(modifier = Modifier
                .wrapContentSize()
                .animateItemPlacement()
                .clickable { onUserClick(user.id) }
            ) {
                UserItem(user = user, onRemoveUser = onRemoveUser
                )
            }
            Divider()
        }
    }
}


@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
private fun LazyItemScope.UserItem(user: User, onRemoveUser: (id: Long) -> Unit) {
    val context = LocalContext.current
    val dismissState = rememberDismissState(confirmStateChange = {
        val shouldDismiss = it == DismissValue.DismissedToEnd || it == DismissValue.DismissedToStart
        if(shouldDismiss)
            onRemoveUser(user.id)
        shouldDismiss
    })
    SwipeToDismiss(state = dismissState,
        background = {
            val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
            Box(Modifier.fillMaxSize().background(Color.Red).padding(horizontal = 20.dp)) {
                val alignment = when (direction) {
                    DismissDirection.StartToEnd -> Alignment.CenterStart
                    DismissDirection.EndToStart -> Alignment.CenterEnd
                }
                Icon(Icons.Default.Delete, contentDescription = "", modifier = Modifier.align(alignment))
            }
        }) {
        Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colors.surface).padding(16.dp),
            verticalAlignment = Alignment.CenterVertically) {
            val avatarBitmap = (
                    user.avatar?: AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_baseline_account_circle_24
                    )!!.toBitmap(50.dp.value.toInt(), 50.dp.value.toInt())
                    ).asImageBitmap()

            Image(modifier = Modifier.size(50.dp).clip(CircleShape),
                contentScale = ContentScale.Crop,
                bitmap = avatarBitmap,
                contentDescription = ""
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(user.name)
        }
    }
}