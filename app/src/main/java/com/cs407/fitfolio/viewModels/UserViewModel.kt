package com.cs407.fitfolio.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs407.fitfolio.data.FitfolioDatabase
import com.cs407.fitfolio.data.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserState(
    val id: Int = 0,
    val name: String = "",
    val uid: String = "",
    val email: String = "",
    val isLoggedOut: Boolean = false
)

class UserViewModel : ViewModel() {
    private val _userState = MutableStateFlow(UserState())
    private val auth: FirebaseAuth = Firebase.auth
    val userState = _userState.asStateFlow()

    init {
        auth.addAuthStateListener { auth ->
            val user = auth.currentUser
            if (user == null) {
                setUser(UserState())
            }
        }
    }

    fun setUser(state: UserState) {
        _userState.update {
            state
        }
    }

    // Logs the user into the application
    fun loginUser(db: FitfolioDatabase) {
        val firebaseUser = auth.currentUser ?: return

        // Find the user in the database
        viewModelScope.launch {
            val localUser = db.userDao().getByUID(firebaseUser.uid)

            if (localUser != null) {
                // Set user state if user exists in the database
                setUser(
                    UserState(
                        id = localUser.userId,
                        name = localUser.username,
                        uid = localUser.userUID,
                        email = localUser.email
                    )
                )
            } else {
                // Insert user and set user state if user does not exist in the database
                val newId = db.userDao().insert(
                    User(
                        userUID = firebaseUser.uid,
                        username = firebaseUser.displayName ?: "",
                        email = firebaseUser.email ?: ""
                    )
                )
                setUser(
                    UserState(
                        id = newId.toInt(),
                        name = firebaseUser.displayName ?: "",
                        uid = firebaseUser.uid,
                        email = firebaseUser.email ?: ""
                    )
                )
            }
        }
    }

    // Logs the user out of the application
    fun logoutUser() {
        auth.signOut()
        _userState.value = UserState(isLoggedOut = true)
    }
}