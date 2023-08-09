package com.pixelart.notedock.fragment.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.pixelart.notedock.BR
import com.pixelart.notedock.R
import com.pixelart.notedock.dataBinding.setupDataBinding
import com.pixelart.notedock.domain.livedata.observer.EventObserver
import com.pixelart.notedock.domain.livedata.observer.SpecificEventObserver
import com.pixelart.notedock.ext.openLoginActivity
import com.pixelart.notedock.ext.showAsSnackBar
import com.pixelart.notedock.viewModel.settings.ChangeEmailEvent
import com.pixelart.notedock.viewModel.settings.ChangeEmailViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChangeEmailFragment : Fragment() {

    private val changeEmailViewModel: ChangeEmailViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val dataBinding = setupDataBinding<ViewDataBinding>(
            R.layout.fragment_change_email,
            BR.viewmodel to changeEmailViewModel,
        )
        setHasOptionsMenu(true)
        changeEmailViewModel.lifecycleOwner = this
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
                .addOnFailureListener { error ->
                    if (error is FirebaseNetworkException) {
                        // All is well
                    } else {
                        openLoginActivity()
                    }
                }
        }
    }

    private fun observeLiveData() {
        changeEmailViewModel.onBackClicked.observe(
            viewLifecycleOwner,
            EventObserver {
                findNavController().popBackStack()
            },
        )

        changeEmailViewModel.changeEmail.observe(
            viewLifecycleOwner,
            SpecificEventObserver<ChangeEmailEvent> { event ->
                view?.let { view ->
                    when (event) {
                        is ChangeEmailEvent.Success -> {
                            openLoginActivity()
                        }

                        is ChangeEmailEvent.InvalidEmail -> R.string.invalid_email_message.showAsSnackBar(
                            view,
                        )

                        is ChangeEmailEvent.TryAgainError -> R.string.try_again.showAsSnackBar(
                            view,
                        )

                        is ChangeEmailEvent.InvalidPassword -> R.string.wrong_password.showAsSnackBar(
                            view,
                        )

                        is ChangeEmailEvent.EmailAlreadyUsed -> R.string.email_already_used_message.showAsSnackBar(
                            view,
                        )

                        is ChangeEmailEvent.UseNewEmail -> R.string.use_new_email.showAsSnackBar(
                            view,
                        )

                        is ChangeEmailEvent.UnknownError -> R.string.error_occurred.showAsSnackBar(
                            view,
                        )

                        is ChangeEmailEvent.NetworkError -> R.string.network_error_message.showAsSnackBar(
                            view,
                        )
                    }
                }
            },
        )
    }
}
