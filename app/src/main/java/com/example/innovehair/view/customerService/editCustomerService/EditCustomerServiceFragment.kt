package com.example.innovehair.view.customerService.editCustomerService

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.innovehair.R
import com.example.innovehair.databinding.FragmentEditCustomerServiceBinding
import com.example.innovehair.service.models.CapillaryProsthesis
import com.example.innovehair.service.models.CustomerService
import com.example.innovehair.view.collaboratorsList.CollaboratorData
import com.example.innovehair.view.customerService.createCustomerService.CustomerServiceType
import com.example.innovehair.view.customerService.createCustomerService.adapter.CustomerServiceCapillaryProthesisAdapter
import com.example.innovehair.view.success.Screens
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EditCustomerServiceFragment : Fragment() {

    private lateinit var binding: FragmentEditCustomerServiceBinding
    private val viewModel by viewModels<EditCustomerServiceViewModel>()
    private lateinit var adapter: CustomerServiceCapillaryProthesisAdapter
    private val args by navArgs<EditCustomerServiceFragmentArgs>()
    private var prothesisTpeSelected: CapillaryProsthesis? = null
    private var scheduleServiceDate: Date? = null
    private var userSelectedDate = false
    private var argument: CollaboratorData? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditCustomerServiceBinding.inflate(inflater, container, false)
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

        viewModel.prosthesisList.observe(viewLifecycleOwner) {
            setupRV(it)
        }

        this.argument = findNavController().currentBackStackEntry?.savedStateHandle?.get<CollaboratorData>("collaborator").also {
            validateFields()
        }

        binding.collaboratorGroup.isVisible = args.customerServiceDetails?.collaborator != null

        binding.scheduleServiceDateValue.text = formatDate(args.customerServiceDetails?.customerServiceResponse?.scheduleServiceDate)

        binding.collaboratorName.text = if (this.argument != null) {
            this.argument!!.user.name
        } else {
            args.customerServiceDetails?.collaborator?.name
        }

        binding.scheduleServiceDateCV.setOnClickListener {
            val calendar = Calendar.getInstance()

            // Configurar a data inicial a partir do args somente se o usuário ainda não tiver selecionado uma data
            if (!userSelectedDate) {
                args.customerServiceDetails?.customerServiceResponse?.scheduleServiceDate?.let { initialDate ->
                    calendar.time = initialDate
                }
            }

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(selectedYear, selectedMonth, selectedDay)

                    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    val formattedDate = dateFormat.format(selectedDate.time)

                    this.scheduleServiceDate = dateFormat.parse(formattedDate)

                    binding.scheduleServiceDateValue.text = formattedDate
                    validateFields()

                    userSelectedDate = true
                },
                year,
                month,
                day
            )

            datePickerDialog.show()
        }

        binding.editCollaboratorAssociation.setOnClickListener {
            val action = EditCustomerServiceFragmentDirections.actionEditCustomerServiceFragmentToSearchCollaboratorsFragment()
            findNavController().navigate(action)
        }

        viewModel.viewSate.observe(viewLifecycleOwner) { viewState ->
            binding.groupLoading.isVisible = viewState is EditCustomerServiceViewState.Loading
            if (viewState is EditCustomerServiceViewState.Success) {
                if (args.isAdmin) {
                    val action = EditCustomerServiceFragmentDirections.actionEditCustomerServiceFragmentToSuccessFragment(screen = Screens.MANAGER_DASHBOARD.name)
                    findNavController().navigate(action)
                } else {
                    val action = EditCustomerServiceFragmentDirections.actionEditCustomerServiceFragmentToSuccessFragment(screen = Screens.MAIN_DASHBOARD.name)
                    findNavController().navigate(action)
                }
            }
        }

        binding.btnRegister.setOnClickListener {
            lifecycleScope.launch {
                val newScheduleServiceDate = if (scheduleServiceDate == null) {
                    args.customerServiceDetails?.customerServiceResponse?.scheduleServiceDate
                } else {
                    scheduleServiceDate
                }

                val newProthesisTpeSelected = if (prothesisTpeSelected == null) {
                    args.customerServiceDetails?.customerServiceResponse?.prothesisTypeId
                } else {
                    prothesisTpeSelected!!.id
                }
                val newCustomerServiceData = CustomerService(
                    prothesisTypeId = newProthesisTpeSelected,
                    scheduleServiceDate = newScheduleServiceDate
                )

                viewModel.updateCustomerService(
                    args.customerServiceDetails?.customerServiceResponse?.id,
                    newCustomerServiceData
                )
            }
        }
    }

    private fun setupRV(data: List<CapillaryProsthesis>) {
        val mutableData: MutableList<CapillaryProsthesis> = if (data is MutableList) {
            data
        } else {
            data.toMutableList()
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val prothesisAlreadySelected = args.customerServiceDetails?.customerServiceResponse?.prothesisTypeId
        this.adapter = CustomerServiceCapillaryProthesisAdapter(mutableData, prothesisAlreadySelected) {
            this.prothesisTpeSelected = it
            validateFields()
        }
        binding.recyclerView.adapter = this.adapter
    }

    private fun formatDate(date: Date?) : String {
        if (date != null) {
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            return dateFormat.format(date)
        }
        return ""
    }

    private fun validateFields() {
        val prothesisTypeValidation = this.prothesisTpeSelected?.id != null &&
                this.prothesisTpeSelected?.id != args.customerServiceDetails?.customerServiceResponse?.prothesisTypeId

        val scheduleServiceDateValue = binding.scheduleServiceDateValue.text.toString()
        val scheduleServiceDateValidation = scheduleServiceDateValue.isNotBlank() &&
                scheduleServiceDateValue != formatDate(args.customerServiceDetails?.customerServiceResponse?.scheduleServiceDate)

        //val collaboratorValue = binding.collaboratorName.text.toString()
        //val collaboratorValidation = this.argument != null &&
        //        args.customerServiceDetails?.collaborator?.id != this.argument?.id

        binding.btnRegister.isEnabled = prothesisTypeValidation || scheduleServiceDateValidation// || collaboratorValidation
    }
}