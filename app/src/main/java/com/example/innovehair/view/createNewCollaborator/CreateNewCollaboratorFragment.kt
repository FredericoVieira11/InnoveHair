package com.example.innovehair.view.createNewCollaborator

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.innovehair.R
import com.example.innovehair.databinding.FragmentCreateNewCollaboratorBinding
import com.example.innovehair.view.success.Screens
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class CreateNewCollaboratorFragment : Fragment() {

    private lateinit var binding: FragmentCreateNewCollaboratorBinding
    private val viewModel by viewModels<CreateNewCollaboratorViewModel>()
    //private val args: CreateNewCollaboratorFragmentArgs by navArgs()
    private var imageUri: Uri? = null

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                imageUri = data?.data ?: return@registerForActivityResult

                binding.userImgProfile.setImageURI(imageUri)
                binding.groupBtn.isVisible = true
                binding.btnAddPicture.isVisible = false
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateNewCollaboratorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.imageUri.observe(viewLifecycleOwner) {
            Glide.with(this)
                .load(it)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.ic_profile_default)
                .skipMemoryCache(true)
                .into(binding.userImgProfile)
        }

        viewModel.viewState.observe(viewLifecycleOwner) { viewState ->
            binding.groupLoading.isVisible = viewState is CreateNewCollaboratorViewState.Loading

            if (viewState is CreateNewCollaboratorViewState.Success) {
                FirebaseAuth.getInstance().signOut()
                val action = CreateNewCollaboratorFragmentDirections.actionCreateNewCollaboratorFragmentToSuccessFragment(screen = Screens.CREATE_NEW_COLLABORATOR.name)
                findNavController().navigate(action)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().popBackStack()
        }

        binding.btnBack.setIcon(R.drawable.ic_arrow_left_yellow)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.nameInput.addTextChangedListener(textWatcher)
        binding.contactInput.addTextChangedListener(textWatcher)
        binding.emailInput.addTextChangedListener(textWatcher)

        binding.btnRemove.setOnClickListener {
            binding.userImgProfile.setImageResource(R.drawable.ic_profile_default)
            binding.groupBtn.isVisible = false
            binding.btnAddPicture.isVisible = true
        }

        binding.btnEdit.setOnClickListener {
            openGallery()
        }

        binding.btnAddPicture.setOnClickListener {
            openGallery()
        }

        binding.btnRegister.setOnClickListener {
            lifecycleScope.launch {
                if (imageUri == null) {
                    viewModel.createNewCollaborator(
                        null,
                        binding.nameInput.text.toString(),
                        binding.contactInput.text.toString(),
                        binding.emailInput.text.toString()
                    )
                } else {
                    viewModel.createNewCollaborator(
                        imageUri,
                        binding.nameInput.text.toString(),
                        binding.contactInput.text.toString(),
                        binding.emailInput.text.toString()
                    )
                }
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            validateFields()
        }
    }

    private fun validateFields() {
        val contactInput = binding.contactInput.text.toString()
        val emailInput = binding.emailInput.text.toString()

        val nameValidation =
            binding.nameInput.text.toString().isNotBlank() && binding.nameInput.text.toString().isNotEmpty()
        val contactValidation = contactInput.length == 9
        val emailValidation = Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()

        binding.btnRegister.isEnabled = nameValidation && contactValidation && emailValidation
    }
}