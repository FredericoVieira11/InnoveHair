package com.example.innovehair.view.managerDashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.innovehair.R
import com.example.innovehair.databinding.FragmentManagerDashboardBinding
import com.example.innovehair.sharedPreferences.SharedPreferencesManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ManagerDashboardFragment : Fragment() {

    private lateinit var binding: FragmentManagerDashboardBinding
    private val viewModel by viewModels<ManagerDashboardViewModel>()
    private val args: ManagerDashboardFragmentArgs by navArgs()
    private lateinit var sharedPreferences: SharedPreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentManagerDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = SharedPreferencesManager(requireContext())

        binding.btnLogout.setIcon(R.drawable.ic_logoff)

        binding.btnLogout.setOnClickListener {
            sharedPreferences.clearCredentials()
            FirebaseAuth.getInstance().signOut()
            val action = ManagerDashboardFragmentDirections.actionManagerDashboardFragmentToLoginFragment()
            findNavController().navigate(action)
        }

        lifecycleScope.launch {
            viewModel.getUserData(
                args.refreshLogin,
                requireContext()
            )
        }

        viewModel.data.observe(viewLifecycleOwner) { name ->
            binding.userNameTxt.text = name.ifEmpty {
                "- - -"
            }
        }

        binding.createNewCollaboratorCV.setOnClickListener {
            val action = ManagerDashboardFragmentDirections.actionManagerDashboardFragmentToCreateNewCollaboratorFragment()
            findNavController().navigate(action)
        }

        binding.listOfCollaboratorsCV.setOnClickListener {
            val action = ManagerDashboardFragmentDirections.actionManagerDashboardFragmentToCollaboratorsListFragment()
            findNavController().navigate(action)
        }

        binding.prothesisListCV.setOnClickListener {
            val action = ManagerDashboardFragmentDirections.actionManagerDashboardFragmentToCapillaryProsthesisFragment()
            findNavController().navigate(action)
        }

        binding.clientsListCV.setOnClickListener {
            val action = ManagerDashboardFragmentDirections.actionManagerDashboardFragmentToClientsListFragment()
            findNavController().navigate(action)
        }

        binding.serviceHistoryCV.setOnClickListener {
            val action = ManagerDashboardFragmentDirections.actionManagerDashboardFragmentToServiceHistoryListFragment()
            findNavController().navigate(action)
        }
    }
}