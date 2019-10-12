package com.pixelart.notedock.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pixelart.notedock.R
import com.pixelart.notedock.module.FolderModule
import kotlinx.android.synthetic.main.folders_list_item.view.*

class FoldersAdapter : RecyclerView.Adapter<FoldersAdapter.FoldersHolder>() {

    private var folders = ArrayList<FolderModule>()

    fun setNewData(newData: ArrayList<FolderModule>) {
        folders = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoldersHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.folders_list_item, parent, false)
        return FoldersHolder(view)
    }

    override fun getItemCount(): Int {
        return folders.size
    }

    override fun onBindViewHolder(holder: FoldersHolder, position: Int) {
        holder.bindData(folders[position])
    }


    class FoldersHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindData(folder: FolderModule) {
            itemView.textViewFolderName.text = folder.name
            itemView.textViewNotesCount.text = folder.notesCount
        }
    }
}