package dev.wary.particle.ui.compose

import android.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import dev.wary.particle.engine.DoubleColor
import dev.wary.particle.engine.Entity
import dev.wary.particle.engine.ParticleEngine
import dev.wary.particle.engine.ParticleParams
import dev.wary.particle.engine.Point
import dev.wary.particle.engine.PointEmitter
import dev.wary.particle.engine.DoubleRangeParam
import dev.wary.particle.engine.ExactParam
import dev.wary.particle.engine.LongRangeParam
import dev.wary.particle.engine.OverflowPolicy
import dev.wary.particle.engine.RangedParticleBuilder
import dev.wary.particle.engine.listParamOf
import dev.wary.particle.engine.toDoubleColor

@Composable
fun PointEmitterScreen(modifier: Modifier = Modifier) {
    val emitter = buildEmitter()
    val engine = remember { mutableStateOf(buildParticleEngine(emitter), neverEqualPolicy()) }

    Scaffold(modifier = Modifier.fillMaxSize()
        .onSizeChanged { size ->
            emitter.position.x = size.width.toDouble() / 2
            emitter.position.y = size.height.toDouble() / 2
        }
    ) { innerPadding ->
        ParticleOverlay(modifier = Modifier.fillMaxSize(), engine)

        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Greeting(name = "Particles", modifier = modifier.align(Alignment.Center))
        }
    }
}

fun buildEmitter(): PointEmitter {
    val builder = RangedParticleBuilder(
        ParticleParams(
            lifeSpan = LongRangeParam(1000, 5000),
            width = ExactParam(4.0),
            height = DoubleRangeParam(8.0, 64.0),
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
            colorChange = ExactParam(DoubleColor(-0.08, 0.0, 0.0, 0.0))
        )
    )
    return PointEmitter(Point(200.0, 200.0), builder, emitRate = 0.1)
}

fun buildParticleEngine(entity: Entity): ParticleEngine {
    val entities = mutableListOf<Entity>().apply {
        add(entity)
    }

    return ParticleEngine(
        initialState = entities,
        maxCapacity = 100,
        edgeCollisions = false,
        overflowPolicy = OverflowPolicy.REPLACE_OLDEST_NON_EMITTER
    )
}
