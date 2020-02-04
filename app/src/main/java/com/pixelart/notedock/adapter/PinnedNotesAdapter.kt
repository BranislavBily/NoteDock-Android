package com.pixelart.notedock.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pixelart.notedock.R
import com.pixelart.notedock.model.NoteModel
import kotlinx.android.synthetic.main.note_list_item.view.*

class PinnedNotesAdapter(private val onNoteClickListener: OnNoteClickListener,
                         private val onImageClickListener: OnImageClickListener) : RecyclerView.Adapter<PinnedNotesAdapter.NotesHolder>() {

    private var notes = ArrayList<NoteModel>()

    fun setNewData(newData: ArrayList<NoteModel>) {
        notes = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_list_item, parent, false)
        return NotesHolder(view, onNoteClickListener, onImageClickListener)
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onBindViewHolder(holder: NotesHolder, position: Int) {
        holder.bindData(notes[position])
    }


    class NotesHolder(itemView: View,
                      private val onNoteClickListener: OnNoteClickListener,
                      private val onImageClickListener: OnImageClickListener
                      ): RecyclerView.ViewHolder(itemView) {

        fun bindData(note: NoteModel) {
            itemView.editTextNoteTitle.text = note.noteTitle
            itemView.textViewNotePreview.text = note.noteDescription

            itemView.imageViewPinned.setImageResource(R.drawable.ic_pinned)

            itemView.setOnClickListener {view ->
                if(view == itemView.imageViewPinned) {
                    onImageClickListener.onImageClick(note.uuid)
                } else {
                    onNoteClickListener.onNoteClick(note.uuid)
                }
            }
        }
    }

    interface OnNoteClickListener {
        fun onNoteClick(noteUUID: String?)
    }

    interface OnImageClickListener {
        fun onImageClick(noteUUID: String?)
    }
}