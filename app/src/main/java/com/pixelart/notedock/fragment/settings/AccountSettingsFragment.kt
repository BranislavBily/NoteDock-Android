package com.pixelart.notedock.fragment.settings

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.internal.AccountType
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.pixelart.notedock.BR
import com.pixelart.notedock.NavigationRouter
import com.pixelart.notedock.R
import com.pixelart.notedock.adapter.settings.AccountAdapter
import com.pixelart.notedock.dataBinding.setupDataBinding
import com.pixelart.notedock.dialog.EditDisplayNameDialog
import com.pixelart.notedock.dialog.EditDisplaySuccessListener
import com.pixelart.notedock.domain.livedata.observer.EventObserver
import com.pixelart.notedock.domain.livedata.observer.SpecificEventObserver
import com.pixelart.notedock.ext.openLoginActivity
import com.pixelart.notedock.ext.openSoftKeyBoard
import com.pixelart.notedock.ext.showAsSnackBar
import com.pixelart.notedock.model.AccountListModel
import com.pixelart.notedock.viewModel.settings.AccountSettingsViewModel
import com.pixelart.notedock.viewModel.settings.UpdateDisplayNameEvent
import kotlinx.android.synthetic.main.create_folder_dialog.view.*
import kotlinx.android.synthetic.main.fragment_account_settings.*
import org.koin.android.viewmodel.ext.android.viewModel

class AccountSettingsFragment : Fragment(), AccountAdapter.OnAccountClickListener {

    private val accountSettingsViewModel: AccountSettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

    override fun onResume() {
        super.onResume()

        FirebaseAuth.getInstance().currentUser?.let { user ->
            user.reload()
                .addOnFailureListener { error ->
                    if (error is FirebaseNetworkException) {
                        //All is well
                    } else {
                        openLoginActivity()
                    }
                }
        }
    }

    private fun observeLiveData(accountAdapter: AccountAdapter) {
        accountSettingsViewModel.onBackClicked.observe(viewLifecycleOwner, EventObserver {
            findNavController().popBackStack()
        })

        accountSettingsViewModel.user.observe(viewLifecycleOwner, Observer { user ->
            user.reload()
                .addOnFailureListener { error ->
                    if (error is FirebaseNetworkException) {
                        //This is alright
                    } else {
                        openLoginActivity()
                    }
                }
                .addOnSuccessListener {
                    accountAdapter.setNewData(fillTable(user))
                }
            accountAdapter.setNewData(fillTable(user))
        })

        accountSettingsViewModel.updateDisplayName.observe(
            viewLifecycleOwner,
            SpecificEventObserver<UpdateDisplayNameEvent> { event ->
                view?.let { view ->
                    when (event) {
                        is UpdateDisplayNameEvent.Success -> {
                            reloadData(accountAdapter)
                        }
                        is UpdateDisplayNameEvent.NetworkError -> R.string.network_error_message.showAsSnackBar(
                            view
                        )
                        is UpdateDisplayNameEvent.UnknownError -> R.string.error_occurred.showAsSnackBar(
                            view
                        )
                    }
                }
            })
    }

    private fun reloadData(accountAdapter: AccountAdapter) {
        FirebaseAuth.getInstance().currentUser?.let { user ->
            accountAdapter.setNewData(fillTable(user))
        }
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
        accountList.add(
            AccountListModel(
                getString(R.string.delete_account),
                "Click to delete your NoteDock account"
            )
        )
        return accountList
    }

    override fun onAccountClick(account: AccountListModel) {
        view?.let { view ->
            when (account.title) {
                getString(R.string.email) -> {
                    NavigationRouter(view).accountToChangeEmail()
                }
                getString(R.string.phone_number) -> {
                    R.string.this_feature_not_available_yet.showAsSnackBar(view)
                }
                getString(R.string.display_name) -> {
                    openEditDisplayNameDialog()
                }
                else -> {
                    NavigationRouter(view).accountToDeleteAccount()
                }
            }
        }
    }

    private fun openEditDisplayNameDialog() {
        activity?.let { activity ->
            val dialog = EditDisplayNameDialog(object : EditDisplaySuccessListener {
                override fun onSuccess(displayName: String?) {
                    displayName?.trim()
                    if (displayName?.isEmpty() == true) {
                        accountSettingsViewModel.updateDisplayName(null)
                    } else {
                        accountSettingsViewModel.updateDisplayName(displayName)
                    }
                }
            }).createDialog(activity)
            dialog.show()
            FirebaseAuth.getInstance().currentUser?.let { user ->
                user.displayName?.let { displayName ->
                    val editText = dialog.findViewById<EditText>(R.id.editTextDisplayName)
                    editText?.setText(displayName, TextView.BufferType.EDITABLE)
                    editText?.setSelection(displayName.length)
                }
            }
            openSoftKeyBoard(activity)
        }
    }
}
