package com.example.innovehair.view.createClientService

import android.app.DatePickerDialog
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.innovehair.R
import com.example.innovehair.databinding.FragmentCreateClientServiceBinding
import com.example.innovehair.service.models.CapillaryProsthesis
import com.example.innovehair.service.models.CustomerService
import com.example.innovehair.view.customerService.createCustomerService.CustomerServiceType
import com.example.innovehair.view.customerService.createCustomerService.adapter.CustomerServiceCapillaryProthesisAdapter
import com.example.innovehair.view.success.Screens
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CreateClientServiceFragment : Fragment() {

    private lateinit var binding: FragmentCreateClientServiceBinding
    private lateinit var adapter: CustomerServiceCapillaryProthesisAdapter
    private val viewModel by viewModels<CreateClientServiceViewModel>()
    private val args by navArgs<CreateClientServiceFragmentArgs>()
    private var prothesisTpeSelected: CapillaryProsthesis? = null
    private var scheduleServiceDate: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateClientServiceBinding.inflate(inflater, container, false)
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

        binding.scheduleServiceDateCV.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Aqui você pode lidar com a data selecionada
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(selectedYear, selectedMonth, selectedDay)

                    // Formatar a data para o formato desejado (dia-mes-ano)
                    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    val formattedDate = dateFormat.format(selectedDate.time)

                    this.scheduleServiceDate = dateFormat.parse(formattedDate)

                    // Exibir a data formatada no TextView
                    binding.scheduleServiceDateValue.text = formattedDate
                    validateFields()
                },
                year,
                month,
                day
            )

            datePickerDialog.show()
        }

        binding.btnRegister.setOnClickListener {
            lifecycleScope.launch {
                val data = CustomerService(
                    args.clientData?.id,
                    FirebaseAuth.getInstance().currentUser?.uid,
                    prothesisTpeSelected?.id,
                    CustomerServiceType.SCHEDULED_SERVICE.name,
                    scheduleServiceDate = scheduleServiceDate,
                    createdAt = Date()
                )
                viewModel.createService(data)
            }
        }

        viewModel.viewState.observe(viewLifecycleOwner) { viewState ->
            binding.loadingGroup.isVisible = viewState is CreateClientServiceViewModel.CreateClientServiceViewState.Loading

            if (viewState is CreateClientServiceViewModel.CreateClientServiceViewState.Success) {
                val action = CreateClientServiceFragmentDirections.actionCreateClientServiceFragmentToSuccessFragment(screen = Screens.MAIN_DASHBOARD.name)
                findNavController().navigate(action)
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
        this.adapter = CustomerServiceCapillaryProthesisAdapter(mutableData, null) {
            this.prothesisTpeSelected = it
            validateFields()
        }
        binding.recyclerView.adapter = this.adapter
    }

    private fun validateFields() {
        val scheduleServiceDateValue = binding.scheduleServiceDateValue.text

        val prothesisTypeValidation = this.prothesisTpeSelected != null
        val scheduleServiceDateValueValidation = scheduleServiceDateValue.isNotBlank() && scheduleServiceDateValue.isNotEmpty()

        binding.btnRegister.isEnabled = prothesisTypeValidation && scheduleServiceDateValueValidation
    }
}