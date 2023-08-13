//package ru.netology.nmedia.di
//
//import android.content.Context
//import androidx.room.Room
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import okhttp3.logging.HttpLoggingInterceptor
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import retrofit2.create
//import ru.netology.nmedia.BuildConfig
//import ru.netology.nmedia.api.ApiService
//import ru.netology.nmedia.auth.AppAuth
//import ru.netology.nmedia.db.AppDb
//import ru.netology.nmedia.repository.AuthRepository
//import ru.netology.nmedia.repository.AuthRepositoryImpl
//import ru.netology.nmedia.repository.PostRepository
//import ru.netology.nmedia.repository.PostRepositoryImpl
//
//class DependencyContainer (private val context: Context) {
//
//    companion object {
//        private const val BASE_URL = "${BuildConfig.BASE_URL}/api${BuildConfig.SPEED}/"
//
//        @Volatile
//        private var instance: DependencyContainer? = null
//
//        fun initApp(context: Context)
//        {
//            instance = DependencyContainer(context)
//        }
//        fun getInstance(): DependencyContainer {
//            return instance!!
//        }
//
//    }
//
//    private val logging = HttpLoggingInterceptor().apply {
//        if (BuildConfig.DEBUG) {
//            level = HttpLoggingInterceptor.Level.BODY
//        }
//    }
//
//    val appAuth = AppAuth(context)
//
//    private val okhttp = OkHttpClient.Builder()
//        .addInterceptor(logging)
//        .addInterceptor { chain ->
////        AppAuth.getInstance().authStateFlow.value.token?.let { token ->
////            val newRequest = chain.request().newBuilder()
////                .addHeader("Authorization", token)
////                .build()
////            return@addInterceptor chain.proceed(newRequest)
////        }
//            val request: Request = appAuth.data.value.token?.let {
//                chain.request().newBuilder().addHeader("Authorization", it).build()
//            } ?: run {
//                chain.request()
//            }
//            // chain.proceed(chain.request())
//            chain.proceed(request)
//        }
//        .build()
//
//    private val retrofit = Retrofit.Builder()
//        .addConverterFactory(GsonConverterFactory.create())
//        .baseUrl(BASE_URL)
//        .client(okhttp)
//        .build()
//
//    private val appBd = Room.databaseBuilder(context, AppDb::class.java, "app.db")
//        .fallbackToDestructiveMigration()
//        .build()
//
//    val apiService = retrofit.create<ApiService>()
//
//    private val postDao = appBd.postDao()
//
//    val repository: PostRepository = PostRepositoryImpl(postDao, apiService)
//
//    val authRepository: AuthRepository = AuthRepositoryImpl(apiService)
//
//
//}
//
