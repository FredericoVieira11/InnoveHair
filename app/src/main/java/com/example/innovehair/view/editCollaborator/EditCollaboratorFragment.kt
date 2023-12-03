package com.example.innovehair.view.editCollaborator

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.net.Uri
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
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
import com.example.innovehair.databinding.FragmentEditCollaboratorBinding
import com.example.innovehair.view.success.Screens
import kotlinx.coroutines.launch

class EditCollaboratorFragment : Fragment() {

    private lateinit var binding: FragmentEditCollaboratorBinding
    private val viewModel by viewModels<EditCollaboratorViewModel>()
    private var imageUri: Uri? = null

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                imageUri = data?.data ?: return@registerForActivityResult

                Glide.with(this)
                    .load(imageUri)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .error(R.drawable.ic_profile_default)
                    .placeholder(R.drawable.ic_profile_default)
                    .skipMemoryCache(true)
                    .into(binding.userImgProfile)

                val isImageChanged = imageUri != viewModel.data.value?.uri
                binding.btnOk.isEnabled = isImageChanged

                binding.groupBtn.isVisible = true
                binding.btnAddPicture.isVisible = false
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditCollaboratorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setIcon(R.drawable.ic_arrow_left_yellow)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().popBackStack()
        }

        viewModel.data.observe(viewLifecycleOwner) { data ->
            Glide.with(this)
                .load(data.uri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.ic_profile_default)
                .placeholder(R.drawable.ic_profile_default)
                .skipMemoryCache(true)
                .into(binding.userImgProfile)

            binding.groupBtn.isVisible = data.uri != null
            binding.btnAddPicture.isVisible = data.uri == null

            binding.nameInput.setText(data.user.name)
            binding.phoneNumberInput.setText(data.user.phoneNumber)
            binding.emailInput.setText(data.user.email)
        }

        binding.nameInput.addTextChangedListener(textWatcher)
        binding.phoneNumberInput.addTextChangedListener(textWatcher)
        binding.emailInput.addTextChangedListener(textWatcher)

        binding.btnEdit.setOnClickListener {
            openGallery()
        }

        binding.btnAddPicture.setOnClickListener {
            openGallery()
        }

        binding.btnRemove.setOnClickListener {
            binding.userImgProfile.setImageResource(R.drawable.ic_profile_default)
            imageUri = null
            binding.btnOk.isEnabled = imageUri != viewModel.data.value?.uri
            binding.groupBtn.isVisible = false
            binding.btnAddPicture.isVisible = true
        }

        binding.btnOk.setOnClickListener {
            val newName = binding.nameInput.text.toString()
            val newPhoneNumber = binding.phoneNumberInput.text.toString()
            val newEmail = binding.emailInput.text.toString()

            if (viewModel.data.value?.uri != null && imageUri == null) {
                lifecycleScope.launch {
                    viewModel.deleteUserImage().also {
                        viewModel.updateUserData(
                            newName,
                            newPhoneNumber,
                            newEmail
                        )
                    }
                }
            } else {
                lifecycleScope.launch {
                    imageUri?.let {
                        viewModel.updateUserImage(it)
                    }.also {
                        viewModel.updateUserData(
                            newName,
                            newPhoneNumber,
                            newEmail
                        )
                    }
                }
            }
            val action = EditCollaboratorFragmentDirections.actionEditCollaboratorFragmentToSuccessFragment(screen = Screens.MAIN_DASHBOARD.name)
            findNavController().navigate(action)
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
            val name = binding.nameInput.text.toString()
            val phoneNumber = binding.phoneNumberInput.text.toString()
            val email = binding.emailInput.text.toString()

            val isNameValid = name.isNotBlank()
            val isPhoneNumberValid = phoneNumber.length == 9 && TextUtils.isDigitsOnly(phoneNumber)
            val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()

            // Verifica se todos os campos têm valores válidos
            val areFieldsValid = isNameValid && isPhoneNumberValid && isEmailValid

            // Verifica se os campos foram alterados e têm valores válidos
            val areFieldsChanged = (isNameValid && name != viewModel.data.value?.user?.name) ||
                    (isPhoneNumberValid && phoneNumber != viewModel.data.value?.user?.phoneNumber) ||
                    (isEmailValid && email != viewModel.data.value?.user?.email)

            // Habilitar ou desabilitar o botão "Ok" com base nas alterações nos campos e nos valores válidos
            binding.btnOk.isEnabled = areFieldsChanged && areFieldsValid
        }
    }
}