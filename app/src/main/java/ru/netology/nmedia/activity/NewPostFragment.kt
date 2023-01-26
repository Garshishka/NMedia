package ru.netology.nmedia.activity

import android.app.Activity
import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.di.DependencyContainer
import ru.netology.nmedia.utils.AndroidUtils
import ru.netology.nmedia.utils.StringArg
import ru.netology.nmedia.viemodel.PostViewModel
import ru.netology.nmedia.viemodel.ViewModelFactory

class NewPostFragment : Fragment() {
    private val dependencyContainer = DependencyContainer.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel by viewModels<PostViewModel>(
            ownerProducer = ::requireParentFragment,
            factoryProducer = {
                ViewModelFactory(
                    dependencyContainer.repository,
                    dependencyContainer.appAuth,
                    dependencyContainer.apiService
                )
            })
        val isEditing = arguments?.textArg != null
        val binding = FragmentNewPostBinding.inflate(inflater, container, false)
        val contract =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val resultCode = result.resultCode
                val data = result.data

                when (resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(data),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }

                    Activity.RESULT_OK -> {
                        //Image Uri will not be null for RESULT_OK
                        val fileUri = data?.data

                        viewModel.changePhoto(fileUri, fileUri?.toFile())
                    }
                }
            }

        if (isEditing) {
            arguments?.textArg.let(binding.edit::setText)
            viewModel.draft = ""
        } else {
            binding.edit.setText(viewModel.draft)
        }
        binding.edit.requestFocus()

        viewModel.photo.observe(viewLifecycleOwner) {
            binding.photo.setImageURI(it.uri)
            binding.photoContainer.isVisible = it.uri != null
        }

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (!isEditing) {
                viewModel.draft = binding.edit.text.toString()
            }
            findNavController().navigateUp()
        }

        binding.pickPhoto.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .crop()
                .compress(2048)
                .createIntent(contract::launch)
        }

        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .cameraOnly()
                .crop()
                .compress(2048)
                .createIntent(contract::launch)
        }

        binding.removePhoto.setOnClickListener {
            viewModel.deletePhoto()
        }

        activity?.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_new_post, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.logOut -> {
                        binding.logOutTab.isVisible = true
                        true
                    }
                    R.id.save -> {
                        viewModel.draft = ""
                        val text = binding.edit.text.toString()
                        if (text.isNotBlank()) {
                            viewModel.changeContent(text)
                            viewModel.save()
                            findNavController().navigateUp()
                        }
                        AndroidUtils.hideKeyboard(requireView())
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)

        binding.goBackButton.setOnClickListener {
            binding.logOutTab.isVisible = false
        }

        binding.logOutButton.setOnClickListener {
            dependencyContainer.appAuth.removeAuth()
            findNavController().navigateUp()
        }
        return binding.root
    }

    companion object {
        var Bundle.textArg by StringArg
    }

}