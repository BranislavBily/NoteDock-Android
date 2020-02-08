package com.pixelart.notedock.fragment.settings


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.pixelart.notedock.BR

import com.pixelart.notedock.R
import com.pixelart.notedock.activity.LoginActivity
import com.pixelart.notedock.dataBinding.setupDataBinding
import com.pixelart.notedock.domain.livedata.observer.EventObserver
import com.pixelart.notedock.domain.livedata.observer.SpecificEventObserver
import com.pixelart.notedock.ext.hideSoftKeyboard
import com.pixelart.notedock.ext.openLoginActivity
import com.pixelart.notedock.ext.showAsSnackBar
import com.pixelart.notedock.viewModel.settings.ChangeEmailViewModel
import com.pixelart.notedock.viewModel.settings.DeleteAccountEvent
import com.pixelart.notedock.viewModel.settings.DeleteAccountViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class DeleteAccountFragment : Fragment() {

    private val deleteAccountViewModel: DeleteAccountViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = setupDataBinding<ViewDataBinding>(
            R.layout.fragment_delete_account,
            BR.viewmodel to deleteAccountViewModel
        )
        setHasOptionsMenu(true)
        deleteAccountViewModel.lifecycleOwner = this
        return dataBinding.root
    }

    override fun onStart() {
        super.onStart()

        observeLiveData()
    }

    override fun onResume() {
        super.onResume()

        FirebaseAuth.getInstance().currentUser?.let { user ->
            user.reload()
                .addOnFailureListener {error ->
                    if(error is FirebaseNetworkException) {
                        //All is well
                    } else {
                        openLoginActivity()
                    }
                }
        }
    }

    private fun observeLiveData() {
        deleteAccountViewModel.onBackClicked.observe(viewLifecycleOwner, EventObserver {
            findNavController().popBackStack()
        })

        deleteAccountViewModel.loading.observe(viewLifecycleOwner, Observer {loading ->
            if(loading) {
                context?.let {context ->
                    view?.let { view ->
                        hideSoftKeyboard(context, view)
                    }
                }
            }
        })

        deleteAccountViewModel.deleteAccount.observe(viewLifecycleOwner, SpecificEventObserver<DeleteAccountEvent> { event ->
            view?.let { view ->
                when(event) {
                    is DeleteAccountEvent.Success -> {
                        val intent = Intent(context, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        activity?.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    }
                    is DeleteAccountEvent.NetworkError -> R.string.network_error_message.showAsSnackBar(view)
                    is DeleteAccountEvent.WrongPassword -> R.string.wrong_password.showAsSnackBar(view)
                    is DeleteAccountEvent.TooManyRequests -> R.string.too_many_requests.showAsSnackBar(view)
                    is DeleteAccountEvent.UnknownError -> R.string.error_occurred.showAsSnackBar(view)
                }
            }
        })
    }
}