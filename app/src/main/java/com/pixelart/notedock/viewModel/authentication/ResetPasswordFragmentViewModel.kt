package com.pixelart.notedock.viewModel.authentication

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel

class ResetPasswordFragmentViewModel: LifecycleViewModel() {

    val email = MutableLiveData<String>()

    val recoverEnabled: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(email) {
            postValue(Patterns.EMAIL_ADDRESS.matcher(it).matches())
        }
    }

    fun recoverAccount() {

    }
}