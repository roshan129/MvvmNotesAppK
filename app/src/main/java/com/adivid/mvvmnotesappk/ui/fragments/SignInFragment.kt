package com.adivid.mvvmnotesappk.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.adivid.mvvmnotesappk.R
import com.adivid.mvvmnotesappk.databinding.FragmentSignInBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment: Fragment(R.layout.fragment_sign_in) {

    private var _binding : FragmentSignInBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSignInBinding.bind(view)

        init()
        setUpOnClickListeners()

    }

    private fun init() {

    }

    private fun setUpOnClickListeners() {
        binding.imageButtonBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.buttonSignIn.setOnClickListener {

        }

        binding.textViewCreateAccount.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_createAccountFragment)
        }

    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


}