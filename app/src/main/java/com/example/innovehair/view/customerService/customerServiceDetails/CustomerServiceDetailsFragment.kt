package com.example.innovehair.view.customerService.customerServiceDetails

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
import com.example.innovehair.databinding.FragmentCustomerServiceDetailsBinding
import com.example.innovehair.view.customerService.createCustomerService.CustomerServiceType
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CustomerServiceDetailsFragment : Fragment() {

    private lateinit var binding: FragmentCustomerServiceDetailsBinding
    private val viewModel by viewModels<CustomerServiceDetailsViewModel>()
    private val args by navArgs<CustomerServiceDetailsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCustomerServiceDetailsBinding.inflate(inflater, container, false)
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

        viewModel.viewState.observe(viewLifecycleOwner) { viewState ->
            binding.loadingGroup.isVisible = viewState is CustomerServiceDetailsState.Loading
            if (viewState is CustomerServiceDetailsState.Success) {
                findNavController().popBackStack()
            }
        }

        binding.scheduleApplicationGroup.isVisible = args.customerServiceDetails?.customerServiceResponse?.customerServiceType == CustomerServiceType.SCHEDULED_APPLICATION.name
        binding.collaboratorGroup.isVisible = args.isAdmin
        binding.editCustomerServiceCV.isVisible = args.customerServiceDetails?.customerServiceResponse?.customerServiceType == CustomerServiceType.SCHEDULED_SERVICE.name

        binding.name.text = args.customerServiceDetails?.client?.name ?: "---"
        binding.phoneNumber.text = args.customerServiceDetails?.client?.phoneNumber ?: "---"
        binding.email.text = args.customerServiceDetails?.client?.email ?: "---"
        binding.prothesisType.text = args.customerServiceDetails?.prothesisType?.name ?: "---"
        binding.createdAt.text = formatDate(args.customerServiceDetails?.customerServiceResponse?.createdAt)
        binding.customerServiceType.text = if (args.customerServiceDetails?.customerServiceResponse?.customerServiceType == CustomerServiceType.SCHEDULED_APPLICATION.name) {
            "Aplicação"
        } else {
            "Atendimento"
        }
        binding.scheduleServiceDate.text = formatDate(args.customerServiceDetails?.customerServiceResponse?.scheduleServiceDate)
        binding.width.text = args.customerServiceDetails?.customerServiceResponse?.width.toString()
        binding.length.text = args.customerServiceDetails?.customerServiceResponse?.length.toString()
        binding.hairDensityPercentage.text = args.customerServiceDetails?.customerServiceResponse?.capillaryDensity.toString()
        binding.hairColour.text = args.customerServiceDetails?.customerServiceResponse?.hairColour ?: "---"
        binding.hairTexture.text = args.customerServiceDetails?.customerServiceResponse?.hairTexture ?: "---"
        binding.scheduleApplicationDate.text = formatDate(args.customerServiceDetails?.customerServiceResponse?.scheduleApplicationDate)
        binding.allergies.text = if (args.customerServiceDetails?.customerServiceResponse?.allergies.isNullOrEmpty()) {
            "---"
        } else {
            args.customerServiceDetails?.customerServiceResponse?.allergies
        }
        binding.collaborator.text = args.customerServiceDetails?.collaborator?.name ?: "- - - -"

        binding.editCustomerServiceCV.setOnClickListener {
            val action = CustomerServiceDetailsFragmentDirections.actionCustomerServiceDetailsFragmentToEditCustomerServiceFragment(
                customerServiceDetails = args.customerServiceDetails,
                isAdmin = args.isAdmin
            )
            findNavController().navigate(action)
        }

        binding.deleteCustomerServiceCV.setOnClickListener {
            val customerServiceId = args.customerServiceDetails?.customerServiceResponse?.id
            lifecycleScope.launch {
                viewModel.deleteCustomerService(customerServiceId)
            }
        }
    }

    private fun formatDate(date: Date?) : String {
        if (date != null) {
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            return dateFormat.format(date)
        }
        return "---"
    }
}