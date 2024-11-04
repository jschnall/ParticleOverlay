package dev.wary.particle.engine

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.appcompat.content.res.AppCompatResources


class ParticleRenderer {
    private val paint = Paint()
    private val bounds = Rect(0,0,0,0)

    fun drawDebugInfo(canvas: Canvas, engine: ParticleEngine) {
        paint.color = Color.WHITE
        canvas.drawRect(0f, 0f, 400f, 120f, paint)
        paint.color = Color.BLACK
        paint.textSize = 48f
        canvas.drawText("Entities: ${engine.entities.size }", 80f, 80f, paint)
    }

    fun drawParticle(particle: Particle, canvas: Canvas, context: Context) {
        bounds.left = particle.left.toInt()
        bounds.right = (particle.left + particle.width).toInt()
        bounds.top = particle.top.toInt()
        bounds.bottom = (particle.top + particle.height).toInt()

        // Draw background
        paint.setARGB(
            particle.color.alpha.toInt(),
            particle.color.red.toInt(),
            particle.color.green.toInt(),
            particle.color.blue.toInt()
        )

        canvas.drawRect(bounds, paint)

        // Draw image
        particle.drawableResId?.let { resId ->
            val drawable = AppCompatResources.getDrawable(context, resId)
            drawable?.let {
                particle.tint?.let { tint ->
                    it.setTint(
                        Color.argb(
                            tint.alpha.toInt(),
                            tint.red.toInt(),
                            tint.green.toInt(),
                            tint.blue.toInt()
                        )
                    )
                }
                it.bounds = bounds
                it.draw(canvas)
            }
        }
    }
}