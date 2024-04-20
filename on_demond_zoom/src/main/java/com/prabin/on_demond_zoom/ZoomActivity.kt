package com.prabin.on_demond_zoom

import android.os.Bundle
import android.util.Log
import com.prabin.baseproject.BaseSplitActivity
import com.prabin.baseproject.LiveMeetingType
import com.prabin.baseproject.ZoomMeetingJoinDetails
import com.prabin.baseproject.ZoomMeetingJoinResponse
import com.prabin.on_demond_zoom.databinding.ActivityZoomBinding
import us.zoom.sdk.JoinMeetingParams
import us.zoom.sdk.ZoomSDK
import us.zoom.sdk.ZoomSDKInitParams
import us.zoom.sdk.ZoomSDKInitializeListener

class ZoomActivity : BaseSplitActivity() {

    private val binding by lazy {
        ActivityZoomBinding.inflate(layoutInflater)
    }

//    override fun attachBaseContext(base: Context?) {
//        super.attachBaseContext(base)
//        SplitCompat.installActivity(this)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val zoomMeetingDetails = ZoomMeetingJoinDetails(
            meetingId = 86981485156,
            fallbackMeetingUrl = "fallbackMeetingUrl",
            meetingType = LiveMeetingType.LIVE_MEETING,
        )

        binding.btnJoin.setOnClickListener {
            launchOnZoom(
                meetingJoinDetails = zoomMeetingDetails,
                data = ZoomMeetingJoinResponse(
                    token = binding.etZoomToken.toString(),
                    password = binding.etZoomPassword.toString()
                ),
                binding.etJwtToken.text.toString()
            )
        }

    }

    private fun initializeZoomSdk(jwtToken: String) {
        val sdk = ZoomSDK.getInstance()
        val params = ZoomSDKInitParams()
        params.jwtToken = jwtToken
        params.domain = "zoom.us"
        params.enableLog = true
        val listener: ZoomSDKInitializeListener = object : ZoomSDKInitializeListener {

            override fun onZoomSDKInitializeResult(errorCode: Int, internalErrorCode: Int) {

            }

            override fun onZoomAuthIdentityExpired() {

            }
        }
        sdk.initialize(this, listener, params)
    }

    private fun launchOnZoom(
        meetingJoinDetails: ZoomMeetingJoinDetails,
        data: ZoomMeetingJoinResponse,
        jwtToken: String
    ) {
        val zoomSdk = ZoomSDK.getInstance()
        val initParam = ZoomSDKInitParams()
        initParam.jwtToken = jwtToken
        initParam.domain = "zoom.us"
        initParam.enableLog = true

        val listener: ZoomSDKInitializeListener = object : ZoomSDKInitializeListener {

            override fun onZoomSDKInitializeResult(errorCode: Int, internalErrorCode: Int) {
                Log.d("ZoomTestActivity", "onZoomSDKInitializeResult: $errorCode")
            }

            override fun onZoomAuthIdentityExpired() {

            }
        }

        zoomSdk.initialize(this, listener, initParam)

        if (!zoomSdk.isInitialized) {
            return
        }

        zoomSdk.zoomUIService.apply {
            val shouldEnablePip = true
            disablePIPMode(shouldEnablePip)
            enableMinimizeMeeting(shouldEnablePip)
        }

        val number: String = meetingJoinDetails.meetingId.toString()

        val params = JoinMeetingParams()
        params.meetingNo = number
        params.webinarToken = data.token

        data.password?.let {
            params.password = it
        }

        zoomSdk.meetingService.joinMeetingWithParams(
            this,
            params,
            null
        )
    }
}