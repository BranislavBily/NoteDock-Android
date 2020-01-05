package com.pixelart.notedock.fragment.authentication


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.pixelart.notedock.BR
import com.pixelart.notedock.R
import com.pixelart.notedock.dataBinding.setupDataBinding
import com.pixelart.notedock.viewModel.authentication.LoginButtonEvent
import com.pixelart.notedock.viewModel.authentication.LoginEvent
import com.pixelart.notedock.viewModel.authentication.LoginFragmentViewModel
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.android.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private val loginFragmentViewModel: LoginFragmentViewModel by viewModel()
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = setupDataBinding<ViewDataBinding>(
            R.layout.fragment_login,
            BR.viewmodel to loginFragmentViewModel
        )
        auth = FirebaseAuth.getInstance()
        loginFragmentViewModel.lifecycleOwner = this
        return dataBinding.root
    }

    override fun onResume() {
        super.onResume()

        observeLiveData()
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        currentUser?.let {
            Log.i("LoginActivity", "User is not null")
        } ?: Log.i("LoginActivity", "Current user is null")
    }


    private fun observeLiveData() {
        loginFragmentViewModel.loginButtonEvent.observe(this, Observer { event ->
            when(event) {
                is LoginButtonEvent.Pressed -> onLoginButtonPressed()
            }
        })

        loginFragmentViewModel.loginCompleted.observe(this, Observer { event ->
            when(event) {
                is LoginEvent.Success -> Toast.makeText(context, "Logged in", Toast.LENGTH_SHORT).show()
                is LoginEvent.InvalidEmail -> Toast.makeText(context, "Invalid email", Toast.LENGTH_SHORT).show()
                is LoginEvent.BadCredentials -> Toast.makeText(context, "Bad credentials", Toast.LENGTH_SHORT).show()
                is LoginEvent.NetworkError -> Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
                is LoginEvent.UnknownError -> Toast.makeText(context, "Unknown error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun onLoginButtonPressed() {
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()

        loginFragmentViewModel.login(email, password)
    }


}



