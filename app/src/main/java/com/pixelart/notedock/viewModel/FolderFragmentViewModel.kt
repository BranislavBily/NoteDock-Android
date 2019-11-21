package com.pixelart.notedock.viewModel

import androidx.lifecycle.LiveData
import com.pixelart.notedock.dataBinding.SingleLiveEvent
import com.pixelart.notedock.dataBinding.rxjava.LifecycleViewModel
import com.pixelart.notedock.domain.usecase.DeleteFolderUseCase
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class FolderFragmentViewModel(
    private val deleteFolderUseCase: DeleteFolderUseCase
): LifecycleViewModel() {

    private val _buttonClicked = SingleLiveEvent<DeleteButtonEvent>()
    val buttonClicked: LiveData<DeleteButtonEvent> = _buttonClicked

    private val _folderDeleted = SingleLiveEvent<FolderDeleteEvent>()
    val folderDeleted: LiveData<FolderDeleteEvent> = _folderDeleted

    fun onButtonClicked() {
        _buttonClicked.postValue(DeleteButtonEvent.OnClick)
    }

    fun deleteFolderModel(folderUUID: String) {
        startStopDisposeBag?.let { bag ->
            deleteFolderUseCase.deleteFolder(folderUUID)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                    { _folderDeleted.postValue(FolderDeleteEvent.Success) },
                    { _folderDeleted.postValue(FolderDeleteEvent.Error) }
                )
                .addTo(bag)
        }
    }
}
sealed class FolderDeleteEvent {
    object Error: FolderDeleteEvent()
    object Success: FolderDeleteEvent()
}

sealed class DeleteButtonEvent {
    object OnClick: DeleteButtonEvent()
}