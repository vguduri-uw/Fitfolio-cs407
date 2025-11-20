package com.cs407.fitfolio.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs407.fitfolio.data.AppDatabase
import com.cs407.fitfolio.R
import com.cs407.fitfolio.data.User
import com.cs407.fitfolio.ui.modals.EditableField
import com.cs407.fitfolio.ui.theme.Kudryashev_Display_Sans_Regular
import com.cs407.fitfolio.ui.theme.Kudryashev_Regular
import com.cs407.fitfolio.viewModels.UserState
import com.cs407.fitfolio.viewModels.UserViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

fun createAccount(
    email: String,
    password: String,
    onSuccess: (FirebaseUser) -> Unit,
    onError: (Exception) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result?.user?.let { onSuccess(it) }
            } else {
                task.exception?.let { onError(it) }
            }
        }
}

@Composable
fun SignUpScreen (
    onNavigateToOutfitsScreen: () -> Unit,
    onNavigateToSignInScreen: () -> Unit,
    userViewModel: UserViewModel
) {

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .align(alignment = Alignment.TopCenter)
                .fillMaxSize()
                .padding(start = 12.dp, end = 12.dp)
        ) {
            // profile image, my outfits title, information icon
            SignUpScreenTopHeader()

            // sign up form - name, email, password, re-enter password
            SignUpForm(onNavigateToSignInScreen, {userState ->
                userViewModel.setUser(userState)   // <- store user in ViewModel
                onNavigateToOutfitsScreen()   })
        }

    }
}

@Composable
fun SignUpScreenTopHeader() {
    Image(
        painter = painterResource(id = R.drawable.app_logo),
        contentDescription = "App logo",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .size(160.dp)
            .clip(CircleShape),
        alignment = Alignment.Center
    )

    Spacer(modifier = Modifier.size(15.dp))

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "FitFolio", fontFamily = Kudryashev_Display_Sans_Regular, fontSize = 60.sp)
    }
}

@Composable
fun SignUpForm( onNavigateToSignInScreen: () -> Unit, signUpButtonClick: (UserState) -> Unit) {
    // User information
    // TODO: implement the way to get this from storage (these are placeholders)
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error: String? by remember { mutableStateOf(null) }
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    // todo: replace with actual error handling
    var reenteredPassword by remember { mutableStateOf("") }
    val passwordsMatch = password == reenteredPassword
    val scope = rememberCoroutineScope()

    // Track whether all fields are filled (to make sign up button available)
    val allFieldsFilled by remember {
        derivedStateOf {
            name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && reenteredPassword.isNotEmpty()
        }
    }
    val onComplete: (Boolean, Exception?, FirebaseUser?) -> Unit =
        { isSuccess, taskException, signedUser ->
            if (isSuccess && signedUser != null)
                scope.launch {
                    var user = db.userDao().getByUID(signedUser.uid)

                    if (user == null) {
                        db.userDao().insert(
                            User(
                                userUID = signedUser.uid,
                                username = name,
                            )
                        )
                        user = db.userDao().getByUID(signedUser.uid)
                    }

                    signUpButtonClick(
                        UserState(
                            id = user!!.userId,
                            name = name,
                            uid = signedUser.uid
                        )
                    )
                }
            else
                error = taskException?.message
        }

    val user = Firebase.auth.currentUser
    if (user != null)
        onComplete(true, null, user)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 48.dp, start = 24.dp, end = 24.dp)
    ) {
        // Fields for editing user information
        // TODO: add edit button and only enable then??
        EditableField(
            label = "Name",
            value = name,
            onValueChange = { name = it },
            isPassword = false,
        )

        EditableField(
            label = "Email",
            value = email,
            onValueChange = { email = it },
            isPassword = false
        )
        EditableField(
            label = "Password",
            value = password,
            onValueChange = { password = it },
            isPassword = true
        )

        EditableField(
            label = "Re-enter Password",
            value = reenteredPassword,
            onValueChange = { reenteredPassword = it },
            isPassword = true
        )
        // todo: implement actual error handling logic
        if (password.isNotEmpty() && reenteredPassword.isNotEmpty() && !passwordsMatch) {
            Text(text = "Passwords do not match", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.size(10.dp))

        // Sign up button
        Button(
            onClick = { createAccount(email, password,
                onSuccess = { user ->
                    scope.launch {
                        var dbUser = db.userDao().getByUID(user.uid)
                        if (dbUser == null) {
                            val newUser = User(userUID = user.uid, username = name)
                            db.userDao().insert(newUser)
                            dbUser = db.userDao().getByUID(user.uid)
                        }
                        signUpButtonClick(UserState(id = dbUser!!.userId, name = dbUser.username, uid = user.uid))
                    }
                },
                onError = { exception ->
                    error = exception.message
                }
            )
            },
            enabled = allFieldsFilled && passwordsMatch,
            content = {
                Text(
                    text = "Sign Up",
                    fontFamily = Kudryashev_Regular,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(5.dp)
                )
            },
        )

        // move to sign in page if account exists already
        Row {
            Text(
                text = "Already have an account?",
                fontFamily = Kudryashev_Regular,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = "Sign in",
                fontFamily = Kudryashev_Regular,
                style = TextStyle(textDecoration = TextDecoration.Underline),
                fontSize = 15.sp,
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .clickable(onClick = { onNavigateToSignInScreen() })
            )
        }
    }
}