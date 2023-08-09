package com.pixelart.notedock.adapter.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pixelart.notedock.R
import com.pixelart.notedock.databinding.AccountListItemBinding
import com.pixelart.notedock.model.AccountListModel

class AccountAdapter(
    private val onAccountClickListener: OnAccountClickListener,
) : RecyclerView.Adapter<AccountAdapter.AccountHolder>() {

    private var accountListItems = ArrayList<AccountListModel>()

    fun setNewData(accountList: ArrayList<AccountListModel>) {
        accountListItems = accountList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountHolder {
        val binding = AccountListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return AccountHolder(
            binding,
            onAccountClickListener,
        )
    }

    override fun getItemCount(): Int {
        return accountListItems.size
    }

    override fun onBindViewHolder(holder: AccountHolder, position: Int) {
        holder.bindData(accountListItems[position])
    }

    class AccountHolder(
        private val binding: AccountListItemBinding,
        private val onAccountClickListener: OnAccountClickListener,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindData(account: AccountListModel) {
            binding.textViewAccountTitle.text = account.title
            binding.textViewAccountDetail.text = account.description
            if (account.title != "Delete account") {
                binding.imageViewAccountIcon.setImageResource(R.drawable.ic_edit)

                binding.imageViewAccountIcon.setOnClickListener {
                    onAccountClickListener.onAccountClick(account)
                }
            } else {
                itemView.setOnClickListener {
                    onAccountClickListener.onAccountClick(account)
                }
            }
        }
    }

    interface OnAccountClickListener {
        fun onAccountClick(account: AccountListModel)
    }
}
