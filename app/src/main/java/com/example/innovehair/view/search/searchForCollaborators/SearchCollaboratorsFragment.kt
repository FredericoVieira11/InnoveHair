package com.example.innovehair.view.search.searchForCollaborators

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.innovehair.R
import com.example.innovehair.databinding.FragmentSearchBinding
import com.example.innovehair.view.collaboratorsList.CollaboratorData
import com.example.innovehair.view.collaboratorsList.adapter.CollaboratorsListAdapter
import kotlinx.coroutines.launch

class SearchCollaboratorsFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: CollaboratorsListAdapter
    private val viewModel by viewModels<SearchCollaboratorsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
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

        viewModel.collaboratorsList.observe(viewLifecycleOwner) {
            setupRV(it)
        }
    }

    private fun setupRV(data: List<CollaboratorData>) {
        val mutableData: MutableList<CollaboratorData> = if (data is MutableList) {
            data
        } else {
            data.toMutableList()
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        this.adapter = CollaboratorsListAdapter(mutableData, requireContext()) { itemData ->
            setNavigation(itemData)
        }
        binding.recyclerView.adapter = this.adapter
    }

    private fun setNavigation(collaborator: CollaboratorData) {
        findNavController().previousBackStackEntry?.savedStateHandle?.set("collaborator", collaborator)
        findNavController().popBackStack()
    }
}