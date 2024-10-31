package dev.wary.particle.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import dev.wary.particle.engine.ParticleEngine

@Composable
fun ParticleOverlay(modifier: Modifier = Modifier, engine: MutableState<ParticleEngine>) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis { currentTime ->
                engine.value.onUpdate(currentTime)
                engine.value = engine.value
            }
        }
    }

    Canvas(
        modifier = modifier
            .background(Color.Transparent)
            .fillMaxSize()
            .clipToBounds()
            .onSizeChanged { size ->
                engine.value.onSizeChanged(size.width, size.height)
            },
        onDraw = {
            drawIntoCanvas { canvas ->
                engine.value.onDraw(canvas.nativeCanvas, context)
            }
        }
    )
}

