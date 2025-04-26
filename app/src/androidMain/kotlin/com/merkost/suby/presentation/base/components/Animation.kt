package com.merkost.suby.presentation.base.components

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith

fun screenStateTransitionSpec(): ContentTransform {
    return (fadeIn(tween(500))).togetherWith(
        fadeOut(animationSpec = tween(500))
    )
}