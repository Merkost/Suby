package com.merkost.suby.presentation.base

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.merkost.suby.R

@Composable
fun LogoImage(modifier: Modifier = Modifier) {
    val isDarkTheme = isSystemInDarkTheme()
    Image(
        modifier = modifier,
        painter = painterResource(id = if (isDarkTheme) R.drawable.suby_logo_white else R.drawable.suby_logo_black),
        contentDescription = "logo"
    )
}