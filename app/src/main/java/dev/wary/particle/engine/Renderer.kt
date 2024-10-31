package dev.wary.particle.engine

import android.content.Context
import android.graphics.BlendMode
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.appcompat.content.res.AppCompatResources


class ParticleRenderer {
    private val paint = Paint()
    private val bounds = Rect(0,0,0,0)

    fun drawDebugInfo(canvas: Canvas, engine: ParticleEngine) {
        paint.alpha = 255
        paint.color = Color.WHITE
        canvas.drawRect(0f, 0f, 400f, 120f, paint)
        paint.color = Color.BLACK
        paint.textSize = 48f
        canvas.drawText("Entities: ${engine.entities.size }", 80f, 80f, paint)
    }

    fun drawParticle(particle: Particle, canvas: Canvas, context: Context) {
        bounds.left = particle.position.x.toInt()
        bounds.right = (particle.position.x + particle.width).toInt()
        bounds.top = particle.position.y.toInt()
        bounds.bottom = (particle.position.y + particle.height).toInt()

        // Draw background
        paint.color = particle.color
        particle.alpha?.let {
            paint.alpha = it.toInt()
        }
        canvas.drawRect(bounds, paint)

        // Draw image
        particle.drawableResId?.let { resId ->
            val drawable = AppCompatResources.getDrawable(context, resId)

            drawable?.let {
                it.bounds = bounds
                it.draw(canvas)
            }
        }
    }
}