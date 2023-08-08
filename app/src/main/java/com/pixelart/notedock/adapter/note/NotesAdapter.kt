package com.pixelart.notedock.adapter.note

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pixelart.notedock.R
import com.pixelart.notedock.databinding.NoteListItemBinding
import com.pixelart.notedock.model.NoteModel

class NotesAdapter(
    private val onNoteClickListener: OnNoteClickListener,
    private val onImageClickListener: OnImageClickListener,
) : RecyclerView.Adapter<NotesAdapter.NotesHolder>() {

    private var notes = ArrayList<NoteModel>()

    fun setNewData(newData: ArrayList<NoteModel>) {
        notes = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesHolder {
        val binding = NoteListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return NotesHolder(
            binding,
            onNoteClickListener,
            onImageClickListener,
        )
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onBindViewHolder(holder: NotesHolder, position: Int) {
        holder.bindData(notes[position])
    }

    class NotesHolder(
        private val binding: NoteListItemBinding,
        private val onNoteClickListener: OnNoteClickListener,
        private val onImageClickListener: OnImageClickListener,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindData(note: NoteModel) {
            binding.textViewNoteTitle.text = note.noteTitle
            binding.textViewNotePreview.text = note.noteDescription

            if (note.marked == true) {
                binding.imageViewPinned.setImageResource(R.drawable.ic_marked)
            } else {
                binding.imageViewPinned.setImageResource(R.drawable.ic_unmarked)
            }

            binding.textViewNoteTitle.setOnClickListener {
                onNoteClickListener.onNoteClick(note.uuid)
            }
            binding.textViewNotePreview.setOnClickListener {
                onNoteClickListener.onNoteClick(note.uuid)
            }
            binding.textViewNoteTitle.setOnLongClickListener {
                onNoteClickListener.onLongNoteClick(note.uuid)
                true
            }
            binding.textViewNotePreview.setOnLongClickListener {
                onNoteClickListener.onLongNoteClick(note.uuid)
                true
            }
            binding.imageViewPinned.setOnClickListener {
                onImageClickListener.onImageClick(note)
            }
        }
    }

    interface OnNoteClickListener {
        fun onNoteClick(noteUUID: String?)
        fun onLongNoteClick(noteUUID: String?)
    }

    interface OnImageClickListener {
        fun onImageClick(note: NoteModel)
    }
}
