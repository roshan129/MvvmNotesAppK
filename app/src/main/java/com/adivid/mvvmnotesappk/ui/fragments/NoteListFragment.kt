package com.adivid.mvvmnotesappk.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.work.*
import com.adivid.mvvmnotesappk.R
import com.adivid.mvvmnotesappk.adapters.NoteListAdapter
import com.adivid.mvvmnotesappk.databinding.FragmentNoteListBinding
import com.adivid.mvvmnotesappk.mapper.NoteDtoMapper
import com.adivid.mvvmnotesappk.ui.viewmodels.NoteViewModel
import com.adivid.mvvmnotesappk.utils.Constants.TIME_INTERVAL
import com.adivid.mvvmnotesappk.utils.Constants.UNIQUE_WORK_NAME
import com.adivid.mvvmnotesappk.utils.FirebaseWorker
import com.adivid.mvvmnotesappk.utils.SharedPrefManager
import com.adivid.mvvmnotesappk.utils.afterTextChanged
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class NoteListFragment : Fragment(R.layout.fragment_note_list) {

    private var _binding: FragmentNoteListBinding? = null
    private val binding get() = _binding!!
    private lateinit var noteListAdapter: NoteListAdapter
    private val noteViewModel: NoteViewModel by viewModels()
    private var isSelectionMode = false
    private var backPressed: Long = 0

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var sharedPrefManager: SharedPrefManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUiMode()

        init()
        setUpOnClickListeners()

        observers()
    }

    private fun setUpUiMode() {
        if (sharedPrefManager.isNightModeOn()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            binding.imageButtonNightMode.setImageResource(R.drawable.ic_light_mode)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            binding.imageButtonNightMode.setImageResource(R.drawable.ic_dark_mode)
        }
    }

    private fun observers() {
        noteViewModel.allNotes.observe(viewLifecycleOwner, {
            /*binding.recyclerView.smoothScrollToPosition(0)*/
            noteListAdapter.submitList(it)
        })

        noteViewModel.searchNotes.observe(viewLifecycleOwner, {
            if (it != null && it.isNotEmpty()) {
                noteListAdapter.submitList(it)
                noteListAdapter.notifyDataSetChanged()
            } else {
                Timber.d("in else null")
            }
        })

        noteViewModel.syncNotes.observe(viewLifecycleOwner, { list ->
            if (list.isNotEmpty()) {
                sendDataToFirebase()
            }
        })

        noteViewModel.deleteNotesFromServer.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                sendDataToFirebase()
            }
        })

        noteViewModel.checkForOfflineData.observe(viewLifecycleOwner, {
            if (it && sharedPrefManager.toShowTransferDataDialog()) {
                showTransferDataDialog()
            } else {
                sharedPrefManager.showTransferDialogPref(false)
            }
        })
    }

    private fun init() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
        setUpRecyclerView()

        if (sharedPrefManager.toShowTransferDataDialog()) {
            checkForOfflineData()
        }
    }

    private fun setUpOnClickListeners() {
        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_noteListFragment_to_addEditNoteFragment)
        }

        binding.imageButtonDelete.setOnClickListener {
            showDeleteMultipleAlertDialog()
        }

        binding.imageButtonCancel.setOnClickListener {
            resetSelectionMode()
        }

        binding.imageButtonSearch.setOnClickListener {
            binding.relativeSearchLayout.isVisible = true
            binding.editTextSearch.requestFocus()
            showOrHideKeyBoard(true)
        }

        binding.imageButtonSearchCancel.setOnClickListener {
            if (binding.editTextSearch.text!!.isEmpty()) {
                binding.relativeSearchLayout.isVisible = false
                showOrHideKeyBoard(false)
            } else {
                binding.editTextSearch.setText("")
            }
        }

        binding.imageButtonSearchBack.setOnClickListener {
            binding.relativeSearchLayout.isVisible = false
            binding.editTextSearch.setText("")
            showOrHideKeyBoard(false)
        }

        binding.ivProfile.setOnClickListener {
            findNavController().navigate(R.id.action_noteListFragment_to_profileFragment)
        }

        binding.editTextSearch.afterTextChanged { s ->
            searchForNotes(s)
        }

        binding.imageButtonNightMode.setOnClickListener {
            if (sharedPrefManager.isNightModeOn()) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPrefManager.saveNightMode(false)
                binding.imageButtonNightMode.setImageResource(R.drawable.ic_dark_mode)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPrefManager.saveNightMode(true)
                binding.imageButtonNightMode.setImageResource(R.drawable.ic_light_mode)
            }
        }

    }

    private fun searchForNotes(s: CharSequence?) {
        noteViewModel.searchNotes(s.toString())
    }

    private fun setUpRecyclerView() {
        binding.recyclerView.apply {
            noteListAdapter = NoteListAdapter()
            adapter = noteListAdapter
            val staggeredGridLayoutManager = StaggeredGridLayoutManager(
                2, LinearLayoutManager.VERTICAL
            )
            layoutManager = staggeredGridLayoutManager
        }

        binding.recyclerView.adapter?.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                binding.recyclerView.smoothScrollToPosition(0)
            }
        })

        noteListAdapter.onItemClick = { note, i ->
            val hasCheckedItems = noteListAdapter.getSelectedCount() > 0

            if (hasCheckedItems && isSelectionMode) {
                noteListAdapter.toggleSelection(i)
                if (noteListAdapter.getSelectedCount() > 0) {
                    Timber.d("${noteListAdapter.getSelectedCount()}")
                    setItemSelectedText(noteListAdapter.getSelectedCount().toString())
                } else {
                    resetSelectionMode()
                }

            } else {
                val noteDto = NoteDtoMapper().mapFromEntity(note)
                val action =
                    NoteListFragmentDirections.actionNoteListFragmentToAddEditNoteFragment(noteDto)
                findNavController().navigate(action)
            }
        }

        noteListAdapter.onItemLongClick = { _, i ->
            isSelectionMode = true
            noteListAdapter.toggleSelection(i)
            val hasCheckedItems = noteListAdapter.getSelectedCount() > 0
            binding.cardLayoutBottom.isVisible = hasCheckedItems
            binding.cardLayoutTop.isVisible = hasCheckedItems
            setItemSelectedText(noteListAdapter.getSelectedCount().toString())
        }
    }

    private fun setItemSelectedText(selectedItems: String) {
        if (selectedItems == "1") {
            val text = selectedItems + "item selected"
            binding.tvItemsSelected.text = text
        } else {
            val text = selectedItems + "items selected"
            binding.tvItemsSelected.text = text
        }
    }

    private fun sendDataToFirebase() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val request = OneTimeWorkRequest.Builder(FirebaseWorker::class.java)
            .setConstraints(constraints)
            .build()
        val workManager = WorkManager.getInstance(requireContext().applicationContext)
        /*workManager.enqueueUniquePeriodicWork(UNIQUE_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)*/
        workManager.beginUniqueWork(UNIQUE_WORK_NAME, ExistingWorkPolicy.KEEP, request).enqueue()
    }

    private fun showDeleteMultipleAlertDialog() {
        AlertDialog.Builder(context).apply {
            setTitle("Confirm")
            setMessage("Are you sure you want to delete selected notes?")
            setPositiveButton("Yes") { _, _ ->
                val listArray = noteListAdapter.getSelectedNotes()
                noteViewModel.updateMultipleNotes(listArray)
                resetSelectionMode()
            }
            setNegativeButton("No") { _: DialogInterface?, _: Int ->

            }
            create().show()
        }
    }

    private fun checkForOfflineData() {
        noteViewModel.checkForOfflineData()

    }

    private fun showTransferDataDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Transfer Data")
            setMessage("Do you want to transfer data saved on this device?")
            setPositiveButton("Yes") { _, _ ->
                noteViewModel.syncOfflineNotes()
                sharedPrefManager.showTransferDialogPref(false)
            }
            setNegativeButton("No") { _, _ ->
                sharedPrefManager.showTransferDialogPref(false)
            }
            create()
            show()
        }
    }

    private fun resetSelectionMode() {
        isSelectionMode = false
        noteListAdapter.removeSelection()
        binding.cardLayoutBottom.isVisible = false
        binding.cardLayoutTop.isVisible = false

    }

    private fun showOrHideKeyBoard(boolean: Boolean) {
        val imm: InputMethodManager? =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        if (boolean) {
            imm!!.showSoftInput(binding.editTextSearch, InputMethodManager.SHOW_IMPLICIT)
            //imm!!.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        } else {
            imm!!.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (binding.relativeSearchLayout.isVisible) {
                binding.editTextSearch.setText("")
                binding.relativeSearchLayout.isVisible = false
                return
            }
            if (binding.cardLayoutTop.isVisible || binding.cardLayoutBottom.isVisible) {
                resetSelectionMode()
                return
            }
            if (backPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                requireActivity().finish()
            } else {
                Toast.makeText(requireContext(), "Press back again to exit!", Toast.LENGTH_SHORT)
                    .show()
            }
            backPressed = System.currentTimeMillis()
        }
    }

}
