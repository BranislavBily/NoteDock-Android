package com.pixelart.notedock.adapter.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pixelart.notedock.R
import com.pixelart.notedock.model.AccountListModel
import kotlinx.android.synthetic.main.account_list_item.view.*

class AccountAdapter(
    private val onAccountClickListener: OnAccountClickListener
) : RecyclerView.Adapter<AccountAdapter.AccountHolder>() {

    private var accountListItems = ArrayList<AccountListModel>()

    fun setNewData(accountList: ArrayList<AccountListModel>) {
        accountListItems = accountList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.account_list_item, parent, false)
        return AccountHolder(
            view, onAccountClickListener
        )
    }

    override fun getItemCount(): Int {
        return accountListItems.size
    }

    override fun onBindViewHolder(holder: AccountHolder, position: Int) {
        holder.bindData(accountListItems[position])
    }


    class AccountHolder(
        itemView: View, private val onAccountClickListener: OnAccountClickListener
    ) : RecyclerView.ViewHolder(itemView) {

        fun bindData(account: AccountListModel) {
            itemView.textViewAccountTitle.text = account.title
            itemView.textViewAccountDetail.text = account.description
            if (account.title != "Delete account") {
                itemView.imageViewAccountIcon.setImageResource(R.drawable.ic_edit)

                itemView.imageViewAccountIcon.setOnClickListener {
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