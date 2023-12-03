package com.example.innovehair.view.customerService.serviceHistoryList

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.innovehair.R
import com.example.innovehair.databinding.CustomDialogSearchBinding
import com.example.innovehair.databinding.FragmentServiceHistoryListBinding
import com.example.innovehair.service.models.CapillaryProsthesis
import com.example.innovehair.service.models.respondeModels.ClientResponse
import com.example.innovehair.service.models.respondeModels.UserResponse
import com.example.innovehair.view.clientsList.adapter.ClientsListAdapter
import com.example.innovehair.view.customerService.createCustomerService.CustomerServiceType
import com.example.innovehair.view.customerService.serviceHistoryList.adapter.CapillaryProthesisAdapter
import com.example.innovehair.view.customerService.serviceHistoryList.adapter.ClientAdapter
import com.example.innovehair.view.customerService.serviceHistoryList.adapter.CollaboratorAdapter
import com.example.innovehair.view.customerService.serviceHistoryList.adapter.ServiceHistoryListAdapter
import kotlinx.coroutines.launch

class ServiceHistoryListFragment : Fragment() {

    private lateinit var binding: FragmentServiceHistoryListBinding
    private lateinit var dialogBinding: CustomDialogSearchBinding
    private val viewModel by viewModels<ServiceHistoryListViewModel>()
    private lateinit var adapter:ServiceHistoryListAdapter
    private lateinit var clientsList: List<ClientResponse>
    private lateinit var collaborators: List<UserResponse>
    private lateinit var prothesisList: List<CapillaryProsthesis>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentServiceHistoryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setIcon(R.drawable.ic_arrow_left_yellow)
        binding.btnSearch.setIcon(R.drawable.ic_filter_white)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().popBackStack()
        }

        lifecycleScope.launch {
            viewModel.getServicesHistoryList()
        }

        viewModel.data.observe(viewLifecycleOwner) { (data, isAdmin) ->
            setupRV(data, isAdmin)
        }

        viewModel.clientsList.observe(viewLifecycleOwner) {
            this.clientsList = it.filterNotNull()
        }

        viewModel.collaboratorList.observe(viewLifecycleOwner) {
            this.collaborators = it
        }

        viewModel.prosthesisList.observe(viewLifecycleOwner) {
            this.prothesisList = it
        }

        viewModel.viewState.observe(viewLifecycleOwner) { viewState ->
            binding.loading.isVisible = viewState is ServiceHistoryListViewState.Loading
            binding.recyclerView.isGone = viewState is ServiceHistoryListViewState.Loading
            binding.recyclerView.isVisible = viewState is ServiceHistoryListViewState.Success
        }

        binding.btnSearch.setOnClickListener {
            /*lifecycleScope.launch {
                viewModel.searchServiceHistory(
                    "",
                    "",
                    "YFuYOuy4TyF5Y23wHytF",
                    CustomerServiceType.SCHEDULED_SERVICE.name
                )
            }*/
            showDialog()
            //Toast.makeText(requireContext(), "clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRV(data: List<CustomerServiceData>, isAdmin: Boolean) {
        val mutableData: MutableList<CustomerServiceData> = if (data is MutableList) {
            data
        } else {
            data.toMutableList()
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        this.adapter = ServiceHistoryListAdapter(mutableData) {
            setNavigation(it, isAdmin)
        }
        binding.recyclerView.adapter = this.adapter
    }

    private fun setNavigation(data: CustomerServiceData, isAdmin: Boolean) {
        val action = ServiceHistoryListFragmentDirections.actionServiceHistoryListFragmentToCustomerServiceDetailsFragment(
            customerServiceDetails = data,
            isAdmin = isAdmin
        )
        findNavController().navigate(action)
    }

    private fun showDialog() {
        dialogBinding = CustomDialogSearchBinding.inflate(LayoutInflater.from(requireContext()))
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(dialogBinding.root)

        val alertDialog = alertDialogBuilder.create()

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var clientSelectedId = ""
        var collaboratorSelectedId = ""
        var customerServiceTypeSelected = ""
        var capillaryProthesisSelectedId = ""

        val customerServiceTypeOptions = resources.getStringArray(R.array.customerServiceType)
        val customerServiceTypeMap = mapOf(
            customerServiceTypeOptions[0] to CustomerServiceType.SCHEDULED_SERVICE,
            customerServiceTypeOptions[1] to CustomerServiceType.SCHEDULED_APPLICATION
        )
        val customerServiceArrayAdapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, customerServiceTypeOptions)
        dialogBinding.customerServiceType.setAdapter(customerServiceArrayAdapter)

        viewModel.clientsList.observe(viewLifecycleOwner) { clients ->
            val clientsArrayAdapter = ClientAdapter(requireContext(), clients.filterNotNull())
            dialogBinding.clientInput.setAdapter(clientsArrayAdapter)
        }

        viewModel.collaboratorList.observe(viewLifecycleOwner) {
            val collaboratorsArrayAdapter = CollaboratorAdapter(requireContext(), it)
            dialogBinding.collaboratorInput.setAdapter(collaboratorsArrayAdapter)
        }

        viewModel.prosthesisList.observe(viewLifecycleOwner) {
            val prothesisArrayAdapter = CapillaryProthesisAdapter(requireContext(), it)
            dialogBinding.prothesisType.setAdapter(prothesisArrayAdapter)
        }

        dialogBinding.clientInput.setOnItemClickListener { adapterView, view, position, l ->
            val selectedClient = viewModel.clientsList.value?.get(position)
            clientSelectedId = selectedClient?.id.toString()
        }

        dialogBinding.collaboratorInput.setOnItemClickListener { adapterView, view, position, l ->
            val selectedClient = viewModel.collaboratorList.value?.get(position)
            collaboratorSelectedId = selectedClient?.id.toString()
        }

        dialogBinding.prothesisType.setOnItemClickListener { adapterView, view, position, l ->
            val selectedClient = viewModel.prosthesisList.value?.get(position)
            capillaryProthesisSelectedId = selectedClient?.id.toString()
        }

        dialogBinding.customerServiceType.setOnItemClickListener { adapterView, view, position, l ->
            val selectedOption = customerServiceTypeOptions[position]
            customerServiceTypeSelected = customerServiceTypeMap[selectedOption].toString()
        }

        dialogBinding.customerServiceType.addTextChangedListener(textWatcher)
        dialogBinding.clientInput.addTextChangedListener(textWatcher)
        dialogBinding.collaboratorInput.addTextChangedListener(textWatcher)
        dialogBinding.prothesisType.addTextChangedListener(textWatcher)

        dialogBinding.btnClose.setOnClickListener {
            alertDialog.dismiss()
        }

        dialogBinding.btnAddFilter.setOnClickListener {
            lifecycleScope.launch {
                viewModel.searchServiceHistory(
                    clientSelectedId,
                    collaboratorSelectedId,
                    capillaryProthesisSelectedId,
                    customerServiceTypeSelected
                )
            }
            alertDialog.dismiss()
        }

        dialogBinding.btnCleanFilter.setOnClickListener {
            lifecycleScope.launch {
                viewModel.getServicesHistoryList()
            }
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            //validateFields()
        }
    }

    private fun validateFields() {

    }
}