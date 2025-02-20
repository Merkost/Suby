package com.merkost.suby.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.merkost.suby.R

@Composable
fun EmptySubscriptions(onAddClicked: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {

        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.norecentsearches))
        LottieAnimation(
            composition,
            iterations = Int.MAX_VALUE,
            modifier = Modifier.height(350.dp),
        )

        Button(onClick = onAddClicked) {
            Text(text = stringResource(R.string.btn_add_first_subscription))
        }
    }
}