package com.adivid.mvvmnotesappk.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.adivid.mvvmnotesappk.R
import com.adivid.mvvmnotesappk.databinding.FragmentSignInBinding
import com.adivid.mvvmnotesappk.ui.viewmodels.AuthViewModel
import com.adivid.mvvmnotesappk.utils.*
import com.adivid.mvvmnotesappk.utils.Constants.RC_SIGN_IN
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SignInFragment : Fragment(R.layout.fragment_sign_in) {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient
    @Inject
    lateinit var sharedPrefManager: SharedPrefManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSignInBinding.bind(view)

        setUpOnClickListeners()
        observers()
        setUpGoogleSignIn()

    }

    private fun observers() {
        authViewModel.userCreated1.observe(viewLifecycleOwner, {
            response ->
            Timber.d("inside confirm")
            when(response) {
                is NetworkResponse.Success -> {
                    hideProgressBar()
                    response.data?.let {
                        Timber.d("data%s", it)
                        if(it) afterSignedInSuccessfully()
                        else showToast(getString(R.string.error_occurred))
                    }
                }
                is NetworkResponse.Error -> {
                    hideProgressBar()
                    showToast(response.message!!)
                }
                is NetworkResponse.Loading -> {
                    showProgressBar()
                }
            }
        })

        authViewModel.googleSignIn.observe(viewLifecycleOwner, {
            if (it != null) afterSignedInSuccessfully()
        })

    }

    private fun afterSignedInSuccessfully() {
        sharedPrefManager.showTransferDialogPref(true)
        showToast(getString(R.string.log_in_success))
        findNavController().navigate(R.id.action_signInFragment_to_profileFragment)
        fetchDataFromFirebase()
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

        binding.googleSignInButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        binding.textViewForgotPassword.setOnClickListener {
            showToast(getString(R.string.to_implement))
        }
    }

    private fun validateFields(email: String, password: String): Boolean {
        when {
            email.isEmpty() -> {
                Toast.makeText(requireContext(), getString(R.string.enter_email), Toast.LENGTH_SHORT).show()
                return false
            }
            password.isEmpty() -> {
                Toast.makeText(requireContext(), getString(R.string.enter_password), Toast.LENGTH_SHORT).show()
                return false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Toast.makeText(requireContext(), getString(R.string.enter_valid_email), Toast.LENGTH_SHORT).show()
                return false
            }
            password.length < 6 -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.password_characters), Toast.LENGTH_SHORT
                ).show()
                return false
            }
            else -> return true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.d("inside onActivityResult")
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Timber.d("firebaseAuthWithGoogle: ${account.id}")
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Timber.d("google sign in failed: $e")
                Toast.makeText(requireContext(), "Some Error Occurred", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        authViewModel.firebaseAuthWithGoogle(idToken)

    }

    private fun setUpGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
    }

    private fun fetchDataFromFirebase() {
        authViewModel.fetchDataFromFirebase()
    }

    private fun showProgressBar() {
        binding.progressBar.showProgressBar()
    }

    private fun hideProgressBar() {
        binding.progressBar.hideProgressBar()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


}