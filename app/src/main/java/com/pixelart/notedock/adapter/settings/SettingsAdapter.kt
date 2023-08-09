package com.pixelart.notedock.adapter.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pixelart.notedock.databinding.SettingsListItemBinding
import com.pixelart.notedock.model.SettingsModel

class SettingsAdapter(
    private val settings: ArrayList<SettingsModel>,
    private val onSettingsClickListener: OnSettingsClickListener,
) : RecyclerView.Adapter<SettingsAdapter.SettingsHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsHolder {
        val binding = SettingsListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return SettingsHolder(
            binding,
            onSettingsClickListener,
        )
    }

    override fun getItemCount(): Int {
        return settings.size
    }

    override fun onBindViewHolder(holder: SettingsHolder, position: Int) {
        holder.bindData(settings[position])
    }

    class SettingsHolder(
        private val binding: SettingsListItemBinding,
        private val onSettingsClickListener: OnSettingsClickListener,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindData(setting: SettingsModel) {
            binding.textViewSettingsTitle.text = setting.title
            binding.imageViewIcon.setImageResource(setting.drawable)

            itemView.setOnClickListener {
                onSettingsClickListener.onSettingClick(setting)
            }
        }
    }

    interface OnSettingsClickListener {
        fun onSettingClick(setting: SettingsModel)
    }
}
