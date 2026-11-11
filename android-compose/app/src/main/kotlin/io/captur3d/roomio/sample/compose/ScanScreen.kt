package io.captur3d.roomio.sample.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import io.captur3d.roomio.sdk.RoomioBundle
import io.captur3d.roomio.sdk.RoomioError
import io.captur3d.roomio.sdk.RoomioScanSession
import io.captur3d.roomio.sdk.RoomioScanState
import io.captur3d.roomio.sdk.RoomioScanView

@Composable
fun ScanScreen(session: RoomioScanSession, onDone: (RoomioBundle?) -> Unit) {
    val state by session.state.collectAsState()
    val showingOnboarding by session.isShowingOnboarding.collectAsState()
    var message by remember { mutableStateOf<String?>(null) }

    fun close(bundle: RoomioBundle?) {
        session.close()
        onDone(bundle)
    }

    LaunchedEffect(session) { session.start() }

    LaunchedEffect(state) {
        when (state) {
            RoomioScanState.Finished -> try {
                close(session.export())
            } catch (error: RoomioError) {
                message = error.message
            }
            RoomioScanState.Cancelled -> close(null)
            else -> Unit
        }
    }

    BackHandler { close(null) }

    Box(Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                RoomioScanView(context).apply {
                    keepScreenOn = true
                    bind(session)
                }
            },
            onRelease = { it.unbind(session) },
            modifier = Modifier.fillMaxSize(),
        )

        if (!showingOnboarding) Column(
            modifier = Modifier.fillMaxSize().safeDrawingPadding().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.weight(1f))
            (message ?: (state as? RoomioScanState.Failed)?.error?.message)?.let {
                Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)) {
                    Text(it, Modifier.padding(horizontal = 20.dp, vertical = 12.dp))
                }
            }
            Spacer(Modifier.weight(1f))
        }
    }
}
