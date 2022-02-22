package com.example.trackerapp.presentation.fragment.walk_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.trackerapp.domain.walk.DeleteWalkInteractor
import com.example.trackerapp.domain.walk.GetWalkListInteractor
import com.example.trackerapp.entity.Walk
import com.example.trackerapp.presentation.base.BaseViewModel
import com.example.trackerapp.presentation.base.BaseViewModelAssistedFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WalkListViewModel @AssistedInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val getWalksList: GetWalkListInteractor,
    private val deleteWalk: DeleteWalkInteractor,
) : BaseViewModel(savedStateHandle) {

    @AssistedFactory
    interface Factory : BaseViewModelAssistedFactory<WalkListViewModel>

    private val _showEmptyState = MutableLiveData<Boolean>()
    val showEmptyState: LiveData<Boolean>
        get() = _showEmptyState

    private val _showLoader = MutableLiveData<Boolean>()
    val showLoader: LiveData<Boolean>
        get() = _showLoader

    private val _showErrorState = MutableLiveData<Boolean>()
    val showErrorState: LiveData<Boolean>
        get() = _showErrorState

    private val _walksList = MutableLiveData<List<Walk>>()
    val walksList: LiveData<List<Walk>>
        get() = _walksList

    fun requestList() {
        viewModelScope.launch(Dispatchers.IO) {
            fetchList()
        }
    }

    fun onDeleteWalkButtonClick(idToDelete: Long) {
        viewModelScope.launch {
            deleteWalk(idToDelete)
                .doOnSuccess {
                    fetchList()
                }
                .doOnError { error ->
                    onStorageError(error)
                }
        }
    }

    private suspend fun fetchList() {
        viewModelScope.launch(Dispatchers.Main) {
            _showLoader.value = true
        }

        getWalksList()
            .doOnSuccess { list -> updateList(list) }
            .doOnError { error ->
                withContext(Dispatchers.Main) {
                    _showLoader.value = false
                    _showErrorState.value = true
                }
                onStorageError(error)
            }
    }

    private fun updateList(list: List<Walk>) {
        viewModelScope.launch(Dispatchers.Main) {
            _showEmptyState.value = list.isEmpty()
            _showLoader.value = false
            _showErrorState.value = false
            _walksList.value = list
        }
    }
}