package id.andra.knowmyface.view.recordPresence

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.andra.knowmyface.api.Resource
import id.andra.knowmyface.api.repository.PresenceRepository
import id.andra.knowmyface.api.request.PresenceRequest
import id.andra.knowmyface.api.response.PresenceResponse
import id.andra.knowmyface.helper.Constant
import kotlinx.coroutines.launch

class RecordPresenceViewModel : ViewModel() {

    private val presenceRepository = PresenceRepository()

    private val _presenceState = MutableLiveData(Constant.PRESENCE_UNDEFINED_STATE)
    val presenceState: LiveData<Int> = _presenceState

    private val _longitude = MutableLiveData("")

    private val _latitude = MutableLiveData("")

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isRecognized = MutableLiveData(false)
    val isRecognized: LiveData<Boolean> = _isRecognized

    private val _presenceResponse = MutableLiveData<PresenceResponse?>()
    val presenceResponse: LiveData<PresenceResponse?> = _presenceResponse

    private val _presenceError = MutableLiveData("")
    val presenceError: LiveData<String> = _presenceError

    fun setPresenceState(value: Int) {
        _presenceState.value = value
    }

    fun setPresenceError(value: String) {
        _presenceError.value = value
    }

    fun setLongitude(value: String) {
        _longitude.value = value
    }

    fun setLatitude(value: String) {
        _latitude.value = value
    }

    fun setIsRecognized(value: Boolean) {
        _isRecognized.value = value
    }

    fun onSubmit(userId: String, photo: List<Byte>) = viewModelScope.launch {
        _isLoading.value = true
        val request = PresenceRequest(
            userId = userId,
            longitude = _longitude.value,
            latitude = _latitude.value,
            photo = photo
        )
        val result =
            if (_presenceState.value == Constant.PRESENCE_UNDEFINED_STATE)
                presenceRepository.clockIn(
                    request = request
                )
            else
                presenceRepository.clockOut(
                    request = request
                )
        when (result) {
            is Resource.Success -> {
                _presenceResponse.value = result.data
            }

            is Resource.Error -> {
                when (result.code) {
                    500 -> {
                        _presenceError.value = result.error?.message.orEmpty()
                    }

                    422 -> {

                    }

                    else -> {}
                }
            }
        }
        _isLoading.value = false
    }

}