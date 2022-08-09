package github.xuqk.kdcalculator

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.withSaveLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import github.xuqk.kdcalculator.ui.theme.KDCalculatorTheme
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var showList: List<String> by mutableStateOf(emptyList())
    private var inputString: String by mutableStateOf("")
    private var calculateError: Boolean = false

    private var calculateComplete: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen(
                calculateError = calculateError,
                showList = showList,
                inputString = inputString,
                onClick = ::input
            )
        }
    }

    private fun input(s: String) {
        when (s) {
            in digitalKeys -> {
                if (calculateComplete) {
                    showList = emptyList()
                    inputString = s
                } else {
                    if (s == ".") {
                        if (!inputString.contains(".")) {
                            inputString += s
                        }
                    } else {
                        val split = inputString.split(".")
                        if (split.any { it.length < 4 }) {
                            inputString += s
                        }
                    }
                }
                calculateError = false
                calculateComplete = false
            }
            in functionKeys -> {
                when (s) {
                    "C" -> {
                        showList = emptyList()
                        inputString = ""
                        calculateComplete = false
                    }
                    "D" -> {
                        if (!calculateComplete) {
                            if (inputString.isEmpty()) {
                                // 当前输入为空，删除显示行最后一个字符
                                val last = showList.lastOrNull() ?: return
                                showList = showList.dropLast(1)
                                if (last.length > 1) {
                                    showList = showList + last.dropLast(1)
                                }
                            } else {
                                inputString = inputString.dropLast(1)
                            }
                        }
                    }
                }
                calculateError = false
            }
            in calculateKeys -> {
                if (calculateComplete) {
                    showList = if (calculateError) {
                        listOf(s)
                    } else {
                        listOf(inputString, s)
                    }

                    inputString = ""
                } else {
                    showList = if (inputString.isEmpty()) {
                        showList + s
                    } else {
                        showList + inputString + s
                    }
                    inputString = ""
                }
                calculateError = false
                calculateComplete = false
            }
            "=" -> {
                if (inputString.isNotEmpty()) {
                    showList = showList + inputString
                }
                inputString = try {
                    calculateError = false
                    getResult(showList)
                } catch (e: Exception) {
                    calculateError = true
                    "算式格式错误"
                }
                calculateComplete = true
            }
        }
    }
}

@Preview
@Composable
fun MainScreen(
    calculateError: Boolean = false,
    showList: List<String> = emptyList(),
    inputString: String = "",
    onClick: (String) -> Unit = {}
) {
    var currX by remember { mutableStateOf(-1f) }
    var currY by remember { mutableStateOf(-1f) }

    var pressState by remember { mutableStateOf(0f) }
    var animSign by remember { mutableStateOf(0) }

    LaunchedEffect(animSign) {
        if (animSign > 0) {
            // 按下
            Animatable(0f).animateTo(1f) {
                pressState = value
            }
            // 回弹
            Animatable(1f).animateTo(0f) {
                pressState = value
            }
        }
    }
    KDCalculatorTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            val maxRotation = remember { 1f }
            var size = remember { IntSize.Zero }
            var columnOffsetInWindow = remember { Offset.Zero }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned {
                        size = it.size
                        columnOffsetInWindow = it.positionInWindow()
                    }
                    .graphicsLayer {
                        if (pressState == 0f || currX < 0 && currY < 0 || size == IntSize.Zero) return@graphicsLayer
                        val centerX = size.width / 2
                        val centerY = size.height / 2
                        if (currX < centerX) {
                            if (currY < centerY) {
                                // 第一象限
                                rotationX =
                                    (centerY - currY) / centerY * maxRotation * pressState * 0.5f
                                rotationY = -(centerX - currX) / centerX * maxRotation * pressState
                            } else {
                                // 第四象限
                                rotationX =
                                    -(currY - centerY) / centerY * maxRotation * pressState * 0.5f
                                rotationY = -(centerX - currX) / centerX * maxRotation * pressState
                            }
                        } else {
                            if (currY < centerY) {
                                // 第二象限
                                rotationX =
                                    -(currY - centerY) / centerY * maxRotation * pressState * 0.5f
                                rotationY = (currX - centerX) / centerX * maxRotation * pressState
                            } else {
                                // 第三象限
                                rotationX =
                                    (centerY - currY) / centerY * maxRotation * pressState * 0.5f
                                rotationY = (currX - centerX) / centerX * maxRotation * pressState
                            }
                        }


                    }
                    .background(color = MaterialTheme.colorScheme.background)
            ) {

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.background),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.End
                ) {
                    val state = rememberScrollState()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(state = state)
                            .padding(8.dp)
                            .onSizeChanged {
                                MainScope().launch {
                                    state.scrollTo(Int.MAX_VALUE)
                                }
                            },
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        showList.forEach {
                            if (it in calculateKeys) {
                                Image(
                                    painter = painterResource(
                                        id = when (it) {
                                            "÷" -> R.drawable.app_svg_division
                                            "x" -> R.drawable.app_svg_multiple
                                            "-" -> R.drawable.app_svg_minus
                                            "+" -> R.drawable.app_svg_plus
                                            ")" -> R.drawable.app_svg_bracket_right
                                            "(" -> R.drawable.app_svg_bracket_left
                                            else -> 0
                                        }
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(36.dp),
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                                )
                            } else {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = MaterialTheme.colorScheme.onBackground,
                                )
                            }
                        }
                    }

                    Text(
                        text = inputString,
                        style = MaterialTheme.typography.displayLarge,
                        modifier = Modifier.padding(8.dp),
                        color = if (calculateError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground
                    )
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
                    modifier = Modifier
                        .shadow(8.dp)
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .navigationBarsPadding(),
                    userScrollEnabled = false
                ) {
                    items(
                        items = keys,
                        key = { it },
                    ) { item ->
                        when {
                            functionKeys.contains(item) -> {
                                CalculatorButton(onClick = {
                                    val offset = it.center - columnOffsetInWindow
                                    currX = offset.x
                                    currY = offset.y
                                    animSign++
                                    onClick(item)
                                }) {
                                    FunctionIcon(item)
                                }
                            }
                            calculateKeys.contains(item) -> {
                                CalculatorButton(onClick = {
                                    val offset = it.center - columnOffsetInWindow
                                    currX = offset.x
                                    currY = offset.y
                                    animSign++
                                    onClick(item)
                                }) {
                                    CalculateIcon(item)
                                }
                            }
                            digitalKeys.contains(item) -> {
                                CalculatorButton(onClick = {
                                    val offset = it.center - columnOffsetInWindow
                                    currX = offset.x
                                    currY = offset.y
                                    animSign++
                                    onClick(item)
                                }) {
                                    Text(
                                        text = item,
                                        style = MaterialTheme.typography.displayMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                            item == "=" -> {
                                CalculatorButton(onClick = {
                                    val offset = it.center - columnOffsetInWindow
                                    currX = offset.x
                                    currY = offset.y
                                    animSign++
                                    onClick(item)
                                }) {
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
    onClick: (Rect) -> Unit = {},
    content: @Composable BoxScope.() -> Unit
) {
    var boundsInWindow = remember { Rect.Zero }
    val rippleColor = MaterialTheme.colorScheme.primary
    val maxAlpha = remember { 0.3f }
    var animSign by remember { mutableStateOf(0) }
    var radius0 by remember { mutableStateOf(0f) }
    var radius1 by remember { mutableStateOf(0f) }
    var radius2 by remember { mutableStateOf(0f) }
    var alpha0 by remember { mutableStateOf(maxAlpha) }
    var alpha1 by remember { mutableStateOf(maxAlpha) }
    var alpha2 by remember { mutableStateOf(maxAlpha) }
    LaunchedEffect(animSign) {
        if (animSign > 0) {
            Animatable(initialValue = 0f).animateTo(
                targetValue = 0.9f,
                tween(durationMillis = 600)
            ) {
                radius0 = value + 1
                alpha0 = (0.9f - value) / 0.9f * maxAlpha
                if (targetValue == value) {
                    radius0 = 0f
                }
            }
        }
    }
    LaunchedEffect(animSign) {
        if (animSign > 0) {
            Animatable(initialValue = 0f).animateTo(
                targetValue = 0.6f,
                tween(durationMillis = 600, delayMillis = 200)
            ) {
                radius1 = value + 1
                alpha1 = (0.6f - value) / 0.9f * maxAlpha
                if (targetValue == value) {
                    radius1 = 0f
                }
            }
        }
    }
    LaunchedEffect(animSign) {
        if (animSign > 0) {
            Animatable(initialValue = 0f).animateTo(
                targetValue = 0.3f,
                tween(durationMillis = 600, delayMillis = 400)
            ) {
                radius2 = value + 1
                alpha2 = (0.3f - value) / 0.9f * maxAlpha
                if (targetValue == value) {
                    radius2 = 0f
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .padding(8.dp)
            .aspectRatio(1f)
            .fillMaxWidth()
            .drawBehind {
                if (radius0 > 1f) {
                    drawContext.canvas.withSaveLayer(
                        Rect(center, size.width),
                        Paint()
                    ) {
                        drawCircle(color = Color.Black)
                        drawCircle(
                            color = rippleColor,
                            radius = size.minDimension / 2f * radius0,
                            alpha = alpha0,
                            blendMode = BlendMode.SrcOut
                        )

                    }
                }
                if (radius1 > 1f) {
                    drawContext.canvas.withSaveLayer(
                        Rect(center, size.width),
                        Paint()
                    ) {
                        drawCircle(color = Color.Black)
                        drawCircle(
                            color = rippleColor,
                            radius = size.minDimension / 2f * radius1,
                            alpha = alpha1,
                            blendMode = BlendMode.SrcOut
                        )

                    }
                }
                if (radius2 > 1f) {
                    drawContext.canvas.withSaveLayer(
                        Rect(center, size.width),
                        Paint()
                    ) {
                        drawCircle(color = Color.Black)
                        drawCircle(
                            color = rippleColor,
                            radius = size.minDimension / 2f * radius2,
                            alpha = alpha2,
                            blendMode = BlendMode.SrcOut
                        )

                    }
                }
            }
            .onGloballyPositioned {
                boundsInWindow = it.boundsInWindow()
            }
            .pointerInput(Unit) {
                forEachGesture {
                    awaitPointerEventScope {
                        val firstDown = awaitFirstDown()
                        if (firstDown.pressed) {
                            animSign++
                            onClick(boundsInWindow)
                            Log.d("TAG_LOG", "CalculatorButton: onPress")
                        }
                        return@awaitPointerEventScope
                    }
                }
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
                ")" -> R.drawable.app_svg_bracket_right
                "(" -> R.drawable.app_svg_bracket_left
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
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
