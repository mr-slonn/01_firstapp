package ru.netology.nmedia.activity


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.mediaArg
import ru.netology.nmedia.databinding.FragmentPhotoBinding
import ru.netology.nmedia.util.loadMediaAttachment


class PhotoFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPhotoBinding.inflate(
            inflater,
            container,
            false
        )

        val actionBar = (activity as AppCompatActivity).supportActionBar
        requireActivity().onBackPressedDispatcher.addCallback {
            actionBar?.setDisplayHomeAsUpEnabled(false)
            actionBar?.setDisplayShowHomeEnabled(false)
            actionBar?.title = getString(R.string.nmedia)

            actionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.purple_700)))

            findNavController().navigateUp()
        }
        actionBar?.setDisplayShowHomeEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        binding.photo.loadMediaAttachment(requireNotNull(requireArguments().mediaArg))
        actionBar?.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        return binding.root
    }

    /*    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
            val inflater = super.onGetLayoutInflater(savedInstanceState)
            val contextThemeWrapper: Context = ContextThemeWrapper(requireContext(), R.style.Theme_photoView)
            return inflater.cloneInContext(contextThemeWrapper)
        }*/
}
