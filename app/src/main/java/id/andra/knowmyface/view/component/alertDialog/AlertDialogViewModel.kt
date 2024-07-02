package id.andra.knowmyface.view.component.alertDialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AlertDialogViewModel : ViewModel() {

    private val _title = MutableLiveData("")
    val title: LiveData<String> = _title

    private val _message = MutableLiveData("")
    val message: LiveData<String> = _message

    fun setTitle(value: String) {
        _title.value = value
    }

    fun setMessage(value: String) {
        _message.value = value
    }

}