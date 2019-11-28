package com.pixelart.notedock.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pixelart.notedock.R
import com.pixelart.notedock.model.NoteModel
import kotlinx.android.synthetic.main.note_list_item.view.*

class NotesAdapter(private val onNoteClickListener: OnNoteClickListener) : RecyclerView.Adapter<NotesAdapter.NotesHolder>() {

    private var notes = ArrayList<NoteModel>()

    fun setNewData(newData: ArrayList<NoteModel>) {
        notes = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_list_item, parent, false)
        return NotesHolder(view, onNoteClickListener)
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onBindViewHolder(holder: NotesHolder, position: Int) {
        holder.bindData(notes[position])
    }


    class NotesHolder(itemView: View, private val onNoteClickListener: OnNoteClickListener): RecyclerView.ViewHolder(itemView) {

        fun bindData(note: NoteModel) {
            itemView.editTextNoteTitle.text = note.noteTitle
            itemView.textViewNotePreview.text = note.noteDescription

            itemView.setOnClickListener {
                onNoteClickListener.onNoteClick(note.uid)
            }
        }
    }

    interface OnNoteClickListener {
        fun onNoteClick(uid: String?)
    }
}