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
import dev.wary.particle.engine.DoubleColor
import dev.wary.particle.engine.Entity
import dev.wary.particle.engine.ParticleEmitter
import dev.wary.particle.engine.ParticleEngine
import dev.wary.particle.engine.ParticleParams
import dev.wary.particle.engine.Point
import dev.wary.particle.engine.DoubleRangeParam
import dev.wary.particle.engine.ExactParam
import dev.wary.particle.engine.LongRangeParam
import dev.wary.particle.engine.RangedParticleBuilder
import dev.wary.particle.engine.listParamOf
import dev.wary.particle.engine.toDoubleColor

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
            lifeSpan = LongRangeParam(2000, 5000),
            width = DoubleRangeParam(8.0, 24.0),
            height = DoubleRangeParam(8.0, 24.0),
            vx = DoubleRangeParam(-0.02, 0.02),
            vy = DoubleRangeParam(-0.1, -0.04),
            ax = ExactParam(0.0),
            ay = ExactParam(0.00001),
            color = listParamOf(
                Color.YELLOW.toDoubleColor()
            ),
            colorChange = ExactParam(DoubleColor(-0.04, 0.0, -0.1, 0.0)),
        )
    )

    val x = 500.0
    val y = 500.0
    val w = 52.0
    val h = 473.0

    val entities = mutableListOf<Entity>().apply {
        add(
            ParticleEmitter(
                builder = builder,
                lifeSpan = 30_000,
                source = { Point(x + w / 2, y) },
                width = w,
                height = h,
                position = Point(x, y),
                velocity = Point(0.0, 0.0),
                drawableResId = R.drawable.match
            )
        )
    }

    return ParticleEngine(entities)
}
