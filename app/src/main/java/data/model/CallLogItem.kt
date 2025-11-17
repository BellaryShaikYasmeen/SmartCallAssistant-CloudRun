package data.model

data class CallLogItem(
    val name: String?,
    val number: String,
    val type: String,
    val duration: Long,
    val date: String
)