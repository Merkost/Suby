package com.merkost.suby.presentation.base

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubyTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    upPress: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    TopAppBar(
        modifier = modifier,
        navigationIcon = {
            upPress?.let {
                IconButton(onClick = upPress) {
                    Icon(imageVector = Icons.Rounded.ArrowBackIosNew)
                }
            }
        },
        title = {
            ProvideTextStyle(value = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)) {
                title()
            }
        },
        actions = actions,
        scrollBehavior = scrollBehavior,
    )
}