package ui.viewmodel;

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import data.model.CallLogItem
import data.repositary.CallRepository
import kotlinx.coroutines.launch


class CallViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CallRepository(application.contentResolver)

    val callLogs = MutableLiveData<List<CallLogItem>>()
    val summary = MutableLiveData<String>()

    fun loadCallLogs() {
        viewModelScope.launch {
            callLogs.value = repository.getCallLogs()
        }
    }

    fun analyzeCalls() {
        viewModelScope.launch {
            try {
                val logs = callLogs.value ?: emptyList()
                if (logs.isEmpty()) {
                    summary.value = "No call logs found. Grant permission first."
                    //Toast.makeText(this@, "Grant permission and ensure you have some call logs.", Toast.LENGTH_SHORT).show()
                }
                else {
                    summary.value = "Sending ${logs.size} logs to AI..."
                    val result = repository.analyzeCalls(logs)
                    summary.value = result
                }
            } catch (e: Exception) {
                summary.value = "Crash prevented: ${e.localizedMessage}"
                e.printStackTrace()
            }
        }
    }

}
