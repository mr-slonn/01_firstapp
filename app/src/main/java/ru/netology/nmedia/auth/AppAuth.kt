package ru.netology.nmedia.auth

import android.content.Context

import androidx.core.content.edit
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.api.ApiService
//import ru.netology.nmedia.di.DependencyContainer
import ru.netology.nmedia.dto.PushToken
import ru.netology.nmedia.dto.Token
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    @ApplicationContext
    private val context: Context
) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val idKey = "id"
    private val tokenKey = "token"

    //private val _authStateFlow: MutableStateFlow<AuthState>
   private val _data = MutableStateFlow(Token())


    init {
        val token = prefs.getString(tokenKey, null)
        val id = prefs.getLong(idKey, 0L)

        if (!prefs.contains(idKey) || !prefs.contains(tokenKey)) {
            prefs.edit {
                clear()
            }
        } else {
            _data.value = Token(id = id, token = token)
        }



//        val id = prefs.getLong(idKey, 0)
//        val token = prefs.getString(tokenKey, null)
//
//        if (id == 0L || token == null) {
//           _data = MutableStateFlow(Token())
//            _authStateFlow = MutableStateFlow(AuthState())
//            with(prefs.edit()) {
//                clear()
//                apply()
//            }
//        } else {
//           _data = MutableStateFlow(Token(id, token))
//         //   _authStateFlow = MutableStateFlow(AuthState(id, token))
//        }
        sendPushToken()
    }

    val data : StateFlow<Token> = _data.asStateFlow()
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
       _data.value = Token(id = id, token = token)
       // _authStateFlow.value = AuthState(id, token)
        sendPushToken()
    }

    // @Synchronized
//    fun removeAuth() {
////        _authStateFlow.value = AuthState()
////        with(prefs.edit()) {
////            clear()
////            commit()
////        }
//        prefs.edit {
//            clear()
//        }
//        _data.value = Token()
//       // _authStateFlow.value = AuthState()
//        sendPushToken()
//    }

    @Synchronized
    fun removeAuth() {
        _data.value = Token()
        with(prefs.edit()) {
            clear()
            apply()
        }
        sendPushToken()
    }


    //    companion object {
//        private var INSTANCE: AppAuth? = null
//        fun getInstance(): AppAuth = requireNotNull(INSTANCE){
//            "AppAuth is not initialized, you must call AppAuth.initializeApp(Context context) first."
//        }
//        fun init(context: Context)
//        {
//            INSTANCE = AppAuth(context)
//        }
//
////        @Volatile
////        private var instance: AppAuth? = null
////
////        fun getInstance(): AppAuth = synchronized(this) {
////            instance ?: throw IllegalStateException(
////                "AppAuth is not initialized, you must call AppAuth.initializeApp(Context context) first."
////            )
////        }
////
////        fun initApp(context: Context): AppAuth = instance ?: synchronized(this) {
////            instance ?: buildAuth(context).also { instance = it }
////        }
////
////        private fun buildAuth(context: Context): AppAuth = AppAuth(context)
//    }
    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface AppAuthEntryPoint {
        fun apiService(): ApiService
    }

    fun sendPushToken(token: String? = null) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val pushToken = PushToken(token ?: FirebaseMessaging.getInstance().token.await())
                val entryPoint =
                    EntryPointAccessors.fromApplication(context, AppAuthEntryPoint::class.java)
                entryPoint.apiService().sendPushToken(pushToken)
                //DependencyContainer.getInstance().apiService.sendPushToken(pushToken)
//               код из лекции
                //                val pushToken = PushToken(token ?: Firebase.messaging.token.await())
//                PostsApi.retrofitService.sendPushToken(pushToken)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

//data class AuthState(val id: Long = 0, val token: String? = null)
