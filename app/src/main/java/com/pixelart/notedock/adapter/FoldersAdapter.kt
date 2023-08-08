package com.pixelart.notedock.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pixelart.notedock.databinding.FolderListItemBinding
import com.pixelart.notedock.model.FolderModel

class FoldersAdapter(private val onFolderClickListener: OnFolderClickListener) :
    RecyclerView.Adapter<FoldersAdapter.FoldersHolder>() {

    private var folders = ArrayList<FolderModel>()

    fun setNewData(newData: ArrayList<FolderModel>) {
        folders = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoldersHolder {
        val binding = FolderListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return FoldersHolder(binding, onFolderClickListener)
    }

    override fun getItemCount(): Int {
        return folders.size
    }

    override fun onBindViewHolder(holder: FoldersHolder, position: Int) {
        holder.bindData(folders[position])
    }

    class FoldersHolder(
        private val binding: FolderListItemBinding,
        private val onFolderClickListener: OnFolderClickListener,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(folder: FolderModel) {
            binding.textViewFolderName.text = folder.name
            binding.textViewNotesCount.text = folder.notesCount.toString()

            itemView.setOnClickListener {
                onFolderClickListener.onFolderClick(folder.uid, folder.name)
            }

            itemView.setOnLongClickListener {
                onFolderClickListener.onFolderLongPress(folder.uid, folder.name)
                true
            }
        }
    }

    interface OnFolderClickListener {
        fun onFolderClick(uid: String?, name: String?)
        fun onFolderLongPress(uid: String?, folderName: String?)
    }
}
