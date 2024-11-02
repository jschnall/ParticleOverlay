package dev.wary.particle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.wary.particle.ui.compose.ParticleEmitterScreen
import dev.wary.particle.ui.theme.MyApplicationTheme

class ComposeActivity : ComponentActivity() {
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