package com.pixelart.notedock.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixelart.notedock.LifecycleViewModel
import com.pixelart.notedock.SingleLiveEvent
import com.pixelart.notedock.domain.usecase.DeleteFolderUseCase
import com.pixelart.notedock.model.FolderModel
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class FolderFragmentViewModel(
    private val deleteFolderUseCase: DeleteFolderUseCase
): LifecycleViewModel() {

    private val _buttonClicked = SingleLiveEvent<DeleteButtonEvent>()
    val buttonClicked: LiveData<DeleteButtonEvent> = _buttonClicked

    fun onButtonClicked() {
        _buttonClicked.postValue(DeleteButtonEvent.onClick)
    }

    private val _folderDeleted = SingleLiveEvent<FolderDeleteEvent>()
    val folderDeleted: LiveData<FolderDeleteEvent> = _folderDeleted

    fun deleteFolderModel(folderModel: FolderModel) {
        startStopDisposeBag?.let { bag ->
            deleteFolderUseCase.deleteFolder(folderModel)
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
    object onClick: DeleteButtonEvent()
}