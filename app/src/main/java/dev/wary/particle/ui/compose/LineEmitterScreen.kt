package dev.wary.particle.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.wary.particle.R
import dev.wary.particle.engine.DoubleColor
import dev.wary.particle.engine.Entity
import dev.wary.particle.engine.LineEmitter
import dev.wary.particle.engine.Particle
import dev.wary.particle.engine.ParticleEngine
import dev.wary.particle.engine.Point
import dev.wary.particle.engine.TemplateParticleBuilder
import dev.wary.particle.ui.theme.MyApplicationTheme

@Composable
fun LineEmitterScreen(modifier: Modifier = Modifier) {
    val emitter = buildLineEmitter()
    val engine = remember { mutableStateOf(buildEngine(emitter), neverEqualPolicy()) }

    Scaffold(modifier = Modifier
        .fillMaxSize()
        .onSizeChanged { size ->
            emitter.end.x = size.width.toDouble()
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            ParticleOverlay(modifier = Modifier.fillMaxSize(), engine)
            Box(modifier = Modifier.fillMaxWidth().background(Color.White).height(200.dp).align(Alignment.BottomCenter)) {
            }
        }
    }
}
fun buildLineEmitter(): LineEmitter {
    val builder = TemplateParticleBuilder(
        Particle(
            Point(0.0, 0.0),
            width = 32.0,
            height = 32.0,
            lifeSpan = 10_000,
            tint = DoubleColor(0.0, 255.0, 255.0, 255.0),
            tintChange = DoubleColor(0.08, 0.0, 0.0, 0.0),
            velocity = Point(0.0, 0.2),
            drawableResId = R.drawable.snowflake
        )
    )
    return LineEmitter(Point(0.0, 0.0), Point(400.0, 0.0), builder, 0.08)
}

fun buildEngine(entity: Entity): ParticleEngine {
    val entities = mutableListOf<Entity>().apply {
        add(entity)
    }

    return ParticleEngine(initialState = entities, edgeCollisions = false)
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

