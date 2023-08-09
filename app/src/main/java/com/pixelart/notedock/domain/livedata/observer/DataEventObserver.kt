package com.pixelart.notedock.domain.livedata.observer

import androidx.lifecycle.Observer
import com.pixelart.notedock.domain.livedata.model.DataEvent

class DataEventObserver<T>(val onChangedCallback: (data: T?) -> Unit) : Observer<DataEvent<T>> {
    override fun onChanged(value: DataEvent<T>) {
        if (!value.consumed) {
            value.consume()
            onChangedCallback(value.data)
        }
    }
}
