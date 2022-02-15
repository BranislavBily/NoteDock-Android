package com.pixelart.notedock.adapter.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pixelart.notedock.R
import com.pixelart.notedock.model.SettingsModel
import kotlinx.android.synthetic.main.settings_list_item.view.*

class SettingsAdapter(
    private val settings: ArrayList<SettingsModel>,
    private val onSettingsClickListener: OnSettingsClickListener
) : RecyclerView.Adapter<SettingsAdapter.SettingsHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.settings_list_item, parent, false)
        return SettingsHolder(
            view,
            onSettingsClickListener
        )
    }

    override fun getItemCount(): Int {
        return settings.size
    }

    override fun onBindViewHolder(holder: SettingsHolder, position: Int) {
        holder.bindData(settings[position])
    }


    class SettingsHolder(
        itemView: View,
        private val onSettingsClickListener: OnSettingsClickListener
    ) : RecyclerView.ViewHolder(itemView) {

        fun bindData(setting: SettingsModel) {
            itemView.textViewSettingsTitle.text = setting.title
            itemView.imageViewIcon.setImageResource(setting.drawable)

            itemView.setOnClickListener {
                onSettingsClickListener.onSettingClick(setting)
            }
        }
    }

    interface OnSettingsClickListener {
        fun onSettingClick(setting: SettingsModel)
    }
}