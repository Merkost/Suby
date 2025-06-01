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
import com.merkost.suby.presentation.base.Icon
import com.merkost.suby.ui.theme.SubyTheme

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
            .padding(
                vertical = SubyTheme.spacing.small,
                horizontal = SubyTheme.spacing.extraSmall
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SubyTheme.spacing.small),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                tint = iconTint,
                modifier = Modifier.size(SubyTheme.spacing.iconMedium)
            )
            Spacer(modifier = Modifier.width(SubyTheme.spacing.small))
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