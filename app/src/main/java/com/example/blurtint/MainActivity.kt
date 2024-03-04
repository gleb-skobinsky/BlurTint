package com.example.blurtint

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Paint
import android.graphics.RenderEffect
import android.graphics.RenderNode
import android.graphics.Shader
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.withSave
import com.example.blurtint.ui.theme.BlurTintTheme

@RequiresApi(31)
class MainActivity : ComponentActivity() {

    private val renderNode = RenderNode("Content").apply {
        setPosition(0, 0, 1000, 1000)
        setRenderEffect(
            RenderEffect.createChainEffect(
                RenderEffect.createColorFilterEffect(
                    BlendModeColorFilter(Color.Red.copy(alpha = 0.3f).toArgb(), BlendMode.DST_OVER)
                ),
                RenderEffect.createBlurEffect(20f, 20f, Shader.TileMode.CLAMP)
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val textList = List(100) { "Label ${it + 1}" }

        setContent {
            val dir = LocalLayoutDirection.current
            BlurTintTheme {
                // A surface container using the 'background' color from the theme
                Scaffold(
                    topBar = {
                        Column(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.tertiaryContainer.copy(0.7f))
                                .fillMaxWidth()
                                .height(52.dp)
                                .padding(horizontal = 20.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Scaffold example",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                ) { scaffoldPadding ->
                    println("Main app scope recomposed")
                    Box(
                        Modifier
                            .padding(top = scaffoldPadding.calculateTopPadding())
                            .fillMaxSize()
                            .drawWithContent {
                                val cs = this
                                draw(this, dir, Canvas(renderNode.beginRecording()), size) {
                                    cs.drawContent()
                                }
                                renderNode.endRecording()
                                drawContent()
                                with(drawContext.canvas.nativeCanvas) {
                                    withSave {
                                        drawRenderNode(renderNode)
                                    }
                                }
                            }
                    ) {
                        LazyColumn(
                            Modifier.padding(horizontal = 16.dp)
                        ) {
                            items(count = textList.size, key = { it }) {
                                Text(
                                    text = "Label ${it + 1}",
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        OffsetSlider()
                    }
                }
            }
        }
    }

    @Composable
    private fun OffsetSlider() {
        var sliderValue by remember {
            mutableFloatStateOf(0f)
        }
        Slider(
            value = sliderValue,
            {
                sliderValue = it
                renderNode.translationX = it
            }
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BlurTintTheme {
        Greeting("Android")
    }
}