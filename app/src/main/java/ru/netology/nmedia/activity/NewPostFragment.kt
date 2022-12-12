package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.PostViewModel
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.utils.AndroidUtils
import ru.netology.nmedia.utils.StringArg

class NewPostFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel by viewModels<PostViewModel>(ownerProducer = ::requireParentFragment)
        val isEditing = arguments?.textArg != null

        val binding = FragmentNewPostBinding.inflate(inflater, container, false)
        if (isEditing) {
            arguments?.textArg.let(binding.edit::setText)
            viewModel.draft = ""
        } else {
            binding.edit.setText(viewModel.draft)
        }
        binding.edit.requestFocus()
        binding.ok.setOnClickListener {
            viewModel.draft = ""
            val text = binding.edit.text.toString()
            if (text.isNotBlank()) {
                viewModel.changeContent(text)
                viewModel.save()
            }
            AndroidUtils.hideKeyboard(requireView())
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            viewModel.load()
            findNavController().navigateUp()
        }

        viewModel.postCreatedError.observe(viewLifecycleOwner) {
            Toast.makeText(
                activity,
                getString(R.string.specific_posting_error, it),
                Toast.LENGTH_LONG
            )
                .show()
        }

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (!isEditing) {
                viewModel.draft = binding.edit.text.toString()
            }
            findNavController().navigateUp()
        }

        return binding.root
    }

    companion object {
        var Bundle.textArg by StringArg
    }

}