package dev.wary.particle.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import dev.wary.particle.R
import dev.wary.particle.engine.Entity
import dev.wary.particle.engine.LineEmitter
import dev.wary.particle.engine.Particle
import dev.wary.particle.engine.ParticleEngine
import dev.wary.particle.engine.Point
import dev.wary.particle.engine.TemplateParticleBuilder
import dev.wary.particle.ui.theme.MyApplicationTheme
import java.util.logging.Logger

@Composable
fun LineEmitterScreen(modifier: Modifier = Modifier) {
    val engine = remember { mutableStateOf(buildLineEmitter(), neverEqualPolicy()) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Greeting(
            name = "Android",
            modifier = Modifier.padding(innerPadding)
        )
        ParticleOverlay(modifier = Modifier.fillMaxSize(), engine)
    }
}

fun buildLineEmitter(): ParticleEngine {
    val builder = TemplateParticleBuilder(
        Particle(
            Point(1, 1),
            width = 64,
            height = 64,
            lifeSpan = 7000,
            velocity = Point(200, 120),
            //color = Color.BLUE,
            drawableResId = R.drawable.snowflake
        )
    )
    val entities = mutableListOf<Entity>().apply {
        add(LineEmitter(Point(1, 1), Point(1, 400), builder))
    }

    return ParticleEngine(entities)
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}

