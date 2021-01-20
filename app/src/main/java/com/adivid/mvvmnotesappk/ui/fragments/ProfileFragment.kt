package com.adivid.mvvmnotesappk.ui.fragments

import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.adivid.mvvmnotesappk.R
import com.adivid.mvvmnotesappk.databinding.FragmentProfileBinding
import com.adivid.mvvmnotesappk.utils.Constants.KEY_EMAIL
import com.adivid.mvvmnotesappk.utils.SharedPrefManager
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment: Fragment(R.layout.fragment_profile) {

    private var _binding :FragmentProfileBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sharedPrefManager: SharedPrefManager

    @Inject lateinit var sharedPreferencs: SharedPreferences

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bind = FragmentProfileBinding.bind(view)
        _binding = bind

        init()
        setUpOnClickListeners()

    }

    private fun init() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
        if(sharedPrefManager.getEmail().isNotEmpty())
            binding.textViewSignIn.text = sharedPrefManager.getEmail()

        val s =sharedPreferencs.getString(KEY_EMAIL, "");
        Timber.d("sharedpref email: $s")
    }

    private fun setUpOnClickListeners() {
        binding.imageButtonBack.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_noteListFragment)
        }

        binding.textViewSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
        }

        binding.textViewSignIn.setOnLongClickListener {
            showSignOutAlertDialog()
            return@setOnLongClickListener true
        }

    }

    private fun showSignOutAlertDialog() {
        AlertDialog.Builder(context) .apply {
            setTitle("Sign Out")
            setMessage("Are you sure you want to log out?")
            setPositiveButton("Yes") { _, _ ->
                auth.signOut()
                if (auth.currentUser == null) binding.textViewSignIn.text = "Sign In or Sign Up"
                sharedPrefManager.clearPrefs()
            }
            setNegativeButton("No"){_,_->

            }
            create().show()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            findNavController().navigate(R.id.action_profileFragment_to_noteListFragment)
        }
    }

}