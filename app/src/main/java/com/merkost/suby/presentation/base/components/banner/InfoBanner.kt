package com.merkost.suby.presentation.base.components.banner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.merkost.suby.ui.theme.SubyTheme

enum class BannerType {
    INFO, SUCCESS, WARNING, ERROR
}

/**
 * A lightweight, animated banner that slides + fades into view when `visible` is true,
 * showing a leading icon and a text message. You can customize background & content colors.
 */
@Composable
fun InfoBanner(
    visible: Boolean,
    message: String,
    bannerType: BannerType = BannerType.INFO,
    modifier: Modifier = Modifier
) {
    val colors = getBannerColors(bannerType)
    val icon = getBannerIcon(bannerType)

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(durationMillis = 300)) +
                slideInVertically(
                    initialOffsetY = { fullHeight -> -fullHeight },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ),
        exit = fadeOut(animationSpec = tween(durationMillis = 250)) +
                slideOutVertically(
                    targetOffsetY = { fullHeight -> -fullHeight },
                    animationSpec = tween(durationMillis = 300)
                ),
        modifier = modifier
    ) {
        Surface(
            shadowElevation = 4.dp,
            shape = MaterialTheme.shapes.large,
            color = colors.backgroundColor,
            contentColor = colors.contentColor,
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(colors.contentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = colors.contentColor
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = colors.contentColor
                )
            }
        }
    }
}

@Composable
fun InfoBanner(
    visible: Boolean,
    iconComposable: @Composable () -> Unit,
    message: String,
    backgroundColor: Color = SubyTheme.colors.banner.infoBannerBackground,
    contentColor: Color = SubyTheme.colors.banner.infoBannerContent,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { -it / 2 },
            animationSpec = tween(durationMillis = 500, easing = EaseOutCubic)
        ) + fadeIn(animationSpec = tween(durationMillis = 300)),
        exit = slideOutVertically(
            targetOffsetY = { -it / 2 },
            animationSpec = tween(durationMillis = 300, easing = EaseInCubic)
        ) + fadeOut(animationSpec = tween(durationMillis = 200)),
        modifier = modifier
    ) {
        Surface(
            shadowElevation = 4.dp,
            shape = MaterialTheme.shapes.large,
            color = backgroundColor,
            contentColor = contentColor,
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(contentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    iconComposable()
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = contentColor
                )
            }
        }
    }
}

@Composable
private fun getBannerColors(bannerType: BannerType): BannerColors {
    return when (bannerType) {
        BannerType.INFO -> BannerColors(
            backgroundColor = SubyTheme.colors.banner.infoBannerBackground,
            contentColor = SubyTheme.colors.banner.infoBannerContent
        )

        BannerType.SUCCESS -> BannerColors(
            backgroundColor = SubyTheme.colors.banner.successBannerBackground,
            contentColor = SubyTheme.colors.banner.successBannerContent
        )

        BannerType.WARNING -> BannerColors(
            backgroundColor = SubyTheme.colors.banner.warningBannerBackground,
            contentColor = SubyTheme.colors.banner.warningBannerContent
        )

        BannerType.ERROR -> BannerColors(
            backgroundColor = SubyTheme.colors.banner.errorBannerBackground,
            contentColor = SubyTheme.colors.banner.errorBannerContent
        )
    }
}

@Composable
private fun getBannerIcon(bannerType: BannerType): ImageVector {
    return when (bannerType) {
        BannerType.INFO -> Icons.Default.Info
        BannerType.SUCCESS -> Icons.Default.CheckCircle
        BannerType.WARNING -> Icons.Default.Warning
        BannerType.ERROR -> Icons.Default.Error
    }
}

private data class BannerColors(
    val backgroundColor: Color,
    val contentColor: Color
)

@Preview
@Composable
fun InfoBannerPreview() {
    SubyTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InfoBanner(
                visible = true,
                message = "This is an info banner with important information",
                bannerType = BannerType.INFO
            )

            InfoBanner(
                visible = true,
                message = "Operation completed successfully!",
                bannerType = BannerType.SUCCESS
            )

            InfoBanner(
                visible = true,
                message = "Please check your subscription details",
                bannerType = BannerType.WARNING
            )

            InfoBanner(
                visible = true,
                message = "An error occurred while processing",
                bannerType = BannerType.ERROR
            )
        }
    }
}