package com.pixelart.notedock.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pixelart.notedock.R
import com.pixelart.notedock.model.FolderModel
import kotlinx.android.synthetic.main.folder_list_item.view.*

class FoldersAdapter(private val onFolderClickListener: OnFolderClickListener) : RecyclerView.Adapter<FoldersAdapter.FoldersHolder>() {

    private var folders = ArrayList<FolderModel>()

    fun setNewData(newData: ArrayList<FolderModel>) {
        folders = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoldersHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.folder_list_item, parent, false)
        return FoldersHolder(view, onFolderClickListener)
    }

    override fun getItemCount(): Int {
        return folders.size
    }

    override fun onBindViewHolder(holder: FoldersHolder, position: Int) {
        holder.bindData(folders[position])
    }


    class FoldersHolder(itemView: View, private val onFolderClickListener: OnFolderClickListener): RecyclerView.ViewHolder(itemView) {

        fun bindData(folder: FolderModel) {
            itemView.textViewFolderName.text = folder.name
            itemView.textViewNotesCount.text = folder.notesCount.toString()

            itemView.setOnClickListener {
                onFolderClickListener.onFolderClick(folder.uid, folder.name)
            }

            itemView.setOnLongClickListener {
                onFolderClickListener.onFolderLongPress(folder.uid)
                true
            }
        }
    }

    interface OnFolderClickListener {
        fun onFolderClick(uid: String?, name: String?)
        fun onFolderLongPress(uid: String?)
    }
}