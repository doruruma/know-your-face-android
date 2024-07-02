package id.andra.knowmyface.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _isRedirectToRecordPresence = MutableLiveData(false)
    val isRedirectToRecordPresence: LiveData<Boolean> = _isRedirectToRecordPresence

    private val _isWaitForCameraPerm = MutableLiveData(false)
    val isWaitForCameraPerm: LiveData<Boolean> = _isWaitForCameraPerm

    private val _isWaitForLocationPerm = MutableLiveData(false)
    val isWaitForLocationPerm: LiveData<Boolean> = _isWaitForLocationPerm

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

    fun setIsRedirectToRecordPresence(value: Boolean) {
        _isRedirectToRecordPresence.value = value
    }

    fun setIsWaitForCameraPerm(value: Boolean) {
        _isWaitForCameraPerm.value = value
    }

    fun setIsWaitForLocationPerm(value: Boolean) {
        _isWaitForLocationPerm.value = value
    }

}