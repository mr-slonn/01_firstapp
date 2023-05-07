package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible


import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R


import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel


class NewPostFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )


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

        binding.progress.isVisible = false
        binding.ok.isEnabled = true
        binding.edit.isEnabled = true

        val text = arguments?.textArg

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

        binding.ok.setOnClickListener {
            actionBar?.setDisplayHomeAsUpEnabled(false)
            actionBar?.setDisplayShowHomeEnabled(false)

            binding.progress.isVisible = true
            binding.edit.isEnabled = false
            binding.ok.isEnabled = false

            viewModel.changeContent(binding.edit.text.toString())
            viewModel.save()
            AndroidUtils.hideKeyboard(requireView())
//            findNavController().navigateUp()
//            actionBar?.title = getString(R.string.nmedia)
//            actionBar?.setDisplayHomeAsUpEnabled(false)
//            actionBar?.setDisplayShowHomeEnabled(false)
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
