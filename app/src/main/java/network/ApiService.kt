package network

import data.model.CallLogItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/analyze-calls")
    suspend fun analyzeCalls(@Body callLogs: List<CallLogItem>): Response<Map<String, String>>
}