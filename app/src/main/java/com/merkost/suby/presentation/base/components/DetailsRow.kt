package com.merkost.suby.presentation.base.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.merkost.suby.presentation.base.Icon

@Composable
fun DetailsRow(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: ImageVector,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    labelStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    valueStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
    labelColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
    valueColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = labelStyle,
                color = labelColor
            )
        }

        Text(
            text = value,
            style = valueStyle,
            color = valueColor
        )
    }
} 