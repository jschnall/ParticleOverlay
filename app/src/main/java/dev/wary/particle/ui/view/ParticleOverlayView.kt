package dev.wary.particle.ui.view

import android.content.Context
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import dev.wary.particle.engine.ParticleEngine
import java.time.Clock

class ParticleOverlayView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
): View(
    context,
    attrs,
    defStyleAttr,
    0
) {
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
    }

    constructor(context: Context) : this(context, null, 0) {
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        engine?.let {
            it.bounds.right = width.toDouble()
            it.bounds.bottom = height.toDouble()
        }
        for (listener in onSizeChangeListeners) {
            listener(w, h)
        }
    }

//    override fun onMeasure (widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//    }
//
//    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
//        super.onLayout(changed, left, top, right, bottom)
//    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        engine?.onDraw(canvas, context)
    }

    fun start() {
        handler.postDelayed(updateCallback, DELAY_MILLIS)
    }

    fun stop() {
        handler.removeCallbacks(updateCallback)
    }

    fun addSizeChangeListener(listener: (Int, Int) -> Unit) {
        onSizeChangeListeners.add(listener)
    }

    fun removeSizeChangeListener(listener: (Int, Int) -> Unit) {
        onSizeChangeListeners.remove(listener)
    }

    val onSizeChangeListeners = mutableSetOf<(Int, Int) -> Unit>()
    var engine: ParticleEngine? = null
    var clock = Clock.systemDefaultZone()
    private val handler: Handler = Handler(Looper.getMainLooper())
    private val updateCallback = object : Runnable {
        override fun run() {
            engine?.onUpdate(clock.millis())
            invalidate()
            handler.postDelayed(this, DELAY_MILLIS)
        }
    }

    companion object {
        const final val DELAY_MILLIS = 60L
    }
}

