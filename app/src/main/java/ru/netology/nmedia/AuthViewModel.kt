package ru.netology.nmedia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import ru.netology.nmedia.auth.AppAuth

class AuthViewModel : ViewModel() {
    val state = AppAuth.getInstance().state
        .asLiveData()
    val authorized: Boolean
        get() = state.value != null
}