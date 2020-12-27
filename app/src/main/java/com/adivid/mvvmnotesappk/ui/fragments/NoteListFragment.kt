package com.adivid.mvvmnotesappk.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.adivid.mvvmnotesappk.R
import com.adivid.mvvmnotesappk.adapters.NoteListAdapter
import com.adivid.mvvmnotesappk.databinding.FragmentNoteListBinding
import com.adivid.mvvmnotesappk.ui.viewmodels.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class NoteListFragment : Fragment(R.layout.fragment_note_list) {

    private var _binding: FragmentNoteListBinding? = null
    private val binding get() = _binding!!
    private lateinit var noteListAdapter: NoteListAdapter
    private val noteViewModel: NoteViewModel by viewModels()
    //abcd
    //pqrs


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoteListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        setUpOnClickListeners()

    }

    private fun init() {
        setUpRecyclerView()

        noteViewModel.allNotes.observe(viewLifecycleOwner, Observer {
            /*binding.recyclerView.smoothScrollToPosition(0)*/
            noteListAdapter.submitList(it)
        })

    }

    private fun setUpRecyclerView() {
        binding.recyclerView.apply {
            noteListAdapter = NoteListAdapter()
            adapter = noteListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.recyclerView.adapter?.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                binding.recyclerView.smoothScrollToPosition(0)
            }
        })

        noteListAdapter.onItemClick = {
            val action = NoteListFragmentDirections.actionNoteListFragmentToAddEditNoteFragment(it.body)
            findNavController().navigate(action)
        }
    }

    private fun setUpOnClickListeners() {
        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_noteListFragment_to_addEditNoteFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
