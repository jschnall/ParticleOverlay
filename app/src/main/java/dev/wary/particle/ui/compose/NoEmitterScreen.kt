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
import dev.wary.particle.engine.ParticleEngine
import dev.wary.particle.engine.ParticleParams
import dev.wary.particle.engine.PointEmitter
import dev.wary.particle.engine.DoubleRangeParam
import dev.wary.particle.engine.ExactParam
import dev.wary.particle.engine.LongRangeParam
import dev.wary.particle.engine.OverflowPolicy
import dev.wary.particle.engine.RangedParticleBuilder
import dev.wary.geo.Point
import dev.wary.geo.Rect
import dev.wary.particle.engine.Particle
import dev.wary.particle.engine.listParamOf
import dev.wary.particle.engine.toDoubleColor
import kotlin.random.Random

@Composable
fun NoEmitterScreen(modifier: Modifier = Modifier) {
    val colorListParam = listParamOf(
        Color.RED.toDoubleColor(),
        Color.GREEN.toDoubleColor(),
        Color.BLUE.toDoubleColor(),
    )

    val entities = mutableListOf<Rect>()
    repeat (200) {
        entities.add(
            Particle(
                position = Point(
                    Random.nextInt(500).toDouble(),
                    Random.nextInt(500).toDouble()
                ),
                velocity = Point(
                    Random.nextDouble(),
                    Random.nextDouble()
                ),
                width = 32.0,
                height = 32.0,
                lifeSpan = Random.nextLong(3000, 10_000),
                color = colorListParam.value,
            )
        )
    }

    val engine = ParticleEngine(
        initialState = entities,
        maxCapacity = 1000,
        edgeCollisions = true,
        particleCollisions = true,
        overflowPolicy = OverflowPolicy.DO_NOT_CREATE,
    )

    val engineState = remember { mutableStateOf(engine, neverEqualPolicy()) }

    Scaffold(modifier = Modifier.fillMaxSize()
        .onSizeChanged { size ->
            engine.sizeChanged(size.width, size.height)
            engine.entities
            engine.bounds
        }
    ) { innerPadding ->
        ParticleOverlay(modifier = Modifier.fillMaxSize(), engineState)

        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            //Greeting(name = "No Emitter", modifier = modifier.align(Alignment.Center))
        }
    }
}
