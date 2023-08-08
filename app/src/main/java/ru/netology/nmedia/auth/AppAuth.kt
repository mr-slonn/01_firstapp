package ru.netology.nmedia.auth

import android.content.Context

import androidx.core.content.edit
import kotlinx.coroutines.flow.*
import ru.netology.nmedia.dto.Token

class AppAuth private constructor(context: Context) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val idKey = "id"
    private val tokenKey = "token"

    //private val _authStateFlow: MutableStateFlow<AuthState>
    private val _data = MutableStateFlow (Token())
    val data = _data.asStateFlow()

    init {
        val token = prefs.getString(tokenKey, null)
        val id = prefs.getLong(idKey, 0L)

        if (!prefs.contains(idKey) || !prefs.contains(tokenKey)){
            prefs.edit{
                clear()
            }
        } else
        {
            _data.value = Token(id = id, token= token)
        }


//        if (id == 0L || token == null) {
//
//            _authStateFlow = MutableStateFlow(AuthState())
//            with(prefs.edit()) {
//               clear()
//               apply()
//           }
//        } else {
//            _authStateFlow = MutableStateFlow(AuthState(id, token))
//        }
    }

    //val authStateFlow: StateFlow<AuthState> = _authStateFlow.asStateFlow()

   // @Synchronized
    fun setAuth(id: Long, token: String) {
//        _authStateFlow.value = AuthState(id, token)
//        with(prefs.edit()) {
//            putLong(idKey, id)
//            putString(tokenKey, token)
//            apply()
//        }
        prefs.edit {
            putLong(idKey, id)
            putString(tokenKey, token)
        }
        _data.value = Token(id = id, token= token)
    }

   // @Synchronized
    fun removeAuth() {
//        _authStateFlow.value = AuthState()
//        with(prefs.edit()) {
//            clear()
//            commit()
//        }
       prefs.edit {
           clear()
       }
       _data.value = Token()
    }

    companion object {
        private var INSTANCE: AppAuth? = null
        fun getInstance(): AppAuth = requireNotNull(INSTANCE){
            "AppAuth is not initialized, you must call AppAuth.initializeApp(Context context) first."
        }
        fun init(context: Context)
        {
            INSTANCE = AppAuth(context)
        }

//        @Volatile
//        private var instance: AppAuth? = null
//
//        fun getInstance(): AppAuth = synchronized(this) {
//            instance ?: throw IllegalStateException(
//                "AppAuth is not initialized, you must call AppAuth.initializeApp(Context context) first."
//            )
//        }
//
//        fun initApp(context: Context): AppAuth = instance ?: synchronized(this) {
//            instance ?: buildAuth(context).also { instance = it }
//        }
//
//        private fun buildAuth(context: Context): AppAuth = AppAuth(context)
    }
}

//data class AuthState(val id: Long = 0, val token: String? = null)
