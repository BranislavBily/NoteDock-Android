package com.pixelart.notedock.domain.livedata.observer

import androidx.lifecycle.Observer
import com.pixelart.notedock.domain.livedata.model.Event

class SpecificEventObserver<T : Event>(val onChangedCallback: (event: T) -> Unit) : Observer<T> {
    override fun onChanged(event: T?) {
        if (event != null && !event.consumed) {
            event.consume()
            onChangedCallback(event)
        }
    }
}