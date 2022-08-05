package github.xuqk.kdcalculator

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import github.xuqk.kdcalculator.ui.theme.KDCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

private fun locatePointer(rectList: List<Rect>, offset: Offset): Int {
    return rectList.indexOfFirst { it.contains(offset) }
}

@Preview
@Composable
fun MainScreen() {
    val rectList: MutableList<Rect> = remember { MutableList(keys.size) { Rect.Zero }}
    var pressRect = remember { Rect.Zero }
    KDCalculatorTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            var columnOffset = remember { Offset.Zero }
            Column(q
                modifier = Modifier
                    .padding(top = 100.dp)
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.background)
                    .onGloballyPositioned {
                        columnOffset = it.positionInWindow()
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                val index = locatePointer(rectList, it + columnOffset)
                                if (index > -1) {

                                }


                                Log.d("TAG_LOG", "MainScreen: onPress：${it}")
                            },
                            onTap = {
                                Log.d("TAG_LOG", "MainScreen: onTap：${it}")
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                Log.d("TAG_LOG", "MainScreen: onDragStart：${it}")
                            },
                            onDragEnd = {
                                Log.d("TAG_LOG", "MainScreen: onDragEnd：")
                            },
                            onDragCancel = {
                                Log.d("TAG_LOG", "MainScreen: onDragCancel：")
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                Log.d("TAG_LOG", "MainScreen: onDrag：${dragAmount}")
                            }
                        )
                    }
            ) {

                Box(modifier = Modifier.weight(1f)) {

                }

                val animatorMark = System.currentTimeMillis()
                LaunchedEffect(animatorMark) {

                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    userScrollEnabled = false
                ) {

                    itemsIndexed(
                        items = keys,
                        key = { index, item ->  item },
//                        span = { if (it == "") GridItemSpan(2) else GridItemSpan(1) }
                    ) { index, item ->
                        when {
                            functionKeys.contains(item) -> {
                                CalculatorButton(onPositionInWindowChange = { rectList[index] = it }) {
                                    FunctionIcon(item)
                                }
                            }
                            calculateKeys.contains(item) -> {
                                CalculatorButton(onPositionInWindowChange = { rectList[index] = it }) {
                                    CalculateIcon(item)
                                }
                            }
                            digitalKeys.contains(item) -> {
                                CalculatorButton(onPositionInWindowChange = { rectList[index] = it }) {
                                    Text(
                                        text = item,
                                        style = MaterialTheme.typography.displayMedium,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                            item == "=" -> {
                                CalculatorButton(onPositionInWindowChange = { rectList[index] = it }) {
                                    Text(
                                        text = item,
                                        style = MaterialTheme.typography.displayMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(
    onPositionInWindowChange: (Rect) -> Unit = {},
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .aspectRatio(1f)
            .fillMaxWidth()
            .onGloballyPositioned {
                onPositionInWindowChange(it.boundsInWindow())
            },
        contentAlignment = Alignment.Center,
        content = content
    )
}

@Preview
@Composable
fun CalculateIcon(key: String = "÷") {
    Image(
        painter = painterResource(
            id = when (key) {
                "÷" -> R.drawable.app_svg_division
                "x" -> R.drawable.app_svg_multiple
                "-" -> R.drawable.app_svg_minus
                "+" -> R.drawable.app_svg_plus
                else -> 0
            }
        ),
        contentDescription = null,
        modifier = Modifier.size(44.dp),
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
    )
}

@Preview
@Composable
fun FunctionIcon(key: String = "÷") {
    when (key) {
        "D" -> {
            Image(
                painter = painterResource(id = R.drawable.app_svg_delete),
                contentDescription = null,
                modifier = Modifier.size(44.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
        }
        else -> {
            Text(
                text = key,
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private val keys = listOf(
    "AC",
    "MC",
    "D",
    "÷",
    "7",
    "8",
    "9",
    "x",
    "4",
    "5",
    "6",
    "-",
    "1",
    "2",
    "3",
    "+",
    "0",
    ".",
    "",
    "="
)

private val digitalKeys = listOf(
    "7",
    "8",
    "9",
    "4",
    "5",
    "6",
    "1",
    "2",
    "3",
    "0",
    ".",
)

private val functionKeys = listOf(
    "AC",
    "MC",
    "D",
)

private val calculateKeys = listOf(
    "÷",
    "x",
    "-",
    "+",
)
