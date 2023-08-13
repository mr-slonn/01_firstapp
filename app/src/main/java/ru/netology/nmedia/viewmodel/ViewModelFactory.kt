//package ru.netology.nmedia.viewmodel
//
//import android.view.View
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import ru.netology.nmedia.auth.AppAuth
//import ru.netology.nmedia.dto.Post
//import ru.netology.nmedia.repository.AuthRepository
//import ru.netology.nmedia.repository.PostRepository
//
//class ViewModelFactory(
//    private val repository: PostRepository,
//    private val appAuth: AppAuth,
//    private val authRepository: AuthRepository,
//) : ViewModelProvider.Factory{
//    @Suppress("UNCHECKED_CAST")
//    override fun <T : ViewModel> create(modelClass: Class<T>): T =
//        when {
//            modelClass.isAssignableFrom(PostViewModel::class.java) -> {
//                PostViewModel(repository,appAuth) as T
//            }
//            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
//                AuthViewModel(authRepository, appAuth) as T
//            }
//            else -> error("Unknown class: $modelClass")
//        }
//}
