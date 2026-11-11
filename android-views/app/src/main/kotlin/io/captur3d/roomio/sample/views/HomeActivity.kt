package io.captur3d.roomio.sample.views

import android.content.Intent
import android.os.Bundle
import android.text.format.Formatter
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import io.captur3d.roomio.sdk.Roomio
import io.captur3d.roomio.sdk.RoomioBundle
import io.captur3d.roomio.sdk.RoomioError
import io.captur3d.roomio.sdk.RoomioSupport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class HomeActivity : AppCompatActivity() {
    private var bundle: RoomioBundle? = null

    private lateinit var lastScan: TextView
    private lateinit var share: Button
    private lateinit var error: TextView

    private val scan = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        result.data?.getStringExtra(ScanActivity.EXTRA_ERROR)?.let { show(error = it) }
        result.data?.getStringExtra(ScanActivity.EXTRA_FILE)?.let { bundle = RoomioBundle(File(it), File(it).length()) }
        render()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        lastScan = findViewById(R.id.last_scan)
        share = findViewById(R.id.share)
        error = findViewById(R.id.error)
        share.setOnClickListener { bundle?.let { lifecycleScope.launch { share(it.file) } } }
        findViewById<Button>(R.id.send_diagnostics).setOnClickListener { sendDiagnostics() }

        val start = findViewById<Button>(R.id.start)
        if (Roomio.isSupported(this) != RoomioSupport.UNSUPPORTED) {
            start.setOnClickListener {
                show(error = null)
                scan.launch(Intent(this, ScanActivity::class.java))
            }
        } else {
            start.visibility = View.GONE
            findViewById<View>(R.id.unsupported).visibility = View.VISIBLE
        }
    }

    private fun render() {
        lastScan.text = bundle?.let { "Last scan: ${Formatter.formatShortFileSize(this, it.sizeBytes)}" }
        lastScan.visibility = if (bundle == null) View.GONE else View.VISIBLE
        share.visibility = lastScan.visibility
        error.visibility = if (error.text.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    private fun show(error: String?) {
        this.error.text = error
        render()
    }

    private fun sendDiagnostics() {
        lifecycleScope.launch {
            try {
                share(Roomio.exportDiagnostics(this@HomeActivity))
            } catch (error: RoomioError) {
                show(error = error.message)
            }
        }
    }

    private suspend fun share(file: File) {
        // FileProvider can't share from the folder the SDK saves to, so share a copy in cacheDir/shared (see file_paths.xml).
        val copy = withContext(Dispatchers.IO) {
            val dir = File(cacheDir, "shared").apply { deleteRecursively(); mkdirs() }
            file.copyTo(File(dir, file.name))
        }
        val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", copy)
        val intent = Intent(Intent.ACTION_SEND)
            .setType("application/zip")
            .putExtra(Intent.EXTRA_STREAM, uri)
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(Intent.createChooser(intent, null))
    }
}
