package com.merkost.suby.presentation.base

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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
            Text(text = "Save")
        }
    }
}