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
import androidx.navigation.fragment.navArgs
import com.adivid.mvvmnotesappk.R
import com.adivid.mvvmnotesappk.databinding.FragmentAddEditNoteBinding
import com.adivid.mvvmnotesappk.db.Note
import com.adivid.mvvmnotesappk.model.domain.NoteDto
import com.adivid.mvvmnotesappk.ui.viewmodels.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class AddEditNoteFragment : Fragment(R.layout.fragment_add_edit_note) {

    private var _binding: FragmentAddEditNoteBinding? = null
    private val binding get() = _binding!!
    private val noteViewModel: NoteViewModel by viewModels()
    private val args by navArgs<AddEditNoteFragmentArgs>()
    private var isUpdate = false
    private var noteDto: NoteDto? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)*/
        binding.editTextBody.requestFocus()
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)

        binding.floatingActionButton.setOnClickListener {
            if(validateFields()){
                insertOrUpdateNote()
            }
        }

        noteDto = args.Note
        noteDto?.let {
            val noteMessage = it.body
            binding.editTextBody.setText(noteMessage)
            binding.editTextBody.setSelection(noteMessage.length)
            isUpdate = true
        }

    }

    private fun validateFields(): Boolean {
        val body = binding.editTextBody.text.toString().trim()
        return if(body.isEmpty()){
            Toast.makeText(requireContext(), "Please add a note!", Toast.LENGTH_SHORT).show();
            false
        }else{
            true
        }
    }

    private fun insertOrUpdateNote() {
        val body = binding.editTextBody.text.toString().trim()
        val note = Note(body)
        if (isUpdate) {
            note.id = this.noteDto?.id
            noteViewModel.updateNote(note)
            Toast.makeText(requireContext(), "Updated", Toast.LENGTH_SHORT).show()
        } else {
            noteViewModel.insertNote(note)
        }
        Timber.d("inserted")
        /*findNavController().navigate(R.id.action_addEditNoteFragment_to_noteListFragment, null)*/
        requireActivity().onBackPressed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}