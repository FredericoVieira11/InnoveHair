package com.example.innovehair.view.customerService.createCustomerService

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.innovehair.R
import com.example.innovehair.databinding.FragmentCreateCustomerServiceBinding
import com.example.innovehair.service.models.CapillaryProsthesis
import com.example.innovehair.service.models.Client
import com.example.innovehair.service.models.CustomerService
import com.example.innovehair.view.customerService.createCustomerService.adapter.CustomerServiceCapillaryProthesisAdapter
import com.example.innovehair.view.success.Screens
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CreateCustomerServiceFragment : Fragment() {

    private lateinit var binding: FragmentCreateCustomerServiceBinding
    private lateinit var adapter: CustomerServiceCapillaryProthesisAdapter
    private val viewModel by viewModels<CreateCustomerServiceViewModel>()
    private var customerServiceAdapterPosition: Int? = null
    private var prothesisTpeSelected: CapillaryProsthesis? = null
    private var densityValue: Int? = null
    private var scheduleServiceDate: Date? = null
    private var scheduleApplicationDate: Date? = null
    private lateinit var customerServiceType: CustomerServiceType

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateCustomerServiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().popBackStack()
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnBack.setIcon(R.drawable.ic_arrow_left_yellow)

        val customerServiceType = resources.getStringArray(R.array.customerServiceType)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, customerServiceType)
        binding.customerServiceType.setAdapter(arrayAdapter)

        binding.customerServiceType.addTextChangedListener(textWatcher)

        binding.customerServiceType.setOnItemClickListener { parent, view, position, id ->
            when (position) {
                0 -> {
                    binding.registrationFieldsGroup.isVisible = true
                    binding.scheduleServiceGroup.isVisible = true
                    binding.scheduleApplicationGroup.isVisible = false
                    this.customerServiceAdapterPosition = position
                }
                1 -> {
                    binding.registrationFieldsGroup.isVisible = true
                    binding.scheduleServiceGroup.isVisible = false
                    binding.scheduleApplicationGroup.isVisible = true
                    this.customerServiceAdapterPosition = position
                }
                else -> {
                    return@setOnItemClickListener
                }
            }
            validateFields()
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

        binding.nameInput.addTextChangedListener(textWatcher)
        binding.phoneNumberInput.addTextChangedListener(textWatcher)
        binding.emailInput.addTextChangedListener(textWatcher)
        binding.widthInput.addTextChangedListener(textWatcher)
        binding.lengthInput.addTextChangedListener(textWatcher)
        binding.hairColourInput.addTextChangedListener(textWatcher)
        binding.hairTextureInput.addTextChangedListener(textWatcher)
        binding.allergiesAutoComplete.addTextChangedListener(textWatcher)

        binding.btnRegister.setOnClickListener {
            lifecycleScope.launch {
                val nameInput = binding.nameInput.text.toString()
                val phoneNumberInput = binding.phoneNumberInput.text.toString()
                val emailInput = binding.emailInput.text.toString()
                val widthInput = binding.widthInput.text.toString().toDoubleOrNull()
                val lengthInput = binding.lengthInput.text.toString().toDoubleOrNull()
                val hairColourInput = binding.hairColourInput.text.toString()
                val hairTextureInput = binding.hairTextureInput.text.toString()
                val allergiesInput = binding.allergiesAutoComplete.text.toString()

                if (customerServiceAdapterPosition == null) return@launch
                val clientData = Client(
                    name = nameInput,
                    phoneNumber = phoneNumberInput,
                    email = emailInput
                )
                val customerServiceData = CustomerService(
                    prothesisTypeId = prothesisTpeSelected?.id,
                    customerServiceType = CustomerServiceType.fromInt(
                        customerServiceAdapterPosition!!
                    )?.name,
                    scheduleServiceDate = scheduleServiceDate,
                    width = widthInput,
                    length = lengthInput,
                    capillaryDensity = densityValue,
                    hairColour = hairColourInput,
                    hairTexture = hairTextureInput,
                    scheduleApplicationDate = scheduleApplicationDate,
                    allergies = allergiesInput,
                    createdAt = Date()
                )
                viewModel.registerCustomerService(clientData, customerServiceData)
            }
        }

        viewModel.viewState.observe(viewLifecycleOwner) { viewState ->
            binding.loadingGroup.isVisible = viewState is CreateCustomerServiceViewState.Loading
            if (viewState is CreateCustomerServiceViewState.Success) {
                val action = CreateCustomerServiceFragmentDirections.actionCreateCustomerServiceFragmentToSuccessFragment(screen = Screens.CREATE_CUSTOMER_SERVICE.name)
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
        when(this.customerServiceAdapterPosition) {
            0 -> {
                validateFieldsScheduleService()
            }
            1 -> {
                validateFieldsScheduleApplication()
            }
            2 -> {
                // i do not need this guy here
                validateFieldsClientRegistration()
            }
            null -> {
                return
            }
        }
    }

    // i do not need this function
    private fun validateFieldsClientRegistration() {
        val contactInput = binding.phoneNumberInput.text.toString()
        val emailInput = binding.emailInput.text.toString()

        val nameValidation =
            binding.nameInput.text.toString().isNotBlank() && binding.nameInput.text.toString().isNotEmpty()
        val contactValidation = contactInput.length == 9
        val emailValidation = Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()

        binding.btnRegister.isEnabled = nameValidation && contactValidation && emailValidation
    }

    private fun validateFieldsScheduleService() {
        val contactInput = binding.phoneNumberInput.text.toString()
        val emailInput = binding.emailInput.text.toString()
        val scheduleServiceDateValue = binding.scheduleServiceDateValue.text

        val nameValidation =
            binding.nameInput.text.toString().isNotBlank() && binding.nameInput.text.toString().isNotEmpty()
        val contactValidation = contactInput.length == 9
        val emailValidation = Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()
        val prothesisTypeValidation = this.prothesisTpeSelected != null
        val scheduleServiceDateValueValidation = scheduleServiceDateValue.isNotBlank() && scheduleServiceDateValue.isNotEmpty()

        binding.btnRegister.isEnabled = nameValidation && contactValidation && emailValidation && prothesisTypeValidation && scheduleServiceDateValueValidation
    }

    private fun validateFieldsScheduleApplication() {
        val contactInput = binding.phoneNumberInput.text.toString()
        val emailInput = binding.emailInput.text.toString()
        val widthInput = binding.widthInput.text.toString()
        val lengthInput = binding.lengthInput.text.toString()
        val hairColourInput = binding.hairColourInput.text.toString()
        val hairTextureInput = binding.hairTextureInput.text.toString()
        val scheduleApplicationDateValue = binding.scheduleApplicationDateValue.text
        val allergiesCheckbox = binding.allergiesCheckbox.isChecked
        val allergiesInput = binding.allergiesAutoComplete.text.toString()

        val nameValidation =
            binding.nameInput.text.toString().isNotBlank() && binding.nameInput.text.toString().isNotEmpty()
        val contactValidation = contactInput.length == 9
        val emailValidation = Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()
        val prothesisTypeValidation = this.prothesisTpeSelected != null
        val widthValidation = widthInput.isNotBlank() && widthInput.isNotEmpty()
        val lengthValidation = lengthInput.isNotBlank() && lengthInput.isNotEmpty()
        val hairColourValidation = hairColourInput.isNotBlank() && hairColourInput.isNotEmpty()
        val hairTextureValidation = hairTextureInput.isNotBlank() && hairTextureInput.isNotEmpty()
        val densityValueValidation = this.densityValue != null
        val scheduleApplicationDateValueValidation = scheduleApplicationDateValue.isNotBlank() && scheduleApplicationDateValue.isNotEmpty()
        val allergiesValidation = !allergiesCheckbox || (allergiesCheckbox && allergiesInput.isNotBlank() && allergiesInput.isNotEmpty())

        binding.btnRegister.isEnabled =
                    nameValidation && contactValidation && emailValidation && prothesisTypeValidation
                    && widthValidation && lengthValidation && hairColourValidation && hairTextureValidation && densityValueValidation
                    && scheduleApplicationDateValueValidation && allergiesValidation
    }
}

enum class CustomerServiceType {
    SCHEDULED_SERVICE,
    SCHEDULED_APPLICATION;

    companion object {
        fun fromInt(value: Int): CustomerServiceType? {
            return when (value) {
                0 -> SCHEDULED_SERVICE
                1 -> SCHEDULED_APPLICATION
                else -> null
            }
        }
    }
}