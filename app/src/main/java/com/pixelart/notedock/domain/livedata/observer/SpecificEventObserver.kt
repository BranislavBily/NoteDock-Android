package com.pixelart.notedock.domain.livedata.observer

import androidx.lifecycle.Observer
import com.pixelart.notedock.domain.livedata.model.Event

class SpecificEventObserver<T : Event>(val onChangedCallback: (event: T) -> Unit) : Observer<T> {
    override fun onChanged(value: T) {
        if (!value.consumed) {
            value.consume()
            onChangedCallback(value)
        }
    }
}
