package dev.wary.particle.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.wary.particle.R
import dev.wary.particle.engine.Entity
import dev.wary.particle.engine.LineEmitter
import dev.wary.particle.engine.Particle
import dev.wary.particle.engine.ParticleEngine
import dev.wary.particle.engine.Point
import dev.wary.particle.engine.TemplateParticleBuilder
import dev.wary.particle.ui.theme.MyApplicationTheme

@Composable
fun LineEmitterScreen(modifier: Modifier = Modifier) {
    val engine = remember { mutableStateOf(buildLineEmitter(), neverEqualPolicy()) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Greeting(name = "This is a test ", modifier = modifier.align(Alignment.Center))
        }
        ParticleOverlay(modifier = Modifier.fillMaxSize(), engine)
    }
}

fun buildLineEmitter(): ParticleEngine {
    val builder = TemplateParticleBuilder(
        Particle(
            Point(0.0, 0.0),
            width = 64.0,
            height = 64.0,
            lifeSpan = 7000,
            velocity = Point(2.0, 1.2),
            //color = Color.BLUE,
            drawableResId = R.drawable.snowflake
        )
    )
    val entities = mutableListOf<Entity>().apply {
        add(LineEmitter(Point(1.0, 1.0), Point(1.0, 400.0), builder))
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

