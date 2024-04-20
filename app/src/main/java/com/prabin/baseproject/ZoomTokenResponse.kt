package com.prabin.baseproject

data class ZoomMeetingJoinResponse(
    val token: String,
    val password: String? = null,
)

data class ZoomMeetingJoinDetails(
    val meetingId: Long,
    val fallbackMeetingUrl: String?,
    val meetingType: LiveMeetingType
)

enum class LiveMeetingType{
    LIVE_MEETING
}