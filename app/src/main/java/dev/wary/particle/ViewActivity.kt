package dev.wary.particle

import android.graphics.Color
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import dev.wary.particle.engine.DoubleColor
import dev.wary.particle.engine.DoubleRangeParam
import dev.wary.particle.engine.ExactParam
import dev.wary.particle.engine.LineEmitter
import dev.wary.particle.engine.LongRangeParam
import dev.wary.particle.engine.ParticleEngine
import dev.wary.particle.engine.ParticleParams
import dev.wary.particle.engine.Point
import dev.wary.particle.engine.RangedParticleBuilder
import dev.wary.particle.engine.Rect
import dev.wary.particle.engine.listParamOf
import dev.wary.particle.engine.toDoubleColor
import dev.wary.particle.ui.view.ParticleOverlayView

class ViewActivity : ComponentActivity() {
    lateinit var particleView: ParticleOverlayView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val emitter = buildLineEmitter()
        particleView = ParticleOverlayView(this)

        particleView.engine = buildEngine(emitter)
        particleView.addSizeChangeListener(
            { w, h ->
                emitter.end.x = w.toDouble()
            }
        )

        val layout = FrameLayout(this)
        val imageView = ImageView(this)
        layout.setBackgroundColor(Color.BLACK)
        imageView.setImageResource(R.drawable.umbrella)
        layout.addView(imageView)
        layout.addView(particleView)

        setContentView(layout)
    }

    override fun onStart() {
        super.onStart()
        particleView.start()
    }
    override fun onStop() {
        super.onStop()
        particleView.stop()
    }

    fun buildLineEmitter(): LineEmitter {
        val builder = RangedParticleBuilder(
            ParticleParams(
                lifeSpan = LongRangeParam(1000, 5000),
                width = ExactParam(32.0),
                height = ExactParam(32.0),
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

    fun buildEngine(entity: Rect): ParticleEngine {
        val entities = mutableListOf<Rect>().apply {
            add(entity)
        }

        return ParticleEngine(gravity = 0.0005, initialState = entities, edgeCollisions = false)
    }
}