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
import dev.wary.particle.engine.DoubleColor
import dev.wary.particle.engine.Entity
import dev.wary.particle.engine.ParticleEngine
import dev.wary.particle.engine.ParticleParams
import dev.wary.particle.engine.Point
import dev.wary.particle.engine.PointEmitter
import dev.wary.particle.engine.DoubleRangeParam
import dev.wary.particle.engine.ExactParam
import dev.wary.particle.engine.LongRangeParam
import dev.wary.particle.engine.RangedParticleBuilder
import dev.wary.particle.engine.listParamOf
import dev.wary.particle.engine.toDoubleColor

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
            lifeSpan = LongRangeParam(1000, 5000),
            width = DoubleRangeParam(4.0, 16.0),
            height = DoubleRangeParam(8.0, 16.0),
            vx = DoubleRangeParam(-0.4, 0.4),
            vy = DoubleRangeParam(-0.4, 0.4),
            ax = ExactParam(0.0),
            ay = ExactParam(0.0),
            color = listParamOf(
                Color.RED.toDoubleColor(),
                Color.GREEN.toDoubleColor(),
                Color.BLUE.toDoubleColor(),
                Color.YELLOW.toDoubleColor(),
                Color.MAGENTA.toDoubleColor()
            ),
            colorChange = ExactParam(DoubleColor(0.0, 0.0, 0.0, 0.0))
        )
    )
    val entities = mutableListOf<Entity>().apply {
        add(PointEmitter(Point(200.0, 200.0), builder))
    }

    return ParticleEngine(entities)
}
