package ru.netology.nmedia.activity

import android.app.Activity
import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.core.view.isVisible


import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider

import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R


import ru.netology.nmedia.databinding.FragmentNewPostBinding
//import ru.netology.nmedia.di.DependencyContainer
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.util.loadMediaAttachment

import ru.netology.nmedia.viewmodel.PostViewModel

//import ru.netology.nmedia.viewmodel.ViewModelFactory

@AndroidEntryPoint
class NewPostFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
        var Bundle.mediaArg: String? by StringArg
    }

//    private val viewModel: PostViewModel by viewModels(
//        ownerProducer = ::requireParentFragment
//    )
//    private val dependencyContainer = DependencyContainer.getInstance()
//
//    private val viewModel: PostViewModel by viewModels(
//        ownerProducer = ::requireParentFragment,
//        factoryProducer = {
//            ViewModelFactory(dependencyContainer.repository,dependencyContainer.appAuth, dependencyContainer.authRepository )
//        }
//    )

    //    private val viewModel: PostViewModel by viewModels(
//        ownerProducer = ::requireParentFragment)
    private val viewModel: PostViewModel by activityViewModels()


    //private var fragmentBinding: FragmentNewPostBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(
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
                        // Код из файла лекци
                        // val uri: Uri? = it.data?.data

                        //Код из видое лекци
                        val uri = it.data?.data ?: return@registerForActivityResult
//                        Код из файла лекци
//                        viewModel.changePhoto(uri, uri?.toFile())
//                        Код из видое лекци
                        viewModel.changePhoto(PhotoModel(uri, uri.toFile()))
                    }
                }
            }

        binding.progress.isVisible = false
        //binding.ok.isEnabled = true
        binding.edit.isEnabled = true

        val text = arguments?.textArg
        val media = arguments?.mediaArg

        val actionBar = (activity as AppCompatActivity).supportActionBar

        requireActivity().onBackPressedDispatcher.addCallback {
            viewModel.cancelEdit()
            findNavController().navigateUp()
            actionBar?.setDisplayHomeAsUpEnabled(false)
            actionBar?.setDisplayShowHomeEnabled(false)
            actionBar?.title = getString(R.string.nmedia)
        }

        actionBar?.setDisplayShowHomeEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title =
            if (text != null) getString(R.string.edit_post) else getString(R.string.add_post)

        binding.edit.setText(text)


        if (media != null) {
            //     attachment.loadAttachment(viewModel.getAttachmentUrl(state.post.attachment.url))
            binding.photoContainer.isVisible = true
            binding.photo.loadMediaAttachment(media)

        } else {
            binding.photoContainer.isGone = true
        }


//        binding.ok.setOnClickListener {
//            actionBar?.setDisplayHomeAsUpEnabled(false)
//            actionBar?.setDisplayShowHomeEnabled(false)
//
//            binding.progress.isVisible = true
//            binding.edit.isEnabled = false
//            binding.ok.isEnabled = false
//
//            viewModel.changeContent(binding.edit.text.toString())
//            viewModel.save()
//            AndroidUtils.hideKeyboard(requireView())
//            findNavController().navigateUp()
//            actionBar?.title = getString(R.string.nmedia)
//            actionBar?.setDisplayHomeAsUpEnabled(false)
//            actionBar?.setDisplayShowHomeEnabled(false)
//        }

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
            binding.photoContainer.isGone = true
        }



        viewModel.photo.observe(viewLifecycleOwner) { photo ->
            //if (it.uri == null) {
            if (photo == null && media == null) {
                //binding.photoContainer.visibility = View.GONE
                binding.photoContainer.isGone = true
                return@observe
            } else if (photo != null) {
                //binding.photoContainer.visibility = View.VISIBLE
                binding.photoContainer.isVisible = true
                binding.photo.setImageURI(photo.uri)
            }
        }



        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_new_post, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.save -> {
//                        fragmentBinding?.let {
////                            viewModel.changeContent(it.edit.text.toString())
////                            viewModel.save()
////                            AndroidUtils.hideKeyboard(requireView())
//
//                            actionBar?.setDisplayHomeAsUpEnabled(false)
//                            actionBar?.setDisplayShowHomeEnabled(false)
//
//                            it.progress.isVisible = true
//                            it.edit.isEnabled = false
//                            //binding.ok.isEnabled = false
//
//                            viewModel.changeContent(it.edit.text.toString())
//                            viewModel.save()
//                            AndroidUtils.hideKeyboard(requireView())
//                            findNavController().navigateUp()
//                            actionBar?.title = getString(R.string.nmedia)
//                            actionBar?.setDisplayHomeAsUpEnabled(false)
//                            actionBar?.setDisplayShowHomeEnabled(false)
//
//                        }


//                            viewModel.changeContent(it.edit.text.toString())
//                            viewModel.save()
//                            AndroidUtils.hideKeyboard(requireView())

                        actionBar?.setDisplayHomeAsUpEnabled(false)
                        actionBar?.setDisplayShowHomeEnabled(false)

                        binding.progress.isVisible = true
                        binding.edit.isEnabled = false
                        //binding.ok.isEnabled = false

                        viewModel.changeContent(binding.edit.text.toString())
                        viewModel.save()
                        AndroidUtils.hideKeyboard(requireView())
                        findNavController().navigateUp()
                        actionBar?.title = getString(R.string.nmedia)
                        actionBar?.setDisplayHomeAsUpEnabled(false)
                        actionBar?.setDisplayShowHomeEnabled(false)






                        true
                    }

                    else -> false
                }

        }, viewLifecycleOwner)

        viewModel.postNotCreated.observe(viewLifecycleOwner) {

            actionBar?.setDisplayHomeAsUpEnabled(true)
            actionBar?.setDisplayShowHomeEnabled(true)

            binding.progress.isVisible = false
            binding.edit.isEnabled = true
            //binding.ok.isEnabled = true

            Snackbar.make(
                binding.root,
                R.string.error_loading,
                Snackbar.LENGTH_LONG
            ).setAction(android.R.string.cancel)
            {

            }.show()


        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            viewModel.loadPosts()
            findNavController().navigateUp()
            actionBar?.title = getString(R.string.nmedia)
            actionBar?.setDisplayHomeAsUpEnabled(false)
            actionBar?.setDisplayShowHomeEnabled(false)
        }
        return binding.root
    }
}
