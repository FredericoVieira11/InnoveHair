package com.example.innovehair.view.collaboratorsList

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
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.innovehair.R
import com.example.innovehair.databinding.FragmentCollaboratorsListBinding
import com.example.innovehair.view.collaboratorsList.adapter.CollaboratorsListAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CollaboratorsListFragment : Fragment() {

    private lateinit var binding: FragmentCollaboratorsListBinding
    private val viewModel by viewModels<CollaboratorsListViewModel>()
    private lateinit var adapter: CollaboratorsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCollaboratorsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setIcon(R.drawable.ic_arrow_left_yellow)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().popBackStack()
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.viewState.observe(viewLifecycleOwner) {
            binding.loading.isVisible = it is CollaboratorsListViewState.Loading
        }

        lifecycleScope.launch {
            viewModel.getCollaboratorsList()
        }

        viewModel.collaboratorsList.observe(viewLifecycleOwner) {
            setupRV(it)
            //CoroutineScope(Dispatchers.Main).launch {
            //    delay(1000)
            //    binding.emptyListTxt.isVisible = it.isEmpty()
            //}
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

    private fun setNavigation(itemData: CollaboratorData) {
        val action = CollaboratorsListFragmentDirections.actionCollaboratorsListFragmentToCollaboratorDetailsFragment(
            collaboratorData = itemData
        )
        findNavController().navigate(action)
    }
}