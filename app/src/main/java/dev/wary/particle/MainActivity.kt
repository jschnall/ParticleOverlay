package dev.wary.particle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.wary.particle.ui.LineEmitterScreen
import dev.wary.particle.ui.ParticleEmitterScreen
import dev.wary.particle.ui.PointEmitterScreen
import dev.wary.particle.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                // PointEmitterScreen()
                // LineEmitterScreen()
                ParticleEmitterScreen()
            }
        }
    }
}