package dev.wary.particle

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.wary.particle.engine.DoubleColor
import dev.wary.particle.engine.DoubleRangeParam
import dev.wary.particle.engine.Entity
import dev.wary.particle.engine.ExactParam
import dev.wary.particle.engine.LineEmitter
import dev.wary.particle.engine.ListParam
import dev.wary.particle.engine.LongRangeParam
import dev.wary.particle.engine.Particle
import dev.wary.particle.engine.ParticleEngine
import dev.wary.particle.engine.ParticleParams
import dev.wary.particle.engine.Point
import dev.wary.particle.engine.RangedParticleBuilder
import dev.wary.particle.engine.TemplateParticleBuilder
import dev.wary.particle.engine.listParamOf
import dev.wary.particle.engine.toDoubleColor
import dev.wary.particle.ui.compose.ParticleEmitterScreen
import dev.wary.particle.ui.theme.MyApplicationTheme
import dev.wary.particle.ui.view.ParticleOverlayView

class ViewActivity : ComponentActivity() {
    lateinit var view: ParticleOverlayView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val emitter = buildLineEmitter()

        view = ParticleOverlayView(this)
        view.setBackgroundColor(Color.BLACK)

        view.engine = buildEngine(emitter)
        view.addSizeChangeListener(
            { w, h ->
                emitter.end.x = w.toDouble()
            }
        )
        setContentView(view)
    }

    override fun onStart() {
        super.onStart()
        view.start()
    }
    override fun onStop() {
        super.onStop()
        view.stop()
    }

    fun buildLineEmitter(): LineEmitter {
        val builder = RangedParticleBuilder(
            ParticleParams(
                lifeSpan = LongRangeParam(1000, 5000),
                width = ExactParam(64.0),
                height = ExactParam(64.0),
                vx = ExactParam(0.0),
                vy = DoubleRangeParam(0.1, 0.4),
                ax = ExactParam(0.0),
                ay = ExactParam(0.0),
                drawableResIds = listParamOf(R.drawable.teardrop),
                tint = listParamOf(
                    Color.BLUE.toDoubleColor(),
                ),
                tintChange = ExactParam(DoubleColor(-0.04, 0.08, 0.0, 0.0))
            )
        )
        return LineEmitter(Point(0.0, 0.0), Point(400.0, 0.0), builder, 0.08)
    }

    fun buildEngine(entity: Entity): ParticleEngine {
        val entities = mutableListOf<Entity>().apply {
            add(entity)
        }

        return ParticleEngine(gravity = 0.0005, initialState = entities, edgeCollisions = false)
    }
}