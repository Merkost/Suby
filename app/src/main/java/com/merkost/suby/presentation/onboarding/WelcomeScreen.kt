package com.merkost.suby.presentation.onboarding

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.merkost.suby.R
import com.merkost.suby.SubyShape
import com.merkost.suby.presentation.base.LogoImage
import com.merkost.suby.ui.theme.SubyTheme
import com.merkost.suby.utils.analytics.ScreenLog
import com.merkost.suby.utils.analytics.Screens

@Composable
fun WelcomeScreen(onContinue: () -> Unit) {
    ScreenLog(Screens.Greeting)

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { paddingModifier ->
        Column(
            modifier = Modifier
                .padding(paddingModifier)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(32.dp)
            ) {
                FloatingSubscriptionElements()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AnimatedLogo()

                    Spacer(modifier = Modifier.size(32.dp))

                    AnimatedAppName()

                    Spacer(modifier = Modifier.size(16.dp))

                    Text(
                        text = stringResource(R.string.welcome_tagline),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            EnhancedContinueButton(
                onContinue = onContinue,
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .padding(horizontal = 32.dp)
            )
        }
    }
}

private data class FloatingBubble(
    val emoji: String,
    val x: Float,
    val y: Float,
    val size: Int,
    val colorScheme: Int = 0, // 0=primary, 1=secondary, 2=tertiary
    val alpha: Float = 0.15f
)

@Composable
private fun FloatingSubscriptionElements() {
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    
    val float1 by infiniteTransition.animateFloat(
        initialValue = -20f, targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "float1"
    )
    
    val float2 by infiniteTransition.animateFloat(
        initialValue = -15f, targetValue = 25f,
        animationSpec = infiniteRepeatable(
            animation = tween(5200, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ), label = "float2"
    )
    
    val drift by infiniteTransition.animateFloat(
        initialValue = -10f, targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "drift"
    )
    
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f, targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "pulse"
    )
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rotation"
    )

    val bubbles = listOf(
        FloatingBubble("📺", 30f, 80f, 36, 0, 0.15f),
        FloatingBubble("🎵", 120f, 70f, 32, 1, 0.18f),
        FloatingBubble("☁️", 220f, 85f, 38, 2, 0.20f),
        FloatingBubble("📱", 310f, 75f, 34, 0, 0.12f),
        FloatingBubble("💳", 60f, 140f, 30, 1, 0.10f),
        FloatingBubble("🎮", 160f, 130f, 40, 2, 0.14f),
        FloatingBubble("💰", 270f, 145f, 28, 0, 0.08f),
        FloatingBubble("📊", 90f, 200f, 35, 1, 0.09f),
        FloatingBubble("🔔", 200f, 190f, 33, 2, 0.11f),
        FloatingBubble("⚡", 300f, 205f, 31, 0, 0.10f)
    )

    bubbles.forEachIndexed { index, bubble ->
        AnimatedBubble(
            bubble = bubble,
            floatOffset = if (index % 2 == 0) float1 else float2,
            driftOffset = drift * (if (index % 3 == 0) 1f else -0.8f),
            scale = pulse * (0.9f + (index % 3) * 0.05f),
            rotation = rotation * (if (index % 2 == 0) 1f else -1f) * 0.5f
        )
    }
}

@Composable
private fun AnimatedBubble(
    bubble: FloatingBubble,
    floatOffset: Float,
    driftOffset: Float,
    scale: Float,
    rotation: Float
) {
    val colorScheme = MaterialTheme.colorScheme
    val backgroundColor = when (bubble.colorScheme) {
        1 -> colorScheme.secondary.copy(alpha = bubble.alpha)
        2 -> colorScheme.tertiary.copy(alpha = bubble.alpha)
        else -> colorScheme.primary.copy(alpha = bubble.alpha)
    }

    Box(
        modifier = Modifier
            .offset(
                x = (bubble.x + driftOffset).dp, y = (bubble.y + floatOffset).dp
            )
            .size(bubble.size.dp)
            .scale(scale)
            .alpha(0.6f + (scale - 0.85f) * 2f)
            .rotate(rotation)
            .background(backgroundColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = bubble.emoji,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun AnimatedLogo() {
    LogoImage(
        modifier = Modifier.size(120.dp)
    )
}

@Composable
private fun AnimatedAppName() {
    val infiniteTransition = rememberInfiniteTransition(label = "appName")

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.7f, targetValue = 1f, animationSpec = infiniteRepeatable(
            animation = tween(1500), repeatMode = RepeatMode.Reverse
        ), label = "nameAlpha"
    )

    Text(
        text = stringResource(R.string.welcome_app_name),
        style = MaterialTheme.typography.displayMedium.copy(
            fontWeight = FontWeight.Bold
        ),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.alpha(alpha)
    )
}

@Composable
private fun EnhancedContinueButton(onContinue: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onContinue,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        shape = SubyShape,
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.btn_continue),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview
@Composable
fun WelcomeScreenPreview() {
    SubyTheme {
        WelcomeScreen(onContinue = {})
    }
} 