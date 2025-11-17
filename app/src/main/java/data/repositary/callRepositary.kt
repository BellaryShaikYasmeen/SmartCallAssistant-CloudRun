package data.repositary


import android.content.ContentResolver
import android.provider.CallLog
import data.model.CallLogItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import network.RetrofitClient

class CallRepository(private val contentResolver: ContentResolver) {

    suspend fun getCallLogs(): List<CallLogItem> = withContext(Dispatchers.IO) {
        val list = mutableListOf<CallLogItem>()
        val cursor = contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null, null, null,
            CallLog.Calls.DATE + " DESC"
        )

        cursor?.use {
            val nameIdx = it.getColumnIndex(CallLog.Calls.CACHED_NAME)
            val numIdx = it.getColumnIndex(CallLog.Calls.NUMBER)
            val typeIdx = it.getColumnIndex(CallLog.Calls.TYPE)
            val durIdx = it.getColumnIndex(CallLog.Calls.DURATION)
            val dateIdx = it.getColumnIndex(CallLog.Calls.DATE)

            while (it.moveToNext()) {
                val name = it.getString(nameIdx)
                val number = it.getString(numIdx)
                val type = when (it.getInt(typeIdx)) {
                    CallLog.Calls.INCOMING_TYPE -> "Incoming"
                    CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
                    CallLog.Calls.MISSED_TYPE -> "Missed"
                    else -> "Unknown"
                }
                val duration = it.getLong(durIdx)
                val date = it.getString(dateIdx)

                list.add(CallLogItem(name, number, type, duration, date))
            }
        }
        list
    }

    suspend fun analyzeCalls(callLogs: List<CallLogItem>): String {
        return try {
            val response = RetrofitClient.instance.analyzeCalls(callLogs)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.containsKey("summary")) {
                    body["summary"] ?: "No summary returned from AI."
                } else {
                    "No valid response received from server."
                }
            } else {
                "Server error: ${response.code()}"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Error: ${e.localizedMessage ?: "Unknown error"}"
        }
    }

}