package com.merkost.suby.presentation.base

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TitleColumn(
    modifier: Modifier = Modifier,
    title: String,
    infoInformation: AnnotatedString? = null,
    actions: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val rowModifier = if (actions != null) Modifier.fillMaxWidth() else Modifier
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = rowModifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                infoInformation?.let {
                    val infoMenuOpened = remember {
                        mutableStateOf(false)
                    }
                    Icon(Icons.Outlined.Info, modifier = Modifier
                        .clip(CircleShape)
                        .clickable(role = Role.DropdownList) {
                            infoMenuOpened.value = true
                        })
                    DropdownMenu(expanded = infoMenuOpened.value, onDismissRequest = {
                        infoMenuOpened.value = false
                    }) {
                        DropdownMenuItem(
                            modifier = Modifier.padding(vertical = 2.dp),
                            text = {
                                Text(text = infoInformation)
                            },
                            onClick = { infoMenuOpened.value = false })
                    }
                }
            }
            actions?.let {
                actions()
            }

        }
        content()
    }
}