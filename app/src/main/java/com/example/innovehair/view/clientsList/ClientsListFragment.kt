package com.example.innovehair.view.clientsList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.innovehair.R
import com.example.innovehair.databinding.FragmentClientsListBinding
import com.example.innovehair.service.models.respondeModels.ClientResponse
import com.example.innovehair.view.clientsList.adapter.ClientsListAdapter

class ClientsListFragment : Fragment() {

    private lateinit var binding: FragmentClientsListBinding
    private val viewModel by viewModels<ClientsListViewModel>()
    private lateinit var adapter: ClientsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClientsListBinding.inflate(inflater, container, false)
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

        viewModel.loadData()

        viewModel.data.observe(viewLifecycleOwner) { (data, isAdmin) ->
            setupRV(data, isAdmin)
        }
    }

    private fun setupRV(data: List<ClientResponse>, isAdmin: Boolean) {
        val mutableData: MutableList<ClientResponse> = if (data is MutableList) {
            data
        } else {
            data.toMutableList()
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        this.adapter = ClientsListAdapter(mutableData) {
            setNavigation(it, isAdmin)
        }
        binding.recyclerView.adapter = this.adapter
    }

    private fun setNavigation(client: ClientResponse, isAdmin: Boolean) {
        val action = ClientsListFragmentDirections.actionClientsListFragmentToClientDetailsFragment(
            clientData = client,
            isAdmin = isAdmin
        )
        findNavController().navigate(action)
    }
}