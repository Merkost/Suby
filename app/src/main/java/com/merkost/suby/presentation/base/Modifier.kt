package com.merkost.suby.presentation.base

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import com.merkost.suby.R
import com.merkost.suby.showToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.rotatingOnClick(
    rotationAngle: Float = 360f,
    durationMillis: Int = 600,
    onClick: () -> Unit
): Modifier = composed {
    var isRotated by remember { mutableStateOf(false) }

    val animatedRotation by animateFloatAsState(
        targetValue = if (isRotated) rotationAngle else 0f,
        animationSpec = tween(durationMillis = durationMillis)
    )

    this
        .rotate(animatedRotation)
        .pointerInteropFilter {
            if (it.action == android.view.MotionEvent.ACTION_UP) {
                isRotated = !isRotated
                onClick()
            }
            true
        }
}

fun Modifier.noRippleClickable(
    enabled: Boolean = true,
    onClick: () -> Unit
) = composed {
    clickable(enabled = enabled, interactionSource = null, indication = null) {
        onClick()
    }
}

@Composable
fun DoubleBackPressHandler(enabled: Boolean = true) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isBackPressed = remember { mutableStateOf(false) }
    BackHandler(enabled && !isBackPressed.value) {
        isBackPressed.value = true
        context.showToast(R.string.press_back_to_exit)
        scope.launch {
            delay(2000L)
            isBackPressed.value = false
        }
    }
}