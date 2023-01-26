package ru.netology.nmedia.auth

import android.content.Context
import androidx.core.content.edit
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.di.DependencyContainer
import ru.netology.nmedia.dto.PushToken

data class AuthPair(val id: Long, val token: String)

class AppAuth (context: Context) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val tokenKey = "TOKEN_KEY"
    private val idKey = "ID_KEY"
    private val _state: MutableStateFlow<AuthState?>

    init {
        val token = prefs.getString(tokenKey, null)
        val id = prefs.getLong(idKey, 0L)

        if (token == null || !prefs.contains(idKey)) {
            prefs.edit { clear() }
            _state = MutableStateFlow(null)
        } else {
            _state = MutableStateFlow(AuthState(id, token))
        }
        sendPushToken()
    }

    val state = _state.asStateFlow()

    @Synchronized
    fun setAuth(id: Long, token: String) {
        prefs.edit {
            putLong(idKey, id)
            putString(tokenKey, token)
        }
        _state.value = AuthState(id, token)
        sendPushToken()
    }

    @Synchronized
    fun removeAuth() {
        prefs.edit { clear() }
        _state.value = null
        sendPushToken()
    }

    fun sendPushToken(token: String? = null) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                DependencyContainer.getInstance().apiService.sendPushToken(
                    PushToken(
                        token ?: FirebaseMessaging.getInstance().token.await()
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

}