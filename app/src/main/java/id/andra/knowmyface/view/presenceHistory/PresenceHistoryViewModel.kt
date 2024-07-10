package id.andra.knowmyface.view.presenceHistory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.andra.knowmyface.api.Resource
import id.andra.knowmyface.api.repository.PresenceRepository
import id.andra.knowmyface.model.Presence
import kotlinx.coroutines.launch

class PresenceHistoryViewModel : ViewModel() {

    private val presenceRepository = PresenceRepository()

    private val _page = MutableLiveData(1)
    val page: LiveData<Int> = _page

    private val _items = MutableLiveData<List<Presence>>(listOf())
    val items: LiveData<List<Presence>> = _items

    fun loadData() = viewModelScope.launch {
        when (val result = presenceRepository.getPresences(page = _page.value ?: 1)) {
            is Resource.Success -> {
                _items.value = result.data?.data.orEmpty()
            }

            is Resource.Error -> {

            }
        }
    }

}