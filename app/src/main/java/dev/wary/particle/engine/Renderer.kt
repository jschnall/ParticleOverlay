package dev.wary.particle.engine

import android.content.Context
import android.graphics.BlendMode
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.withTranslation
import java.util.logging.Logger


class ParticleRenderer {
    private val paint = Paint()
    private val bounds = Rect(0,0,0,0)

    fun drawParticle(particle: Particle, canvas: Canvas, context: Context) {
        bounds.left = particle.position.x
        bounds.right = particle.position.x + particle.width
        bounds.top = particle.position.y
        bounds.bottom = particle.position.y + particle.height

        // Draw background
        paint.color = particle.color
        particle.alpha?.let {
            paint.alpha = it
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