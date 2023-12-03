package com.example.innovehair.view.login

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.innovehair.databinding.FragmentLoginBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {

    //private val roomDb: AppDatabase by lazy {
    //    AppDatabase.getInstance(requireContext())
    //}
    //private val roomDb = AppDatabase.getInstance(requireContext())
    //private val viewModelFactory = LoginViewModelFactory(roomDb)
    //private val viewModel by viewModels<LoginViewModel>{
    //    //LoginViewModelFactory(roomDb.userCredentialsDao())
    //    viewModelFactory
    //}

    //private var roomDb: AppDatabase? = null
    //private var viewModelFactory: LoginViewModelFactory? = null
    //private var viewModel: LoginViewModel? = null

    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var binding: FragmentLoginBinding
    private lateinit var database: DatabaseReference

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //roomDb = AppDatabase.getInstance(requireContext())
        //viewModelFactory = LoginViewModelFactory(roomDb!!)
        //viewModel = ViewModelProvider(this, viewModelFactory!!).get(LoginViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.edtEmail.addTextChangedListener(textWatcher)
        binding.edtPassword.addTextChangedListener(textWatcher)

        binding.btnLogin.setOnClickListener {
            database = Firebase.database.reference

            val emailInput = binding.edtEmail.text.toString()
            val passwordInput = binding.edtPassword.text.toString()
            viewModel.signInUser(requireContext(), emailInput, passwordInput)
        }

        binding.recoverPassword.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToResetPasswordFragment(
                userEmail = binding.edtEmail.text.toString()
            )
            findNavController().navigate(action)
        }

        viewModel.viewState.observe(viewLifecycleOwner) { viewState ->
            binding.group.isVisible = viewState is LoginViewState.Loading
            binding.errorMessageTxt.isVisible = viewState is LoginViewState.Error
            if (viewState is LoginViewState.Success) {
                setNavigation()
            }
        }
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
        val emailInput = binding.edtEmail.text.toString()
        val passwordInput = binding.edtPassword.text.toString()

        val emailValidation = Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()
        val passwordValidation = passwordInput.length >= 8 && passwordInput.any { it.isDigit() } && passwordInput.any { it.isUpperCase() }

        binding.btnLogin.isEnabled = emailValidation && passwordValidation
    }

    private fun setNavigation() {
        viewModel.dataFromFirestore.observe(viewLifecycleOwner) { user ->
            if (user.isAdmin == true) {
                val action = LoginFragmentDirections.actionLoginFragmentToManagerDashboardFragment(
                    refreshLogin = false
                )
                findNavController().navigate(action)
            } else {
                user.name.let {
                    val action = LoginFragmentDirections.actionLoginFragmentToMainDashboardFragment()
                    findNavController().navigate(action)
                }
            }
        }
    }
}