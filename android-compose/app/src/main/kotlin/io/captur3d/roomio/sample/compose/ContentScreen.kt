package io.captur3d.roomio.sample.compose

import android.content.Context
import android.content.Intent
import android.text.format.Formatter
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import io.captur3d.roomio.sdk.Roomio
import io.captur3d.roomio.sdk.RoomioBundle
import io.captur3d.roomio.sdk.RoomioError
import io.captur3d.roomio.sdk.RoomioScanSession
import io.captur3d.roomio.sdk.RoomioSupport
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ContentScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var session by remember { mutableStateOf<RoomioScanSession?>(null) }
    var bundle by remember { mutableStateOf<RoomioBundle?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun startScan() {
        errorMessage = null
        try {
            session = Roomio.startScan(context as ComponentActivity)
        } catch (error: RoomioError) {
            errorMessage = error.message
        }
    }

    val current = session
    if (current != null) {
        ScanScreen(current) { result ->
            bundle = result ?: bundle
            session = null
        }
        return
    }

    Column(
        modifier = Modifier.fillMaxSize().safeDrawingPadding().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Spacer(Modifier.weight(1f))
        Icon(
            Icons.Outlined.ViewInAr,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Text("Roomio Sample", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)

        bundle?.let {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Last scan: ${Formatter.formatShortFileSize(context, it.sizeBytes)}")
                TextButton(onClick = { share(context, it.file) }) { Text("Share scan") }
            }
        }

        TextButton(onClick = {
            scope.launch {
                try {
                    share(context, Roomio.exportDiagnostics(context))
                } catch (error: RoomioError) {
                    errorMessage = error.message
                }
            }
        }) { Text("Send diagnostics") }

        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
        }

        Spacer(Modifier.weight(1f))

        if (Roomio.isSupported(context) != RoomioSupport.UNSUPPORTED) {
            Button(onClick = ::startScan) {
                Text("Start scan")
            }
        } else {
            Text("This device can't scan.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun share(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val intent = Intent(Intent.ACTION_SEND)
        .setType("application/zip")
        .putExtra(Intent.EXTRA_STREAM, uri)
        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    context.startActivity(Intent.createChooser(intent, null))
}
