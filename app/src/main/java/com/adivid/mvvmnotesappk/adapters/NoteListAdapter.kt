package com.adivid.mvvmnotesappk.adapters

import android.content.Context
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.util.forEach
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.adivid.mvvmnotesappk.R
import com.adivid.mvvmnotesappk.db.Note
import com.adivid.mvvmnotesappk.utils.Utils
import com.google.android.material.checkbox.MaterialCheckBox
import timber.log.Timber
import java.util.*

class NoteListAdapter : RecyclerView.Adapter<NoteListAdapter.NoteViewHolder>() {

    private lateinit var context: Context
    var onItemClick: ((Note, Int) -> Unit)? = null
    var onItemLongClick: ((Note, Int) -> Unit)? = null

    private var selectedItemIds: SparseBooleanArray = SparseBooleanArray()

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewBody: TextView = itemView.findViewById(R.id.textViewBody)
        val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
        val checkBox: MaterialCheckBox = itemView.findViewById(R.id.checkbox)
        val cardRelativeLayout: RelativeLayout = itemView.findViewById(R.id.cardRelativeLayout)

        init {
            itemView.setOnClickListener {
                val noteItem = differ.currentList[adapterPosition]
                noteItem?.let {
                    onItemClick?.invoke(noteItem, adapterPosition)
                }
            }

            itemView.setOnLongClickListener {
                val noteItem = differ.currentList[adapterPosition]
                noteItem.let {
                    onItemLongClick?.invoke(noteItem, adapterPosition)
                }
                return@setOnLongClickListener true
            }

        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<Note>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        context = parent.context
        return NoteViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_list,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = differ.currentList[position]
        holder.textViewBody.text = note.body
        holder.textViewDate.text = note.createdDateFormatted
        if (selectedItemIds.get(position)) {
            setUpBGColor(holder, R.color.cardSelectedColor)
            //holder.checkBox.isVisible = true
            //holder.checkBox.isChecked = true
        } else {
            holder.checkBox.isVisible = false
            setUpBGColor(holder, R.color.cardColor)
        }

       /* when (Utils.getRandomNumber()) {
            1 -> setUpBGColor(holder, R.color.notesColorBrown)
            2 -> setUpBGColor(holder, R.color.notesColorOrange)
            3 -> setUpBGColor(holder, R.color.notesColorGreen)
            4 -> setUpBGColor(holder, R.color.notesColorPurple)
            5 -> setUpBGColor(holder, R.color.notesColorRedLight)
            6 -> setUpBGColor(holder, R.color.notesColorAmber)
        }*/

    }

    override fun getItemCount(): Int = differ.currentList.size

    private fun setUpBGColor(holder: NoteViewHolder, color: Int) {
        holder.cardRelativeLayout.setBackgroundColor(ContextCompat.getColor(context, color))
    }

    /** ******************************************************************** **/

    //functions relating to sparse boolean array

    fun toggleSelection(position: Int) {
        Timber.d("toggleSelection: ${selectedItemIds.get(position)}")
        selectView(position, selectedItemIds.get(position))
    }

    fun removeSelection() {
        selectedItemIds = SparseBooleanArray()
        notifyDataSetChanged()
    }

    private fun selectView(position: Int, value: Boolean) {
        if (value) {
            Timber.d("selectView: inside if")
            //selectedItemIds.put(position, value)

            selectedItemIds.delete(position)
        } else {
            Timber.d("selectView: inside else")
            //selectedItemIds.delete(position)

            selectedItemIds.put(position, !value)
        }
        notifyDataSetChanged()
    }

    fun getSelectedCount(): Int {
        return selectedItemIds.size()
    }

    fun getSelectedNotes(): List<Note> {
        val list = differ.currentList
        val array = selectedItemIds
        val listToReturn: MutableList<Note> = mutableListOf()
        array.forEach { i, boolean ->
            if (boolean) {
                val note = list[i]
                listToReturn.add(note)
            }
        }
        return listToReturn
    }

    fun getSelectedIds(): SparseBooleanArray {
        return selectedItemIds
    }


}