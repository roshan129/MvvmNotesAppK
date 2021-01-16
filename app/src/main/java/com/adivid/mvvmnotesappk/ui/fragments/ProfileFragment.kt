package com.adivid.mvvmnotesappk.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.adivid.mvvmnotesappk.R
import com.adivid.mvvmnotesappk.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment: Fragment(R.layout.fragment_profile) {

    private var _binding :FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bind = FragmentProfileBinding.bind(view)
        _binding = bind

        init()
        setUpOnClickListeners()

    }

    private fun init() {

    }

    private fun setUpOnClickListeners() {
        binding.imageButtonBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.textViewSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
        }

    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}