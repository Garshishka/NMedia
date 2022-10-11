package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.PostViewModel
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.utils.StringArg

class NewPostFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel by viewModels<PostViewModel>(ownerProducer = ::requireParentFragment)

        val binding = FragmentNewPostBinding.inflate(inflater, container, false)
        arguments?.textArg?.let(binding.edit::setText)
        binding.edit.requestFocus()
        binding.ok.setOnClickListener {
            val text = binding.edit.text.toString()
            if (text.isNotBlank()) {
                viewModel.changeContent(text)
                viewModel.save()
            }
            findNavController().navigateUp()
        }
        return binding.root
    }

    companion object {
        var Bundle.textArg by StringArg
    }

}