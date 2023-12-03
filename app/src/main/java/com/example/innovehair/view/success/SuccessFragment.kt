package com.example.innovehair.view.success

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.innovehair.databinding.FragmentSuccessBinding

class SuccessFragment : Fragment() {

    private lateinit var binding: FragmentSuccessBinding
    private val args: SuccessFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnOk.setOnClickListener {
            when(args.screen) {
                Screens.MAIN_DASHBOARD.name -> {
                    val action = SuccessFragmentDirections.actionSuccessFragmentToMainDashboardFragment()
                    findNavController().navigate(action)
                }
                Screens.MANAGER_DASHBOARD.name -> {
                    val action = SuccessFragmentDirections.actionSuccessFragmentToManagerDashboardFragment(refreshLogin = false)
                    findNavController().navigate(action)
                }
                Screens.CREATE_NEW_COLLABORATOR.name -> {
                    val action = SuccessFragmentDirections.actionSuccessFragmentToManagerDashboardFragment(refreshLogin = true)
                    findNavController().navigate(action)
                }
                Screens.CREATE_CUSTOMER_SERVICE.name -> {
                    val action = SuccessFragmentDirections.actionSuccessFragmentToMainDashboardFragment()
                    findNavController().navigate(action)
                }
                Screens.EDIT_COLLABORATOR.name -> {
                    val action = SuccessFragmentDirections.actionSuccessFragmentToMainDashboardFragment()
                    findNavController().navigate(action)
                }
                Screens.CREATE_APPLICATION.name -> {
                    findNavController().popBackStack()
                }
            }
        }
    }
}

enum class Screens(private val stringValue: String) {
    CREATE_NEW_COLLABORATOR("CREATE_NEW_COLLABORATOR"),
    CREATE_CUSTOMER_SERVICE("CREATE_CUSTOMER_SERVICE"),
    EDIT_COLLABORATOR("EDIT_COLLABORATOR"),
    CREATE_APPLICATION("CREATE_APPLICATION"),
    MAIN_DASHBOARD("MAIN_DASHBOARD"),
    MANAGER_DASHBOARD("MANAGER_DASHBOARD");

    override fun toString(): String {
        return stringValue
    }
}