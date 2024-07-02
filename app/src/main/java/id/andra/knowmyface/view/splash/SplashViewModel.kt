package id.andra.knowmyface.view.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.andra.knowmyface.api.Resource
import id.andra.knowmyface.api.repository.UserRepository
import id.andra.knowmyface.api.response.LoginResponse
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val userRepository = UserRepository()

    private val _refreshTokenResponse = MutableLiveData<LoginResponse?>()
    val refreshTokenResponse: LiveData<LoginResponse?> = _refreshTokenResponse

    private val _refreshTokenError = MutableLiveData(false)
    val refreshTokenError: LiveData<Boolean> = _refreshTokenError

    fun refreshToken(refreshToken: String) = viewModelScope.launch {
        when (val result = userRepository.refreshToken(
            refreshToken = refreshToken
        )) {
            is Resource.Success -> {
                _refreshTokenResponse.value = result.data
            }

            is Resource.Error -> {
                _refreshTokenError.value = true
            }
        }
    }

}