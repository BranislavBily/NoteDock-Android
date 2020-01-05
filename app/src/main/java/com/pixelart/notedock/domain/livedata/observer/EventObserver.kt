package com.pixelart.notedock.domain.livedata.observer

import androidx.lifecycle.Observer
import com.pixelart.notedock.domain.livedata.model.Event

class EventObserver(val onChangedCallback: () -> Unit) : Observer<Event> {
    override fun onChanged(event: Event?) {
        if (event != null && !event.consumed) {
            event.consume()
            onChangedCallback()
        }
    }
}