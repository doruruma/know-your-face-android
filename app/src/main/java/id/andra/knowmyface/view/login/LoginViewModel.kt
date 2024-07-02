package id.andra.knowmyface.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.andra.knowmyface.api.Resource
import id.andra.knowmyface.api.repository.UserRepository
import id.andra.knowmyface.api.response.LoginError
import id.andra.knowmyface.api.response.LoginResponse
import id.andra.knowmyface.api.response.UserResponse
import id.andra.knowmyface.helper.MoshiHelper
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val userRepo = UserRepository()

    private val _showEmailError = MutableLiveData(false)
    val showEmailError: LiveData<Boolean> = _showEmailError

    private val _showPasswordError = MutableLiveData(false)
    val showPasswordError: LiveData<Boolean> = _showPasswordError

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _showErrorDialog = MutableLiveData(false)
    val showErrorDialog: LiveData<Boolean> = _showErrorDialog

    private val _email = MutableLiveData("")
    val email: LiveData<String> = _email

    private val _password = MutableLiveData("")
    val password: LiveData<String> = _password

    private val _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String> = _errorMessage

    private val _loginResponse = MutableLiveData<LoginResponse?>()
    val loginResponse: LiveData<LoginResponse?> = _loginResponse

    private val _currentUserResponse = MutableLiveData<UserResponse?>()
    val currentUserResponse: LiveData<UserResponse?> = _currentUserResponse

    private val _loginError = MutableLiveData<LoginError>()
    val loginError: LiveData<LoginError> = _loginError

    fun toggleShowErrorDialog(value: Boolean) {
        _showErrorDialog.value = value
    }

    fun onEmailChanged(value: String) {
        _email.value = value
    }

    fun onPasswordChanged(value: String) {
        _password.value = value
    }

    fun getCurrentUser() = viewModelScope.launch {
        when (val result = userRepo.getCurrentUser()) {
            is Resource.Success -> {
                _currentUserResponse.value = result.data
            }

            is Resource.Error -> {

            }
        }
        _isLoading.value = false
    }

    fun login() = viewModelScope.launch {
        _isLoading.value = true
        when (val result = userRepo.login(email.value ?: "", password.value ?: "")) {
            is Resource.Success -> {
                _loginResponse.value = result.data
            }

            is Resource.Error -> {
                _isLoading.value = false
                when (result.code) {
                    400 -> {
                        _errorMessage.value = result.error?.message.orEmpty()
                        _showErrorDialog.value = true
                    }

                    422 -> {
                        val moshi = MoshiHelper.createMoshi()
                        val adapter = moshi.adapter(LoginError::class.java)
                        _loginError.value =
                            adapter.fromJson(result.error?.message.orEmpty()) ?: LoginError()
                        _showEmailError.value = _loginError.value?.email?.first()?.isNotEmpty()
                        _showPasswordError.value =
                            _loginError.value?.password?.first()?.isNotEmpty()
                    }

                    else -> {}
                }
            }
        }
    }

}