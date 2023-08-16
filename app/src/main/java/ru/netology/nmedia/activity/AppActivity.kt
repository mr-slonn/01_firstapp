package ru.netology.nmedia.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build

import android.view.MenuItem
import android.widget.Toast
//import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

import androidx.navigation.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import android.Manifest

import android.os.Bundle

import androidx.activity.viewModels


import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg

import ru.netology.nmedia.viewmodel.AuthViewModel
//import ru.netology.nmedia.di.DependencyContainer
//import ru.netology.nmedia.viewmodel.AuthViewModel
import javax.inject.Inject


//import ru.netology.nmedia.viewmodel.ViewModelFactory

@AndroidEntryPoint
class AppActivity : AppCompatActivity(R.layout.activity_app) {

//    private  val dependencyContainer = DependencyContainer.getInstance()
//    private val viewModel: AuthViewModel by viewModels(
//        factoryProducer = {
//            ViewModelFactory(dependencyContainer.repository,dependencyContainer.appAuth, dependencyContainer.authRepository)
//        }
//    )

    //    @Inject
//    lateinit var appAuth: AppAuth
    private val viewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var googleApiAvailability: GoogleApiAvailability


    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging


    // private val viewModel: AuthViewModel by viewModels()


    private fun requestNotificationsPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }

        val permission = Manifest.permission.POST_NOTIFICATIONS

        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            return
        }

        requestPermissions(arrayOf(permission), 1)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestNotificationsPermission()

        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }

            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text?.isNotBlank() != true) {
                return@let
            }
            intent.removeExtra(Intent.EXTRA_TEXT)
            findNavController(R.id.nav_host_fragment).navigate(
                R.id.action_feedFragment_to_newPostFragment,
                Bundle().apply {
                    textArg = text
                }
            )
        }

        firebaseMessaging.token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                println("some stuff happened: ${task.exception}")
                return@addOnCompleteListener
            }

            val token = task.result
            println(token)
        }

        viewModel.data.observe(this) {
            invalidateOptionsMenu()
        }

        checkGoogleApiAvailability()

//        addMenuProvider(object : MenuProvider {
//            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//                menuInflater.inflate(R.menu.menu_main, menu)
//
//                menu.let {
//                    it.setGroupVisible(R.id.unauthenticated, !viewModel.authorized)
//                    it.setGroupVisible(R.id.authenticated, viewModel.authorized)
//                }
//            }
//
//            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
//                when (menuItem.itemId) {
//                    R.id.signin -> {
//                        findNavController().navigate(
//                                    R.id.action_feedFragment_to_logInFragment,
//                                    Bundle().apply {
//                                        textArg = "signin"
//                                    })
//                                true
//                        true
//                    }
//                    R.id.signup -> {
//                        findNavController().navigate(
//                                    R.id.action_feedFragment_to_logInFragment,
//                                    Bundle().apply {
//                                        textArg = "signup"
//                                    })
//                                true
//                    }
//                    R.id.signout -> {
//                        showDialog(false)
//                                //AppAuth.getInstance().removeAuth()
//                                true
//                    }
//                    else -> false
//                }
//
//        })

    }


    private fun checkGoogleApiAvailability() {
        with(googleApiAvailability) {
            val code = isGooglePlayServicesAvailable(this@AppActivity)
            if (code == ConnectionResult.SUCCESS) {
                return@with
            }
            if (isUserResolvableError(code)) {
                getErrorDialog(this@AppActivity, code, 9000)?.show()
                return
            }
            Toast.makeText(this@AppActivity, "Google Api Unavailable", Toast.LENGTH_LONG).show()
        }
//        firebaseMessaging.token.addOnSuccessListener {
//           println(it)
//        }
    }
}
