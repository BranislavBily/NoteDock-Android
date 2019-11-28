package com.pixelart.notedock.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel

class NoteFragmentViewModel: LifecycleViewModel() {

    private val _textViewSkuska = MutableLiveData<String>().also {
        it.postValue("Idem")
    }
    val textViewSkuska: LiveData<String> = _textViewSkuska
}