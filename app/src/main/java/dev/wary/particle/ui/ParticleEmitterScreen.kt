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
import dev.wary.particle.R
import dev.wary.particle.engine.Entity
import dev.wary.particle.engine.ParticleEmitter
import dev.wary.particle.engine.ParticleEngine
import dev.wary.particle.engine.ParticleParams
import dev.wary.particle.engine.Point
import dev.wary.particle.engine.PointEmitter
import dev.wary.particle.engine.RangedParticleBuilder

@Composable
fun ParticleEmitterScreen(modifier: Modifier = Modifier) {
    val engine = remember { mutableStateOf(buildParticleEmitter(), neverEqualPolicy()) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Greeting(
            name = "Android",
            modifier = Modifier.padding(innerPadding)
        )
        ParticleOverlay(modifier = Modifier.fillMaxSize(), engine)
    }
}

fun buildParticleEmitter(): ParticleEngine {
    val builder = RangedParticleBuilder(
        ParticleParams(
            lifeSpan = LongRange(1000, 10_000),
            width = IntRange(8, 32),
            height = IntRange(8, 32),
            vx = IntRange(-40, 40),
            vy = IntRange(-40, -20),
            ax = IntRange(0, 0),
            ay = IntRange(0, 0),
            colors = listOf(
                Color.RED, Color.YELLOW
            ),
            alpha = IntRange(255, 255),
            alphaChange = IntRange(-20, -20)
        )
    )

    val x = 500
    val y = 500
    val w = 52
    val h = 473

    val entities = mutableListOf<Entity>().apply {
        add(
            ParticleEmitter(
                builder = builder,
                lifeSpan = 30_000,
                source = { Point(x + w / 2, y) },
                width = w,
                height = h,
                position = Point(x, y),
                velocity = Point(0, 0),
                drawableResId = R.drawable.match
            )
        )
    }

    return ParticleEngine(entities)
}
