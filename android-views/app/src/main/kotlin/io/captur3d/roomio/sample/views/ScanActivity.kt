package io.captur3d.roomio.sample.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import io.captur3d.roomio.sdk.Roomio
import io.captur3d.roomio.sdk.RoomioBundle
import io.captur3d.roomio.sdk.RoomioError
import io.captur3d.roomio.sdk.RoomioScanSession
import io.captur3d.roomio.sdk.RoomioScanState
import io.captur3d.roomio.sdk.RoomioScanView
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ScanActivity : AppCompatActivity() {
    private lateinit var session: RoomioScanSession
    private lateinit var scanView: RoomioScanView
    private lateinit var message: TextView

    private var exportError: String? = null
    private var export: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        session = try {
            Roomio.startScan(this)
        } catch (error: RoomioError) {
            setResult(RESULT_CANCELED, Intent().putExtra(EXTRA_ERROR, error.message))
            finish()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_scan)
        scanView = findViewById(R.id.scan_view)
        message = findViewById(R.id.message)
        scanView.bind(session)

        session.start()
        onBackPressedDispatcher.addCallback(this) { close(null) }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { session.isShowingOnboarding.collect { render() } }
                session.state.collect { render() }
            }
        }
    }

    private fun render() {
        val state = session.state.value
        val onboarding = session.isShowingOnboarding.value
        val text = exportError ?: (state as? RoomioScanState.Failed)?.error?.message
        message.text = text
        message.visibility = if (onboarding || text == null) View.GONE else View.VISIBLE

        when (state) {
            RoomioScanState.Finished -> if (export == null) export = lifecycleScope.launch { exportScan() }
            RoomioScanState.Cancelled -> close(null)
            else -> Unit
        }
    }

    private suspend fun exportScan() {
        try {
            close(session.export())
        } catch (error: RoomioError) {
            exportError = error.message
            render()
        }
    }

    private fun close(bundle: RoomioBundle?) {
        if (isFinishing) return
        scanView.unbind(session)
        session.close()
        setResult(RESULT_OK, Intent().putExtra(EXTRA_FILE, bundle?.file?.path))
        finish()
    }

    companion object {
        const val EXTRA_FILE = "file"
        const val EXTRA_ERROR = "error"
    }
}
