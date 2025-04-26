package com.merkost.suby.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.merkost.suby.presentation.base.components.LottieLoading
import com.merkost.suby.utils.LottieFiles
import org.jetbrains.compose.resources.stringResource
import suby.app.generated.resources.Res
import suby.app.generated.resources.btn_add_first_subscription

@Composable
fun EmptySubscriptions(onAddClicked: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        LottieLoading(
            modifier = Modifier.height(350.dp),
            file = LottieFiles.EmptySubscriptions
        )

        Button(onClick = onAddClicked) {
            Text(text = stringResource(Res.string.btn_add_first_subscription))
        }
    }
}