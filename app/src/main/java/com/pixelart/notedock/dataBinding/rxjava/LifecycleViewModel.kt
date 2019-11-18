package com.pixelart.notedock.dataBinding.rxjava

import androidx.lifecycle.*
import io.reactivex.disposables.CompositeDisposable

abstract class LifecycleViewModel : ViewModel() {
    private val lifecycleObserver: LifecycleObserver by lazy {
        object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onStart() {
                startStopDisposable.observe()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onResume() {
                resumePauseDisposable.observe()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            fun onPause() {
                resumePauseDisposable.dispose()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun onStop() {
                startStopDisposable.dispose()
            }
        }
    }
    var lifecycleOwner: LifecycleOwner? = null
        set(value) {
            if (value == null) {
                field?.lifecycle?.removeObserver(lifecycleObserver)
            } else {
                value.lifecycle.addObserver(lifecycleObserver)
            }
            field = value
        }

    private val startStopDisposable = ReusableCompositeDisposable { onStartStopObserve(it) }
    private val resumePauseDisposable = ReusableCompositeDisposable { onResumePauseObserve(it)
        }
    /**
     * Use this CompositeDisposable when you need to dispose subscription, that was subscribed out of
     * onStartStopObserve method, when on STOP lifecycle event is triggered.
     */
    protected val startStopDisposeBag
        get() = startStopDisposable.disposeBag
    /**
     * Use this CompositeDisposable when you need to dispose subscription, that was subscribed out of
     * onPauseResumeObserve method, when on PAUSE lifecycle event is triggered.
     */
    protected val resumePauseDisposeBag
        get() = resumePauseDisposable.disposeBag

    /**
     * Override to subscribe Observables for START-->--STOP lifecycle. Add returned Disposable to
     * disposeBag so it can be disposed when lifecycle is ended.
     */
    open fun onStartStopObserve(disposeBag: CompositeDisposable) {}

    /**
     * Override to subscribe Observables for RESUME-->--PAUSE lifecycle. Add returned Disposable to
     * disposeBag so it can be disposed when lifecycle is ended.
     */
    open fun onResumePauseObserve(disposeBag: CompositeDisposable) {}

    override fun onCleared() {
        resumePauseDisposable.dispose()
        startStopDisposable.dispose()
        lifecycleOwner = null
        super.onCleared()
    }
}