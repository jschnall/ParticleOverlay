package dev.wary.particle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.wary.particle.ui.compose.CollisionScreen
import dev.wary.particle.ui.compose.LineEmitterScreen
import dev.wary.particle.ui.compose.NoEmitterScreen
import dev.wary.particle.ui.compose.ParticleEmitterScreen
import dev.wary.particle.ui.compose.PointEmitterScreen
import dev.wary.particle.ui.theme.MyApplicationTheme

class ComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                //NoEmitterScreen()
                // PointEmitterScreen()
                // LineEmitterScreen()
                // ParticleEmitterScreen()
                 CollisionScreen()
            }
        }
    }
}