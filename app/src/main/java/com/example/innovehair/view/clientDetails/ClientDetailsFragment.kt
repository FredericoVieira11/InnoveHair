package com.example.innovehair.view.clientDetails

import android.os.Bundle
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
import com.example.innovehair.databinding.FragmentClientDetailsBinding
import kotlinx.coroutines.launch

class ClientDetailsFragment : Fragment() {

    private lateinit var binding: FragmentClientDetailsBinding
    private val args by navArgs<ClientDetailsFragmentArgs>()
    private val viewModel by viewModels<ClientDetailsViewModel>{
        ClientDetailsViewModel.ClientDetailsViewModelFactory(args.clientData?.collaboratorId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClientDetailsBinding.inflate(inflater, container, false)
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

        binding.name.text = args.clientData?.name ?: ""
        binding.phoneNumber.text = args.clientData?.phoneNumber ?: ""
        binding.email.text = args.clientData?.email ?: ""
        binding.clientAssociationGroup.isVisible = args.isAdmin

        binding.createServiceCV.isVisible = !args.isAdmin
        binding.createApplicationCV.isVisible = !args.isAdmin

        viewModel.collaboratorData.observe(viewLifecycleOwner) {
            binding.collaborator.text = if (it?.user?.name.isNullOrEmpty()) {
                 "---"
            } else {
                it?.user?.name
            }
        }

        viewModel.viewState.observe(viewLifecycleOwner) { viewState ->
            if (viewState is ClientDetailsViewState.Success) {
                findNavController().popBackStack()
            }
        }

        binding.createServiceCV.setOnClickListener {
            val action = ClientDetailsFragmentDirections.actionClientDetailsFragmentToCreateClientServiceFragment(
                clientData = args.clientData
            )
            findNavController().navigate(action)
        }

        binding.createApplicationCV.setOnClickListener {
            val action = ClientDetailsFragmentDirections.actionClientDetailsFragmentToCreateClientApplicationFragment(
                clientData = args.clientData
            )
            findNavController().navigate(action)
        }

        binding.cvEditClient.setOnClickListener {
            val collaborator = viewModel.collaboratorData.value
            val action = ClientDetailsFragmentDirections.actionClientDetailsFragmentToEditClientFragment(
                clientData = args.clientData,
                isAdmin = args.isAdmin,
                collaboratorObject = collaborator
            )
            findNavController().navigate(action)
        }

        binding.cvDeleteClient.setOnClickListener {
            val clientId = args.clientData?.id
            lifecycleScope.launch {
                viewModel.deleteClient(clientId)
            }
        }
    }
}