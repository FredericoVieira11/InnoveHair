package com.example.innovehair.view.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.innovehair.databinding.FragmentSplashBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding
    private val viewModel by viewModels<SplashViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.error.observe(viewLifecycleOwner) {
            val action = SplashFragmentDirections.actionSplashFragmentToLoginFragment()
            findNavController().navigate(action)
        }

        viewModel.isAdmin.observe(viewLifecycleOwner) { isAdmin ->
            if (isAdmin == true) {
                val action = SplashFragmentDirections.actionSplashFragmentToManagerDashboardFragment(
                    refreshLogin = false
                )
                findNavController().navigate(action)
            } else {
                val action = SplashFragmentDirections.actionSplashFragmentToMainDashboardFragment()
                findNavController().navigate(action)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            viewModel.checkUserSession()
        }
    }
}