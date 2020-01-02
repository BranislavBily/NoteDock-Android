package com.pixelart.notedock.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.pixelart.notedock.BR
import com.pixelart.notedock.R
import com.pixelart.notedock.dataBinding.setupDataBinding
import com.pixelart.notedock.viewModel.LoginEvent
import com.pixelart.notedock.viewModel.LoginFragmentViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private val loginFragmentViewModel: LoginFragmentViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = setupDataBinding<ViewDataBinding>(
            R.layout.fragment_login,
            BR.viewmodel to loginFragmentViewModel
        )
        loginFragmentViewModel.lifecycleOwner = this
        return dataBinding.root
    }

    override fun onResume() {
        super.onResume()

        observeLiveData()
    }

    private fun observeLiveData() {
        loginFragmentViewModel.loginEvent.observe(this, Observer { event ->
            when(event) {
                is LoginEvent.Success ->  Toast.makeText(context, "Pressed", Toast.LENGTH_SHORT).show()
                is LoginEvent.Error -> Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

//    private fun navigateToFolders() {
//        val action = LoginFragmentDirections.actionLoginFragmentToFoldersViewFragment()
//        val navigationRouter = NavigationRouter(view)
//        navigationRouter.openAction(action)
//    }
}



