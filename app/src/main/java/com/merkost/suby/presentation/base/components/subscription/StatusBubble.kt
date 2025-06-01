package com.merkost.suby.presentation.base.components.subscription

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merkost.suby.SubyShape
import com.merkost.suby.model.entity.Status
import com.merkost.suby.ui.theme.SubyTheme

@Composable
internal fun BaseBubble(
    modifier: Modifier = Modifier,
    text: String,
    dotColor: Color,
    textColor: Color,
    backgroundColor: Color,
    padding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(4.dp),
    textStyle: TextStyle = MaterialTheme.typography.bodySmall
) {
    Row(
        modifier = modifier
            .clip(SubyShape)
            .background(backgroundColor)
            .padding(padding)
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = horizontalArrangement
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .padding(2.dp)
                .heightIn(max = 4.dp)
                .clip(CircleShape)
                .background(dotColor)
        )

        Text(
            text = text,
            style = textStyle,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun StatusBubble(
    modifier: Modifier = Modifier,
    status: Status,
    padding: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
    textStyle: TextStyle = MaterialTheme.typography.labelMedium
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        status.color.copy(alpha = 0.12f),
                        status.color.copy(alpha = 0.08f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        status.color.copy(alpha = 0.6f),
                        status.color.copy(alpha = 0.4f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .shadow(1.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                status.color,
                                status.color.copy(alpha = 0.8f)
                            ),
                            radius = 12f
                        )
                    )
            )

            Text(
                text = status.statusName,
                style = textStyle.copy(
                    letterSpacing = 0.5.sp
                ),
                color = status.color.copy(alpha = 0.9f),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun TrialBubble(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
    textStyle: TextStyle = MaterialTheme.typography.labelMedium
) {
    val trialColor = Color(0xFFB8860B)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        trialColor.copy(alpha = 0.12f),
                        trialColor.copy(alpha = 0.08f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        trialColor.copy(alpha = 0.6f),
                        trialColor.copy(alpha = 0.4f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Trial",
                style = textStyle.copy(
                    letterSpacing = 0.5.sp
                ),
                color = trialColor.copy(alpha = 0.9f),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun StatusBubbleCompact(
    modifier: Modifier = Modifier,
    status: Status
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = status.color,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(status.color)
            )

            Text(
                text = status.statusName,
                style = MaterialTheme.typography.labelSmall,
                color = status.color,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun StatusDot(
    modifier: Modifier = Modifier,
    status: Status,
    size: Dp = 8.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(status.color)
    )
}

@Composable
fun TrialBadge(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(16.dp)
            .shadow(
                elevation = 2.dp,
                shape = CircleShape,
                clip = false
            )
            .clip(CircleShape)
            .background(Color(0xFFFFD700))
            .padding(1.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .padding(1.dp)
            .clip(CircleShape)
            .background(Color(0xFFFFD700)),
    )
}

@Preview(showBackground = true)
@Composable
private fun StatusBubbleLightPreview() {
    SubyTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "Regular Status Bubbles:", style = MaterialTheme.typography.labelMedium)
            Status.entries.forEach { status ->
                StatusBubble(status = status)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Compact Status Bubbles:", style = MaterialTheme.typography.labelMedium)
            Status.entries.forEach { status ->
                StatusBubbleCompact(status = status)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Status Dots:", style = MaterialTheme.typography.labelMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Status.entries.forEach { status ->
                    StatusDot(status = status)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Trial Components:", style = MaterialTheme.typography.labelMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TrialBubble()
                TrialBadge()
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Active Status Dot + Trial Badge:",
                style = MaterialTheme.typography.labelMedium
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusDot(status = Status.ACTIVE)
                TrialBadge()
            }
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun StatusBubbleDarkPreview() {
    SubyTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "Regular Status Bubbles:", style = MaterialTheme.typography.labelMedium)
                Status.entries.forEach { status ->
                    StatusBubble(status = status)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Compact Status Bubbles:", style = MaterialTheme.typography.labelMedium)
                Status.entries.forEach { status ->
                    StatusBubbleCompact(status = status)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Status Dots:", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Status.entries.forEach { status ->
                        StatusDot(status = status)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Trial Components:", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TrialBubble()
                    TrialBadge()
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Active Status Dot + Trial Badge:",
                    style = MaterialTheme.typography.labelMedium
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusDot(status = Status.ACTIVE)
                    TrialBadge()
                }
            }
        }
    }
}