package com.example.innovehair.view.editClient

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.innovehair.R
import com.example.innovehair.databinding.FragmentEditClientBinding
import com.example.innovehair.service.models.Client
import com.example.innovehair.view.collaboratorsList.CollaboratorData
import com.example.innovehair.view.success.Screens
import kotlinx.coroutines.launch

class EditClientFragment : Fragment() {

    private lateinit var binding: FragmentEditClientBinding
    private val viewModel by viewModels<EditClientViewModel>()
    private val args by navArgs<EditClientFragmentArgs>()
    private var argument: CollaboratorData? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditClientBinding.inflate(inflater, container, false)
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

        this.argument = findNavController().currentBackStackEntry?.savedStateHandle?.get<CollaboratorData>("collaborator")

        binding.nameInput.setText(args.clientData?.name ?: "")
        binding.phoneNumberInput.setText(args.clientData?.phoneNumber ?: "")
        binding.emailInput.setText(args.clientData?.email ?: "")
        binding.collaboratorGroup.isVisible = args.isAdmin
        binding.collaboratorName.text = if (this.argument == null) {
            if (args.collaboratorObject?.user?.name.isNullOrEmpty()) {
                "---"
            } else {
                args.collaboratorObject?.user?.name
            }
        } else {
            this.argument!!.user.name
        }

        binding.nameInput.addTextChangedListener(textWatcher)
        binding.phoneNumberInput.addTextChangedListener(textWatcher)
        binding.emailInput.addTextChangedListener(textWatcher)

        binding.editClientAssociation.setOnClickListener {
            val action = EditClientFragmentDirections.actionEditClientFragmentToSearchCollaboratorsFragment()
            findNavController().navigate(action)
        }

        viewModel.viewState.observe(viewLifecycleOwner) { viewState ->
            binding.loadingGroup.isVisible = viewState is EditClientViewState.Loading
            if (viewState is EditClientViewState.Success) {
                if (args.isAdmin) {
                    val action = EditClientFragmentDirections.actionEditClientFragmentToSuccessFragment(screen = Screens.MANAGER_DASHBOARD.name)
                    findNavController().navigate(action)
                } else {
                    val action = EditClientFragmentDirections.actionEditClientFragmentToSuccessFragment(screen = Screens.MAIN_DASHBOARD.name)
                    findNavController().navigate(action)
                }
            }
        }

        binding.btnEdit.setOnClickListener {
            lifecycleScope.launch {
                val clientId = args.clientData?.id
                val name = binding.nameInput.text.toString()
                val phoneNumber = binding.phoneNumberInput.text.toString()
                val email = binding.emailInput.text.toString()
                val collaboratorId = if (argument == null) {
                    if (args.collaboratorObject?.id.isNullOrEmpty()) {
                        return@launch
                    } else {
                        args.collaboratorObject?.id
                    }
                } else {
                    argument!!.id
                }
                val newClient = Client(
                    name = name,
                    phoneNumber = phoneNumber,
                    email = email,
                    collaboratorId = collaboratorId
                )

                viewModel.updateClient(clientId, newClient).also {
                    if (collaboratorId != null) {
                        viewModel.associateClientToDifferentCollaborator(clientId, collaboratorId)
                    }
                }
            }
        }
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
        val nameInput = binding.nameInput.text.toString()
        val phoneNumberInput = binding.phoneNumberInput.text.toString()
        val emailInput = binding.emailInput.text.toString()

        val isNameValid =
            nameInput.isNotBlank() && nameInput.isNotEmpty()
        val isPhoneNumberValid = phoneNumberInput.length == 9 && TextUtils.isDigitsOnly(phoneNumberInput)
        val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()

        val areFieldsValid = isNameValid && isPhoneNumberValid && isEmailValid

        val areFieldsChanged = (isNameValid && nameInput != args.clientData?.name) ||
                (isPhoneNumberValid && phoneNumberInput != args.clientData?.phoneNumber) ||
                (isEmailValid && emailInput != args.clientData?.email) ||
                (args.isAdmin && args.collaboratorObject?.id != argument?.id)

        binding.btnEdit.isEnabled = areFieldsValid && areFieldsChanged
    }
}