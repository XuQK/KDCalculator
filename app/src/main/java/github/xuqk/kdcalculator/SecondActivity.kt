package github.xuqk.kdcalculator

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.withSaveLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import github.xuqk.kdcalculator.ui.theme.KDCalculatorTheme

class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SecondScreen()
        }
    }
}

private fun locatePointer(rectList: List<Rect>, offset: Offset): Int {
    return rectList.indexOfFirst { it.contains(offset) }
}

@Preview
@Composable
fun SecondScreen() {
//    val rectList: MutableList<Rect> = remember { MutableList(keys.size) { Rect.Zero } }
//    var pressRect = remember { Rect.Zero }
//    var currPosition by remember { mutableStateOf(Offset.Unspecified) }
    var currX by remember { mutableStateOf(-1f) }
    var currY by remember { mutableStateOf(-1f) }

    var nextPressState by remember { mutableStateOf(0f) }
//    var pressState = remember { 0f }
    val pressState by animateFloatAsState(targetValue = nextPressState, tween(durationMillis = 5000))
//    LaunchedEffect(nextPressState) {
//        Animatable(if (nextPressState == 0f) 1f else 0f).animateTo(nextPressState, tween(durationMillis = 600)) {
//            pressState = value
//        }
//    }
//    LaunchedEffect(key1 = Unit, block = { nextPressState = 1f })
    var r by remember { mutableStateOf(Offset.Zero) }
    LaunchedEffect(r) {
        println("变了")
    }
    KDCalculatorTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
//                .background(Color.Black)
        ) {
            val maxRotation = remember { 5f }
            var size = remember { IntSize.Zero }
            Column(
                modifier = Modifier
                    .fillMaxSize()
//                    .onGloballyPositioned {
//                        size = it.size
//                    }
                    .graphicsLayer {
//                        rotationX = currX.coerceAtMost(1f)
//                        rotationY = currY.coerceAtMost(1f)
//                        rotationX = 10f
//                        rotationY = 10f
//                        val centerX = size.width / 2
//                        val centerY = size.height / 2
//                        rotationX = (centerY - currY) / centerY * maxRotation
//                        rotationY = -(centerX - currX) / centerX * maxRotation

//                        if (currX < 0 || currY < 0) return@graphicsLayer
//                        val centerX = size.width / 2
//                        val centerY = size.height / 2
//                        if (currX < centerX) {
//                            if (currY < centerY) {
//                                // 第一象限
//                                rotationX = (centerY - currY) / centerY * maxRotation
//                                rotationY = -(centerX - currX) / centerX * maxRotation
//                            } else {
//                                // 第四象限
//                                rotationX = -(currY - centerY) / centerY * maxRotation
//                                rotationY = -(centerX - currX) / centerX * maxRotation
//                            }
//                        } else {
//                            if (currY < centerY) {
//                                // 第二象限
//                                rotationX = -(currY - centerY) / centerY * maxRotation
//                                rotationY = (currX - centerX) / centerX * maxRotation
//                            } else {
//                                // 第三象限
//                                rotationX = (centerY - currY) / centerY * maxRotation
//                                rotationY = (currX - centerX) / centerX * maxRotation
//                            }
//                        }


                    }
                    .background(color = MaterialTheme.colorScheme.background)
                    .pointerInput(Unit) {
                        detectTapGestures(onPress = {
                            r = Offset(0.0f, 0.1f)
                        })
//                        try {
//                            forEachGesture {
//                                try {
//                                    awaitPointerEventScope {
//                                        try {
//                                            val firstDown = awaitFirstDown()
//                                            if (firstDown.pressed) {
//                                                val currPosition = firstDown.position
//                                                currX = currPosition.x
//                                                currY = currPosition.y
//                                                firstDown.consume()
//                                                Log.d("TAG_LOG", "SecondScreen: ")
//                                                nextPressState = 1f
//                                                return@awaitPointerEventScope
//                                            } else {
//                                                return@awaitPointerEventScope
//                                            }
//                                        } catch (e: Exception) {
//                                            return@awaitPointerEventScope
//                                        }
//                                    }
//                                } catch (e: Exception) {
//                                    e.printStackTrace()
//                                    return@forEachGesture
//                                }
//
//                            }
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                            return@pointerInput
//                        }
                    }
            ) {

            }
        }
    }
}
