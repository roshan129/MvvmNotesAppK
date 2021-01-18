package com.adivid.mvvmnotesappk.ui.fragments

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.adivid.mvvmnotesappk.R
import com.adivid.mvvmnotesappk.databinding.FragmentSignInBinding
import com.adivid.mvvmnotesappk.ui.fragments.states.LoadingStates
import com.adivid.mvvmnotesappk.ui.viewmodels.AuthViewModel
import com.adivid.mvvmnotesappk.utils.showProgressBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment: Fragment(R.layout.fragment_sign_in) {

    private var _binding : FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSignInBinding.bind(view)

        init()
        setUpOnClickListeners()

    }

    private fun init() {
        authViewModel.userCreated.observe(viewLifecycleOwner, { userCreated ->
            if (userCreated) {
                Toast.makeText(requireContext(), "Logged In Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireActivity(), "Some Error Occurred", Toast.LENGTH_SHORT).show()
            }
        })

        authViewModel.uiStates.observe(viewLifecycleOwner, {uiState->
            when (uiState) {
                is LoadingStates.Loading -> {
                    showProgressBar(uiState.isLoading)
                }
                is LoadingStates.Error -> {
                    showProgressBar(false)
                    Toast.makeText(requireContext(), uiState.message, Toast.LENGTH_SHORT)
                        .show();
                }
            }
        })
    }

    private fun setUpOnClickListeners() {
        binding.imageButtonBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.buttonSignIn.setOnClickListener {
            val email = binding.editTextUsername.text.trim().toString()
            val password = binding.editTextPassword.text.trim().toString()
            if (validateFields(email, password)) {
                authViewModel.loginUser(email, password)
            }
        }

        binding.textViewCreateAccount.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_createAccountFragment)
        }

    }

    private fun validateFields(email: String, password: String): Boolean {
        when {
            email.isEmpty() -> {
                Toast.makeText(requireContext(), "Enter email", Toast.LENGTH_SHORT).show();
                return false
            }
            password.isEmpty() -> {
                Toast.makeText(requireContext(), "Enter Password", Toast.LENGTH_SHORT).show();
                return false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Toast.makeText(requireContext(), "Enter a valid email", Toast.LENGTH_SHORT).show();
                return false
            }
            password.length < 6 -> {
                Toast.makeText(
                    requireContext(),
                    "Password should be at least 6 characters long", Toast.LENGTH_SHORT
                ).show()
                return false
            }
            else -> return true
        }
    }

    private fun showProgressBar(b: Boolean) {
        binding.progressBar.showProgressBar(b)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


}