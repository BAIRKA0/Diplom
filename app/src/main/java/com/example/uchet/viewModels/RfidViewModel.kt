package com.example.uchet.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.uchet.entities.RFIDStates

class RfidViewModel : ViewModel() {
    var state by mutableStateOf(RFIDState())
        private set

    private val _isConnected = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean> = _isConnected

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        _isConnected.value = false
    }

    fun setConnectionStatus(isConnected: Boolean) {
        _isConnected.value = isConnected
    }

    fun changeCurrentState(newState: RFIDStates) {
        state = state.copy(
            currentState = newState
        )
    }

    fun updateState(inWork: Boolean) {
        if (inWork && state.currentState == RFIDStates.RFID_CONNECTED) {
            changeCurrentState(RFIDStates.RFID_PROCESSING)
        } else {
            changeCurrentState(
                if (isConnected.value!!)
                    RFIDStates.RFID_CONNECTED
                else
                    RFIDStates.RFID_DISCONNECTED)
        }
    }

    fun setErrorMessage(message: String) {
        _errorMessage.value = message
    }
}
data class RFIDState(
    val currentState: RFIDStates = RFIDStates.RFID_DISCONNECTED
)
