package com.pixelart.notedock.dataBinding.rxjava

import io.reactivex.disposables.CompositeDisposable

class ReusableCompositeDisposable(
    private val onObserveCallback: ((CompositeDisposable) -> Unit)? = null
) {
    private val lock = Unit
    var disposeBag: CompositeDisposable? = null
        private set

    fun dispose() = synchronized(lock) {
        disposeBag?.dispose()
        disposeBag = null
    }

    fun observe() {
        dispose()
        val newBag = CompositeDisposable()
        disposeBag = newBag
        onObserveCallback?.invoke(newBag)
    }
}