package com.example.innovehair.view.capillaryProsthesis

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.innovehair.R
import com.example.innovehair.databinding.CustomDialogAddProthesisBinding
import com.example.innovehair.databinding.FragmentCapillaryProsthesisBinding
import com.example.innovehair.service.models.CapillaryProsthesis
import com.example.innovehair.view.capillaryProsthesis.adapter.CapillaryProsthesisAdapter
import com.example.innovehair.view.capillaryProsthesis.adapter.CapillaryProsthesisOnClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CapillaryProsthesisFragment : Fragment(), CapillaryProsthesisOnClickListener {

    private lateinit var binding: FragmentCapillaryProsthesisBinding
    private lateinit var dialogBinding: CustomDialogAddProthesisBinding
    private val viewModel by viewModels<CapillaryProsthesisViewModel>()
    private lateinit var adapter: CapillaryProsthesisAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCapillaryProsthesisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setIcon(R.drawable.ic_arrow_left_yellow)
        binding.btnAdd.setIcon(R.drawable.ic_add_circle_white)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().popBackStack()
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnAdd.setOnClickListener {
            showProsthesisInputDialog(null)
        }

        viewModel.prosthesisList.observe(viewLifecycleOwner) {
            setupRV(it)
        }

        viewModel.viewState.observe(viewLifecycleOwner) { viewState ->
            binding.loadingGroup.isVisible = viewState is CapillaryProsthesisViewState.Loading
        }
    }

    private fun showProsthesisInputDialog(model: CapillaryProsthesis?) {
        dialogBinding = CustomDialogAddProthesisBinding.inflate(LayoutInflater.from(requireContext()))
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(dialogBinding.root)

        val alertDialog = alertDialogBuilder.create()

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.prothesisInput.setText(model?.name ?: "")
        if (model == null) {
            dialogBinding.priceInput.setText("")
        } else {
            val formatPrice = model.price?.toString()
            dialogBinding.priceInput.setText(formatPrice)
        }

        dialogBinding.btnSave.setOnClickListener {
            val prothesisInput = dialogBinding.prothesisInput.text.toString()
            val priceInput = dialogBinding.priceInput.text.toString().toDoubleOrNull()

            lifecycleScope.launch {
                if (priceInput != null && priceInput != 0.0 && prothesisInput.isNotBlank()) {
                    dialogBinding.errorMessageTxt.visibility = View.INVISIBLE
                    if (model == null) {
                        viewModel.createCapillaryProthesis(prothesisInput, priceInput)
                        alertDialog.dismiss()
                    } else {
                        viewModel.updateCapillaryProthesis(model.id, prothesisInput, priceInput)
                        alertDialog.dismiss()
                    }

                } else {
                    dialogBinding.errorMessageTxt.visibility = View.VISIBLE
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1000)
                        dialogBinding.errorMessageTxt.visibility = View.INVISIBLE
                    }
                }
            }
        }

        dialogBinding.btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun setupRV(data: List<CapillaryProsthesis>) {
        val mutableData: MutableList<CapillaryProsthesis> = if (data is MutableList) {
            data
        } else {
            data.toMutableList()
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        this.adapter = CapillaryProsthesisAdapter(mutableData, this)
        binding.recyclerView.adapter = this.adapter
    }

    override fun onEditClick(item: CapillaryProsthesis) {
        showProsthesisInputDialog(item)
    }

    override fun onRemoveClick(item: CapillaryProsthesis) {
        lifecycleScope.launch {
            viewModel.deleteCapillaryProthesis(item.id)
        }
    }
}