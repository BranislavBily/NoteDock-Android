package com.pixelart.notedock.fragment.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.internal.AccountType
import com.google.firebase.auth.FirebaseUser
import com.pixelart.notedock.BR
import com.pixelart.notedock.R
import com.pixelart.notedock.adapter.settings.AccountAdapter
import com.pixelart.notedock.dataBinding.setupDataBinding
import com.pixelart.notedock.domain.livedata.observer.EventObserver
import com.pixelart.notedock.ext.showAsSnackBar
import com.pixelart.notedock.model.AccountListModel
import com.pixelart.notedock.viewModel.settings.AccountSettingsViewModel
import kotlinx.android.synthetic.main.create_folder_dialog.view.*
import kotlinx.android.synthetic.main.fragment_account_settings.*
import org.koin.android.viewmodel.ext.android.viewModel

class AccountSettingsFragment() : Fragment(), AccountAdapter.OnAccountClickListener {

    private val accountSettingsViewModel: AccountSettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = setupDataBinding<ViewDataBinding>(
            R.layout.fragment_account_settings,
            BR.viewmodel to accountSettingsViewModel
        )
        setHasOptionsMenu(true)
        accountSettingsViewModel.lifecycleOwner = this
        return dataBinding.root
    }

    override fun onStart() {
        super.onStart()

        val accountAdapter = AccountAdapter(this)
        recyclerViewAccount.layoutManager = LinearLayoutManager(context)
        recyclerViewAccount.adapter = accountAdapter
        observeLiveData(accountAdapter)
    }

    private fun observeLiveData(accountAdapter: AccountAdapter) {
        accountSettingsViewModel.onBackClicked.observe(viewLifecycleOwner, EventObserver {
            findNavController().popBackStack()
        })

        accountSettingsViewModel.user.observe(viewLifecycleOwner, Observer { user ->
            accountAdapter.setNewData(fillTable(user))
        })
    }

    private fun fillTable(user: FirebaseUser): ArrayList<AccountListModel> {
        val accountList = ArrayList<AccountListModel>()
        user.email?.let { email ->
            accountList.add(AccountListModel(getString(R.string.email), email))
        }
        accountList.add(AccountListModel(getString(R.string.phone_number), "No phone number"))
        user.displayName?.let { displayName ->
            accountList.add(AccountListModel(getString(R.string.display_name), displayName))
        } ?: run {
            accountList.add(AccountListModel(getString(R.string.display_name), "No display name"))
        }
        accountList.add(AccountListModel(getString(R.string.delete_account), "Click to delete your NoteDock account"))
        return accountList
    }

    override fun onAccountClick(account: AccountListModel) {
        view?.let { view ->
            when(account.title) {
                getString(R.string.email) -> {
                    Log.i("Account", "Email")
                }
                getString(R.string.phone_number) -> {
                    R.string.this_feature_not_available_yet.showAsSnackBar(view)
                }
                getString(R.string.display_name) -> {
                    Log.i("Account", "Display name")
                }
                else -> {
                    Log.i("Account", "Delete account")
                }
            }
        }

    }
}
