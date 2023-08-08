package ru.netology.nmedia.activity


import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentLoginBinding
import ru.netology.nmedia.model.AuthModel
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.model.RegisterModel
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.AuthViewModel


class LogInFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }


    private val viewModel: AuthViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLoginBinding.inflate(
            inflater,
            container,
            false
        )

        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(it.data),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    Activity.RESULT_OK -> {
                        val uri = it.data?.data ?: return@registerForActivityResult
                        viewModel.changePhoto(PhotoModel(uri, uri.toFile()))
                    }
                }
            }

        val actionBar = (activity as AppCompatActivity).supportActionBar

        requireActivity().onBackPressedDispatcher.addCallback {

            findNavController().navigateUp()
            actionBar?.setDisplayHomeAsUpEnabled(false)
            actionBar?.setDisplayShowHomeEnabled(false)
        }
        actionBar?.setDisplayShowHomeEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        var typeVisible = arguments?.textArg == "signin"

        binding.avatarContainer.isVisible = !typeVisible
        binding.signIn.isVisible = typeVisible
        binding.name.isVisible = !typeVisible
        binding.signUp.isVisible = !typeVisible
        binding.toSignIn.isVisible = !typeVisible
        binding.toSignUp.isVisible = typeVisible
        binding.retryPass.isVisible = !typeVisible

        binding.signIn.setOnClickListener {
            AndroidUtils.hideKeyboard(requireView())
            val login = binding.logIn.text.toString().trim()
            val password = binding.pass.text.toString().trim()



            if (login.isNotBlank() && password.isNotBlank()) {
                viewModel.updateUser(AuthModel(login, password))

            } else {
                Snackbar.make(binding.root, R.string.empty_input, Snackbar.LENGTH_LONG)
                    .setAction(R.string.close) { }
                    .show()
            }
        }
        binding.signUp.setOnClickListener {
            AndroidUtils.hideKeyboard(requireView())
            val login = binding.logIn.text.toString().trim()
            val password = binding.pass.text.toString().trim()
            val retryPassword = binding.retryPass.text.toString().trim()
            val name = binding.name.text.toString().trim()

            if (login.isNotBlank() && password.isNotBlank() && name.isNotBlank() && retryPassword.isNotBlank()) {

                if (password == retryPassword) {
                    viewModel.register(RegisterModel(login, password, name))
                } else {
                    Snackbar.make(binding.root, R.string.password_not_retry, Snackbar.LENGTH_LONG)
                        .setAction(R.string.close) { }
                        .show()
                    binding.retryPass.requestFocus()
                }
            } else {
                Snackbar.make(binding.root, R.string.empty_input, Snackbar.LENGTH_LONG)
                    .setAction(R.string.close) { }
                    .show()

            }
        }

        binding.toSignUp.setOnClickListener {
            AndroidUtils.hideKeyboard(requireView())
            typeVisible = false
            binding.avatarContainer.isVisible = !typeVisible
            binding.name.isVisible = !typeVisible
            binding.signIn.isVisible = typeVisible
            binding.signUp.isVisible = !typeVisible
            binding.toSignIn.isVisible = !typeVisible
            binding.toSignUp.isVisible = typeVisible
            binding.retryPass.isVisible = !typeVisible
        }

        binding.toSignIn.setOnClickListener {
            AndroidUtils.hideKeyboard(requireView())
            typeVisible = true
            binding.avatarContainer.isVisible = !typeVisible
            binding.name.isVisible = !typeVisible
            binding.signIn.isVisible = typeVisible
            binding.signUp.isVisible = !typeVisible
            binding.toSignIn.isVisible = !typeVisible
            binding.toSignUp.isVisible = typeVisible
            binding.retryPass.isVisible = !typeVisible

        }

        binding.pickPhoto.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .crop()
                .compress(2048)
                .provider(ImageProvider.GALLERY)
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg",
                    )
                )
                .createIntent(pickPhotoLauncher::launch)
        }

        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .cameraOnly()
                .crop()
                .compress(2048)
                .provider(ImageProvider.CAMERA)
                .createIntent(pickPhotoLauncher::launch)
        }

//        binding.removePhoto.setOnClickListener {
//            viewModel.changePhoto(null, null)
//        }

        binding.removePhoto.setOnClickListener {
            viewModel.clearPhoto()
        }

        val authViewModel: AuthViewModel by viewModels()
        authViewModel.data.observe(viewLifecycleOwner) { token ->
            val authorized = token.token != null
            if (authorized) {
                findNavController().navigateUp()
                actionBar?.setDisplayHomeAsUpEnabled(false)
                actionBar?.setDisplayShowHomeEnabled(false)
            }

        }

        viewModel.photo.observe(viewLifecycleOwner) { photo ->

            if (photo == null) {
                binding.photo.setImageDrawable(context?.let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.baseline_tag_faces_24
                    )
                })
                binding.removePhoto.isGone = true
                return@observe
            } else {
                binding.removePhoto.isVisible = true
                binding.photo.setImageURI(photo.uri)
            }
        }

        viewModel.authState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.transparentBackground.isVisible = state.loading


            if (state.smallError || state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.close) {

                    }
                    .show()
            }
        }

        return binding.root
    }


}
