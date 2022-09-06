package com.raymuko.user

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.raymuko.R
import com.raymuko.util.imageFromUri


@Composable
fun UserDetailScreen(usersState: State<UserDetailViewModel.GetUserState>,
                     onSaveUserClick: () -> Unit){
    val user = usersState.value
    when(user) {
        UserDetailViewModel.GetUserState.Loading -> {} //TODO: Maybe show some loading indication?
        UserDetailViewModel.GetUserState.NotFound -> {} //TODO: show error
        is UserDetailViewModel.GetUserState.NewUser ->
            UserDetail(userModel = user.userModel, onSaveUserClick = onSaveUserClick)
        is UserDetailViewModel.GetUserState.Success ->
            UserDetail(userModel = user.userModel, onSaveUserClick = onSaveUserClick)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun UserDetail(userModel: UserDetailViewModel.UserModel,
                       onSaveUserClick: () -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()) { imageUri: Uri? ->
        imageUri?.let {
            userModel.avatar = imageFromUri(context, it)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var nameHasError by remember { mutableStateOf(false) }
        Box(modifier = Modifier.wrapContentSize()
            .clickable {
                launcher.launch("image/*")
            }
        ) {
            val avatarBitmap = (userModel.avatar?: AppCompatResources.getDrawable(
                context,
                R.drawable.ic_baseline_account_circle_24
            )!!.toBitmap(100.dp.value.toInt(), 100.dp.value.toInt())).asImageBitmap()

            val plusBitmap = AppCompatResources.getDrawable(
                context,
                R.drawable.ic_baseline_add_circle_24
            )!!.toBitmap(25.dp.value.toInt(), 25.dp.value.toInt()).asImageBitmap()

            Image(modifier = Modifier.size(100.dp).clip(CircleShape),
                bitmap = avatarBitmap, contentScale = ContentScale.Crop,
                contentDescription = ""
            )
            Box(modifier = Modifier.size(50.dp)
                .padding(end = 8.dp, bottom = 8.dp)
                .align(Alignment.BottomEnd)
            ) {
                Image(modifier = Modifier
                    .size(25.dp)
                    .background(Color.White, shape = CircleShape)
                    .border(2.dp, Color.White, CircleShape)
                    .align(Alignment.BottomEnd),
                    bitmap = plusBitmap,
                    contentDescription = "")
            }

        }

        val focusManager = LocalFocusManager.current
        OutlinedTextField(isError = nameHasError, modifier = Modifier.fillMaxWidth(), value = userModel.name,
            onValueChange = {
                nameHasError = false
                userModel.name = it
            },
            label = {
                Text("Name")
            }, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ))

        OutlinedTextField(modifier = Modifier.height(200.dp).fillMaxWidth(),
            value = userModel.bio,
            onValueChange = { userModel.bio = it },
            label = { Text("Bio") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
            }
        ))

        fun isInputCorrect(): Boolean{
            if(userModel.name.isEmpty()) {
                Toast.makeText(context, "Name is required", Toast.LENGTH_SHORT).show()
                nameHasError = true
                return false
            }
            return true
        }

        Button(
            onClick = {
                keyboardController?.hide()
                if(!isInputCorrect())
                    return@Button
                onSaveUserClick()
            }
        ) {
            Text("Save")
        }
    }
}