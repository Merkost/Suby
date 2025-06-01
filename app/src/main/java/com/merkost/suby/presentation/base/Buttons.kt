package com.merkost.suby.presentation.base

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.merkost.suby.R
import com.merkost.suby.SubyShape

@Composable
fun SubyButton(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    shape: Shape = SubyShape,
    onClick: () -> Unit
) {
    Button(modifier = modifier, enabled = enabled, onClick = onClick, shape = shape) {
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = text,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun SubyHugeButton(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    shape: Shape = SubyShape,
    onClick: () -> Unit
) {
    Button(modifier = modifier, enabled = enabled, onClick = onClick, shape = shape) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = text,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun SaveButton(modifier: Modifier = Modifier, enabled: Boolean, onClick: () -> Unit) {
    Button(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AnimatedContent(enabled) {
                if (it) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            modifier = Modifier.size(32.dp),
                            painter = painterResource(id = R.drawable.suby_logo_black),
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                        )
                        Spacer(Modifier.size(4.dp))
                    }
                }
            }
            Text(
                text = stringResource(R.string.btn_save),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun SubyIconButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    text: String,
    contentDescription: String? = null,
    outlined: Boolean = true,
    fullWidth: Boolean = false,
    iconSize: Int = 18,
    onClick: () -> Unit
) {
    val buttonModifier = if (fullWidth) modifier.fillMaxWidth() else modifier
    
    if (outlined) {
        OutlinedButton(
            onClick = onClick,
            modifier = buttonModifier,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Icon(
                imageVector = icon,
                modifier = Modifier.size(iconSize.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                textAlign = TextAlign.Center
            )
        }
    } else {
        Button(
            onClick = onClick,
            modifier = buttonModifier,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Icon(
                imageVector = icon,
                 modifier = Modifier.size(iconSize.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BaseItem(
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
        contentColor = MaterialTheme.colorScheme.onPrimary
    ),
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    onClick?.let {
        Card(
            modifier = modifier,
            colors = colors,
            shape = SubyShape,
        ) {
            Box(
                modifier = Modifier
                    .combinedClickable(
                        onClick = onClick,
                        onLongClick = onLongClick,
                        role = Role.Tab,
                    )
                    .padding(16.dp), contentAlignment = Alignment.Center
            ) {
                content()
            }
        }
    } ?: run {
        Card(colors = colors) {
            Box(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}