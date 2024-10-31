package dev.wary.particle.ui

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dev.wary.particle.engine.Entity
import dev.wary.particle.engine.ParticleEngine
import dev.wary.particle.engine.ParticleParams
import dev.wary.particle.engine.Point
import dev.wary.particle.engine.PointEmitter
import dev.wary.particle.engine.RangedParticleBuilder

@Composable
fun PointEmitterScreen(modifier: Modifier = Modifier) {
    val engine = remember { mutableStateOf(buildPointEmitter(), neverEqualPolicy()) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Greeting(
            name = "Android",
            modifier = Modifier.padding(innerPadding)
        )
        ParticleOverlay(modifier = Modifier.fillMaxSize(), engine)
    }
}

fun buildPointEmitter(): ParticleEngine {
    val builder = RangedParticleBuilder(
        ParticleParams(
            lifeSpan = LongRange(1000, 5000),
            width = IntRange(4, 16),
            height = IntRange(4, 16),
            vx = IntRange(-4, 4),
            vy = IntRange(-4, 4),
            ax = IntRange(0, 0),
            ay = IntRange(0, 0),
            colors = listOf(
                Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA
            )
        )
    )
    val entities = mutableListOf<Entity>().apply {
        add(PointEmitter(Point(200, 200), builder))
    }

    return ParticleEngine(entities)
}
