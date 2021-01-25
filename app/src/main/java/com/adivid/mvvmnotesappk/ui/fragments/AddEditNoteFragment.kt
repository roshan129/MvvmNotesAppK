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
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.adivid.mvvmnotesappk.R
import com.adivid.mvvmnotesappk.databinding.FragmentAddEditNoteBinding
import com.adivid.mvvmnotesappk.db.Note
import com.adivid.mvvmnotesappk.model.domain.NoteDto
import com.adivid.mvvmnotesappk.ui.viewmodels.NoteViewModel
import com.adivid.mvvmnotesappk.utils.FirebaseWorker
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AddEditNoteFragment : Fragment(R.layout.fragment_add_edit_note) {

    private var _binding: FragmentAddEditNoteBinding? = null
    private val binding get() = _binding!!
    private val noteViewModel: NoteViewModel by viewModels()
    private val args by navArgs<AddEditNoteFragmentArgs>()
    private var isUpdate = false
    private var noteDto: NoteDto? = null
    @Inject lateinit var auth: FirebaseAuth

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

        init()
        setUpOnClickListeners()

    }

    private fun init() {
        binding.editTextBody.requestFocus()
        showOrHideKeyBoard(true)
        noteDto = args.Note
        noteDto?.let {
            val noteMessage = it.body
            binding.editTextBody.setText(noteMessage)
            binding.editTextBody.setSelection(noteMessage.length)
            isUpdate = true
        }
    }

    private fun setUpOnClickListeners() {
        binding.floatingActionButton.setOnClickListener {
            if(validateFields()){
                insertOrUpdateNote()
            }
        }

        binding.imageButtonBack.setOnClickListener {
            showOrHideKeyBoard(false)
            requireActivity().onBackPressed()
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
        val userId = if(auth.currentUser != null) auth.currentUser!!.uid else "0"
        val note = Note(body = body, userId = userId)
        if (isUpdate) {
            note.id = noteDto?.id
            note.isUpdated = 1
            note.isDataSent = 0
            note.documentId = noteDto?.docId
            noteViewModel.updateNote(note)
            Toast.makeText(requireContext(), "Updated", Toast.LENGTH_SHORT).show()
        } else {
            noteViewModel.insertNote(note)
        }
        Timber.d("inserted")
        /*findNavController().navigate(R.id.action_addEditNoteFragment_to_noteListFragment, null)*/
        requireActivity().onBackPressed()
    }

    private fun showOrHideKeyBoard(boolean: Boolean) {
        val imm: InputMethodManager? =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        if (boolean) {
            imm!!.showSoftInput(binding.editTextBody, InputMethodManager.SHOW_IMPLICIT)
            //imm!!.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        } else {
            //imm!!.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0)
            imm!!.hideSoftInputFromWindow(binding.editTextBody.windowToken, 0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}