package com.example.innovehair.view.createClientApplication

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.innovehair.R
import com.example.innovehair.databinding.FragmentCreateClientApplicationBinding
import com.example.innovehair.service.models.CapillaryProsthesis
import com.example.innovehair.service.models.CustomerService
import com.example.innovehair.view.customerService.createCustomerService.CreateCustomerServiceFragmentDirections
import com.example.innovehair.view.customerService.createCustomerService.CreateCustomerServiceViewState
import com.example.innovehair.view.customerService.createCustomerService.CustomerServiceType
import com.example.innovehair.view.customerService.createCustomerService.adapter.CustomerServiceCapillaryProthesisAdapter
import com.example.innovehair.view.success.Screens
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CreateClientApplicationFragment : Fragment() {

    private lateinit var binding: FragmentCreateClientApplicationBinding
    private lateinit var adapter: CustomerServiceCapillaryProthesisAdapter
    private val args by navArgs<CreateClientApplicationFragmentArgs>()
    private val viewModel by viewModels<CreateClientApplicationViewModel>()
    private var prothesisTpeSelected: CapillaryProsthesis? = null
    private var densityValue: Int? = null
    private var scheduleApplicationDate: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateClientApplicationBinding.inflate(inflater, container, false)
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

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Atualiza o texto do EditText de acordo com o progresso da SeekBar
                densityValue = 80 + (progress * 1.0).toInt() // Mapeia o progresso de 0-100 para 80-180
                binding.hairDensityPercentageTxt.text = "$densityValue%"
                validateFields()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }
        })

        binding.scheduleApplicationDateCV.setOnClickListener {
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

                    this.scheduleApplicationDate = dateFormat.parse(formattedDate)

                    // Exibir a data formatada no TextView
                    binding.scheduleApplicationDateValue.text = formattedDate
                    validateFields()
                },
                year,
                month,
                day
            )

            datePickerDialog.show()
        }

        binding.allergiesCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.allergiesTitle.isVisible = true
                binding.allergiesAutoComplete.isVisible = true
            } else {
                binding.allergiesTitle.isVisible = false
                binding.allergiesAutoComplete.isVisible = false
            }
            validateFields()
        }
        binding.widthInput.addTextChangedListener(textWatcher)
        binding.lengthInput.addTextChangedListener(textWatcher)
        binding.hairColourInput.addTextChangedListener(textWatcher)
        binding.hairTextureInput.addTextChangedListener(textWatcher)
        binding.allergiesAutoComplete.addTextChangedListener(textWatcher)

        viewModel.prosthesisList.observe(viewLifecycleOwner) {
            setupRV(it)
        }

        binding.btnRegister.setOnClickListener {
            lifecycleScope.launch {
                val widthInput = binding.widthInput.text.toString().toDoubleOrNull()
                val lengthInput = binding.lengthInput.text.toString().toDoubleOrNull()
                val hairColourInput = binding.hairColourInput.text.toString()
                val hairTextureInput = binding.hairTextureInput.text.toString()
                val allergiesInput = binding.allergiesAutoComplete.text.toString()

                val data = CustomerService(
                    args.clientData?.id,
                    FirebaseAuth.getInstance().currentUser?.uid,
                    prothesisTpeSelected?.id,
                    CustomerServiceType.SCHEDULED_APPLICATION.name,
                    scheduleServiceDate = null,
                    width = widthInput,
                    length = lengthInput,
                    capillaryDensity = densityValue,
                    hairColour = hairColourInput,
                    hairTexture = hairTextureInput,
                    scheduleApplicationDate= scheduleApplicationDate,
                    allergies = allergiesInput,
                    createdAt = Date()
                )
                viewModel.createApplication(data)
            }
        }

        viewModel.viewState.observe(viewLifecycleOwner) { viewState ->
            binding.loadingGroup.isVisible = viewState is CreateClientApplicationViewModel.CreateClientApplicationViewState.Loading
            if (viewState is CreateClientApplicationViewModel.CreateClientApplicationViewState.Success) {
                val action = CreateClientApplicationFragmentDirections.actionCreateClientApplicationFragmentToSuccessFragment(screen = Screens.MAIN_DASHBOARD.name)
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
        val widthInput = binding.widthInput.text.toString()
        val lengthInput = binding.lengthInput.text.toString()
        val hairColourInput = binding.hairColourInput.text.toString()
        val hairTextureInput = binding.hairTextureInput.text.toString()
        val scheduleApplicationDateValue = binding.scheduleApplicationDateValue.text
        val allergiesCheckbox = binding.allergiesCheckbox.isChecked
        val allergiesInput = binding.allergiesAutoComplete.text.toString()

        val prothesisTypeValidation = this.prothesisTpeSelected != null
        val widthValidation = widthInput.isNotBlank() && widthInput.isNotEmpty()
        val lengthValidation = lengthInput.isNotBlank() && lengthInput.isNotEmpty()
        val hairColourValidation = hairColourInput.isNotBlank() && hairColourInput.isNotEmpty()
        val hairTextureValidation = hairTextureInput.isNotBlank() && hairTextureInput.isNotEmpty()
        val densityValueValidation = this.densityValue != null
        val scheduleApplicationDateValueValidation = scheduleApplicationDateValue.isNotBlank() && scheduleApplicationDateValue.isNotEmpty()
        val allergiesValidation = !allergiesCheckbox || (allergiesCheckbox && allergiesInput.isNotBlank() && allergiesInput.isNotEmpty())

        binding.btnRegister.isEnabled =
            prothesisTypeValidation && widthValidation && lengthValidation &&
                    hairColourValidation && hairTextureValidation && densityValueValidation
                    && scheduleApplicationDateValueValidation && allergiesValidation
    }
}