package com.merkost.suby.presentation.base.components.subscription

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.merkost.suby.SubyShape
import com.merkost.suby.model.entity.Status
import com.merkost.suby.ui.theme.SubyTheme


@Composable
fun StatusBubble(
    modifier: Modifier = Modifier,
    status: Status,
    backgroundColor: Color = status.color.copy(alpha = 0.2f),
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
                .background(status.color)
        )

        Text(
            text = status.statusName,
            style = textStyle,
            color = status.color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StatusBubbleLightPreview() {
    SubyTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Status.entries.forEach { status ->
                StatusBubble(status = status)
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
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Status.entries.forEach { status ->
                StatusBubble(status = status)
            }
        }
    }
}