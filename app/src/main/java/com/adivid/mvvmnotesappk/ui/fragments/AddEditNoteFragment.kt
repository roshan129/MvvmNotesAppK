package com.adivid.mvvmnotesappk.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.adivid.mvvmnotesappk.R
import com.adivid.mvvmnotesappk.databinding.FragmentAddEditNoteBinding
import com.adivid.mvvmnotesappk.db.Note
import com.adivid.mvvmnotesappk.ui.viewmodels.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class AddEditNoteFragment :Fragment(R.layout.fragment_add_edit_note) {

    private var _binding : FragmentAddEditNoteBinding? = null
    private val binding get() = _binding!!
    private val noteViewModel: NoteViewModel by viewModels()
    private val args by navArgs<AddEditNoteFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddEditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       /* requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)*/

        binding.editTextBody.requestFocus()
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)

        binding.floatingActionButton.setOnClickListener {
            insertNote()
        }

        val s = args.Note
        s?.let {
            binding.editTextBody.setText(s)
            binding.editTextBody.setSelection(s.length)
        }

    }

    private fun insertNote() {
        val body  = binding.editTextBody.text.toString().trim()
        val note = Note(body)
        noteViewModel.insertNote(note)
        Timber.d("inserted")
        Toast.makeText(requireContext(), "Inserted", Toast.LENGTH_SHORT).show()
        /*findNavController().navigate(R.id.action_addEditNoteFragment_to_noteListFragment, null)*/
        requireActivity().onBackPressed()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}