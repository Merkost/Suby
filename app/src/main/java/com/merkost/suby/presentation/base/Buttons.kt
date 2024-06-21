package com.merkost.suby.presentation.base

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.merkost.suby.R

@Composable
fun SaveButton(modifier: Modifier = Modifier, enabled: Boolean, onClick: () -> Unit) {
    FilledTonalButton(modifier = modifier, onClick = onClick, enabled = enabled) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier.size(32.dp),
                painter = painterResource(id = R.drawable.suby_logo_white),
                contentDescription = ""
            )
            Spacer(Modifier.size(4.dp))
            Text(text = "Save", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
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
            shape = com.merkost.suby.SubyShape,
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