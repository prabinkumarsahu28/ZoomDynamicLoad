package com.prabin.baseproject

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.prabin.baseproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val manager by lazy {
        SplitInstallManagerFactory.create(this)
    }

    companion object {
        private const val DYNAMIC_ZOOM_MODULE = "Zoom Module"
    }

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initComponents()
    }

    private fun initComponents() {
        updateDynamicFeatureButtonState()
        setupZoomInstall()
    }

    private fun setupZoomInstall() {
        binding.startDownloadZoom.setOnClickListener {
            startInstall(DYNAMIC_ZOOM_MODULE)
        }

        binding.openDynamicApp.setOnClickListener {
            val intent = Intent()
            intent.setClassName(
                "com.prabin.baseproject",
                "com.prabin.on_demond_zoom.ZoomActivity"
            )
            startActivity(intent)
        }
    }

    private fun startInstall(name: String) {
        val request = SplitInstallRequest.newBuilder()
            .addModule(name)
            .build()

        val listener = SplitInstallStateUpdatedListener { state ->
            displayLoadingState(state)
            when (state.status()) {
                SplitInstallSessionStatus.DOWNLOADING -> {
                    showToast("Downloading feature")
                }

                SplitInstallSessionStatus.INSTALLED -> {
                    showToast("Feature ready to be used")
                    updateDynamicFeatureButtonState()
                }

                SplitInstallSessionStatus.FAILED -> {
                    showToast("Error: ${state.errorCode()}")
                }

                SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                    /*
                      This may occur when attempting to download a sufficiently large module.

                      In order to see this, the application has to be uploaded to the Play Store.
                      Then features can be requested until the confirmation path is triggered.
                     */
                    startIntentSender(state.resolutionIntent()?.intentSender, null, 0, 0, 0)
                }

                else -> {/* Do nothing in this example */
                    showToast("Error: ${state.errorCode()}")
                }
            }
        }

        manager.registerListener(listener)

        manager.startInstall(request)
            .addOnSuccessListener { sessionId ->
                showToast("Success: $sessionId")
            }
            .addOnFailureListener { exception ->
                showToast("Error: ${exception.message}")
            }
    }

    private fun displayLoadingState(state: SplitInstallSessionState) {
        binding.progressBar.apply {
            max = state.totalBytesToDownload().toInt()
            progress = state.bytesDownloaded().toInt()
        }
    }

    private fun updateDynamicFeatureButtonState() {
        binding.openDynamicApp.isEnabled =
            manager.installedModules.contains(DYNAMIC_ZOOM_MODULE)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}