package com.pixelart.notedock.fragment.settings


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.pixelart.notedock.BR
import com.pixelart.notedock.R
import com.pixelart.notedock.activity.LoginActivity
import com.pixelart.notedock.dataBinding.setupDataBinding
import com.pixelart.notedock.domain.livedata.observer.EventObserver
import com.pixelart.notedock.domain.livedata.observer.SpecificEventObserver
import com.pixelart.notedock.ext.showAsSnackBar
import com.pixelart.notedock.viewModel.settings.ChangeEmailEvent
import com.pixelart.notedock.viewModel.settings.ChangeEmailViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class ChangeEmailFragment : Fragment() {

    private val changeEmailViewModel: ChangeEmailViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = setupDataBinding<ViewDataBinding>(
            R.layout.fragment_change_email,
            BR.viewmodel to changeEmailViewModel
        )
        setHasOptionsMenu(true)
        changeEmailViewModel.lifecycleOwner = this
        return dataBinding.root
    }

    override fun onStart() {
        super.onStart()

        observeLiveData()
    }

    private fun observeLiveData() {
        changeEmailViewModel.onBackClicked.observe(viewLifecycleOwner, EventObserver {
            findNavController().popBackStack()
        })

        changeEmailViewModel.changeEmail.observe(viewLifecycleOwner, SpecificEventObserver<ChangeEmailEvent> { event ->
          view?.let { view ->
              when(event) {
                  is ChangeEmailEvent.Success -> {
                      val intent = Intent(context, LoginActivity::class.java)
                      intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                      startActivity(intent)
                      activity?.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                  }
                  is ChangeEmailEvent.InvalidEmail -> R.string.invalid_email_message.showAsSnackBar(view)
                  is ChangeEmailEvent.TryAgainError -> R.string.try_again.showAsSnackBar(view)
                  is ChangeEmailEvent.InvalidPassword -> R.string.wrong_password.showAsSnackBar(view)
                  is ChangeEmailEvent.EmailAlreadyUsed -> R.string.email_already_used_message.showAsSnackBar(view)
                  is ChangeEmailEvent.UseNewEmail -> R.string.use_new_email.showAsSnackBar(view)
                  is ChangeEmailEvent.UnknownError -> R.string.error_occurred.showAsSnackBar(view)
                  is ChangeEmailEvent.NetworkError -> R.string.network_error_message.showAsSnackBar(view)
              }
          }
        })
    }
}
