package com.merkost.suby.presentation.onboarding

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.merkost.suby.presentation.base.LogoImage
import com.merkost.suby.ui.theme.SubyTheme
import com.merkost.suby.viewModel.AppViewModel

@Composable
fun GreetingScreen(onContinueClick: () -> Unit) {
    val systemUiController = rememberSystemUiController()
    val isDarkTheme = isSystemInDarkTheme()
    val viewModel: AppViewModel = hiltViewModel()

    SideEffect {
        systemUiController.setSystemBarsColor(
            Color.Transparent,
            darkIcons = !isDarkTheme
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(PaddingValues(32.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LogoImage(
                modifier = Modifier
                    .size(250.dp)
                    .weight(3f),
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Suby",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                )
                Text(
                    text = "Manage Subscriptions",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.W600,
                        textAlign = TextAlign.Center
                    )
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                FilledTonalButton(
                    modifier = Modifier.align(Alignment.Center),
                    onClick = {
                        viewModel.updateFirstTimeOpening()
                        onContinueClick()
                    },
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text(
                        text = "Continue",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun GreetingScreenPreview() {
    SubyTheme {
        GreetingScreen {}
    }
}