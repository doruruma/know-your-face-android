package id.andra.knowmyface.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.andra.knowmyface.api.Resource
import id.andra.knowmyface.api.repository.PresenceRepository
import id.andra.knowmyface.api.response.MessageResponse
import id.andra.knowmyface.helper.Constant
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val presenceRepository = PresenceRepository()

    private val _presenceState = MutableLiveData(Constant.PRESENCE_UNDEFINED_STATE)
    val presenceState: LiveData<Int> = _presenceState

    private val _isRedirectToRecordPresence = MutableLiveData(false)
    val isRedirectToRecordPresence: LiveData<Boolean> = _isRedirectToRecordPresence

    private val _isWaitForCameraPerm = MutableLiveData(false)
    val isWaitForCameraPerm: LiveData<Boolean> = _isWaitForCameraPerm

    private val _isWaitForLocationPerm = MutableLiveData(false)
    val isWaitForLocationPerm: LiveData<Boolean> = _isWaitForLocationPerm

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _showErrorDialog = MutableLiveData(false)
    val showErrorDialog: LiveData<Boolean> = _showErrorDialog

    private val _checkStatusResponse = MutableLiveData<MessageResponse?>()
    val checkStatusResponse: LiveData<MessageResponse?> = _checkStatusResponse

    private val _checkStatusError = MutableLiveData("")
    val checkStatusError: LiveData<String> = _checkStatusError

    private val _userName = MutableLiveData("")
    val userName: LiveData<String> = _userName

    private val _longitude = MutableLiveData("")
    val longitude: LiveData<String> = _longitude

    private val _latitude = MutableLiveData("")
    val latitude: LiveData<String> = _latitude

    fun setLongitude(value: String) {
        _longitude.value = value
    }

    fun setLatitude(value: String) {
        _latitude.value = value
    }

    fun setUserName(value: String) {
        _userName.value = "Hai, $value"
    }

    fun setIsRedirectToRecordPresence(value: Boolean) {
        _isRedirectToRecordPresence.value = value
    }

    fun setIsLoading(value: Boolean) {
        _isLoading.value = value
    }

    fun setShowErrorDialog(value: Boolean) {
        _showErrorDialog.value = value
    }

    fun setCheckStatusError(value: String) {
        _checkStatusError.value = value
    }

    fun setIsWaitForCameraPerm(value: Boolean) {
        _isWaitForCameraPerm.value = value
    }

    fun setIsWaitForLocationPerm(value: Boolean) {
        _isWaitForLocationPerm.value = value
    }

    fun toggleShowErrorDialog(value: Boolean) {
        _showErrorDialog.value = value
    }

    fun checkPresenceStatus() = viewModelScope.launch {
        when (val result = presenceRepository.checkStatus()) {
            is Resource.Success -> {
                _checkStatusResponse.value = result.data
                _isRedirectToRecordPresence.value = true
            }

            is Resource.Error -> {
                when (result.code) {
                    500 -> {
                        _checkStatusError.value = result.error?.message.orEmpty()
                        _showErrorDialog.value = true
                    }

                    else -> {}
                }
            }
        }
        _isLoading.value = false
    }

}