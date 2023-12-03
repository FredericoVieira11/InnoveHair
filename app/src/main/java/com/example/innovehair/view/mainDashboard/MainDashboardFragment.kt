package com.example.innovehair.view.mainDashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.innovehair.R
import com.example.innovehair.databinding.FragmentMainDashboardBinding
import com.google.firebase.auth.FirebaseAuth

class MainDashboardFragment : Fragment() {

    private lateinit var binding: FragmentMainDashboardBinding
    private val viewModel by viewModels<MainDashboardViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogout.setIcon(R.drawable.ic_logoff)

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val action = MainDashboardFragmentDirections.actionMainDashboardFragmentToLoginFragment()
            findNavController().navigate(action)
        }

        viewModel.data.observe(viewLifecycleOwner) { (name, image) ->
            Glide.with(this)
                .load(image)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.ic_profile_default)
                .placeholder(R.drawable.ic_profile_default)
                .skipMemoryCache(true)
                .into(binding.userImgProfile)

            binding.userNameTxt.text = name.ifEmpty {
                "- - -"
            }
        }

        binding.editUserDataCV.setOnClickListener {
            val action = MainDashboardFragmentDirections.actionMainDashboardFragmentToEditCollaboratorFragment()
            findNavController().navigate(action)
        }

        binding.customerServiceRegistrationBtn.setOnClickListener {
            val action = MainDashboardFragmentDirections.actionMainDashboardFragmentToCreateCustomerServiceFragment()
            findNavController().navigate(action)
        }

        binding.clientsListCV.setOnClickListener {
            val action = MainDashboardFragmentDirections.actionMainDashboardFragmentToClientsListFragment()
            findNavController().navigate(action)
        }

        binding.serviceHistoryCV.setOnClickListener {
            val action = MainDashboardFragmentDirections.actionMainDashboardFragmentToServiceHistoryListFragment()
            findNavController().navigate(action)
        }
    }
}