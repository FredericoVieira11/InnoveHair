package com.example.innovehair.view.resetPassword

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.innovehair.R
import com.example.innovehair.databinding.FragmentResetPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ResetPasswordFragment : Fragment() {

    private lateinit var binding: FragmentResetPasswordBinding
    private val viewModel by viewModels<ResetPasswordViewModel>()
    private val args by navArgs<ResetPasswordFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().popBackStack()
        }
        binding.arrowIconBack.setIcon(R.drawable.ic_arrow_left_yellow)
        binding.arrowIconBack.setOnClickListener {
            findNavController().popBackStack()
        }

        if (args.userEmail.isNotEmpty()) {
            binding.emailInput.setText(args.userEmail)
            textWatcher.afterTextChanged(Editable.Factory.getInstance().newEditable(args.userEmail))
        }

        binding.emailInput.addTextChangedListener(textWatcher)

        binding.btnSubmit.setOnClickListener {
            //viewModel.sendResetPassword(binding.emailInput.text.toString())
            FirebaseAuth.getInstance().fetchSignInMethodsForEmail(binding.emailInput.text.toString())
                .addOnSuccessListener {
                    it.signInMethods
                }
                .addOnFailureListener {
                    it.stackTrace
                }
            /*FirebaseAuth.getInstance().sendPasswordResetEmail(binding.emailInput.text.toString())
                .addOnCompleteListener {
                    if (it.isSuccessful) {

                    }
                }
                .addOnFailureListener {
                    it.stackTrace
                }*/
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            binding.errorMessageTxt.isVisible = false
        }

        override fun afterTextChanged(p0: Editable?) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(1000)
                binding.btnSubmit.isEnabled = Patterns.EMAIL_ADDRESS.matcher(p0.toString()).matches()
                binding.errorMessageTxt.isVisible = !Patterns.EMAIL_ADDRESS.matcher(p0.toString()).matches()
            }
        }
    }
}