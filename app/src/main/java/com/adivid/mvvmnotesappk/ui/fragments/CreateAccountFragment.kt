package com.adivid.mvvmnotesappk.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.adivid.mvvmnotesappk.R
import com.adivid.mvvmnotesappk.databinding.FragmentCreateAccountBinding
import com.adivid.mvvmnotesappk.ui.fragments.states.LoadingStates
import com.adivid.mvvmnotesappk.ui.viewmodels.AuthViewModel
import com.adivid.mvvmnotesappk.utils.SharedPrefManager
import com.adivid.mvvmnotesappk.utils.showProgressBar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateAccountFragment : Fragment(R.layout.fragment_create_account) {

    private var _binding: FragmentCreateAccountBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    //@Inject lateinit var sharedPrefManager: SharedPrefManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCreateAccountBinding.bind(view)

        init()
        setUpOnClickListeners()
    }

    private fun init() {
        authViewModel.userCreated.observe(viewLifecycleOwner, { userCreated ->
            if (userCreated) {
                Toast.makeText(requireContext(), "User Created", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_createAccountFragment_to_profileFragment)

            } else {
                Toast.makeText(requireActivity(), "Some Error Occurred", Toast.LENGTH_SHORT).show()
            }
        })

        authViewModel.progressBar.observe(viewLifecycleOwner, {
            showProgressBar(it)
        })

        authViewModel.uiStates.observe(viewLifecycleOwner, { uiState ->
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
        binding.textViewHaveAcc.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.buttonCreate.setOnClickListener {
            registerUser()
        }

    }

    private fun registerUser() {
        val email = binding.editTextUsername.text.trim().toString()
        val password = binding.editTextPassword.text.trim().toString()
        if (validateFields(email, password)) {
            authViewModel.registerUser(email, password)
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