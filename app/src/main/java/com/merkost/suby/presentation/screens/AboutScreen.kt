package com.merkost.suby.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merkost.suby.BuildConfig
import com.merkost.suby.presentation.base.LogoImage
import com.merkost.suby.presentation.base.SubyHugeButton
import com.merkost.suby.presentation.base.SubyTopAppBar
import com.merkost.suby.ui.theme.SubyTheme
import com.merkost.suby.utils.analytics.ScreenLog
import com.merkost.suby.utils.analytics.Screens
import com.merkost.suby.utils.sendSupportEmail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    upPress: () -> Unit,
) {
    ScreenLog(Screens.About)
    val context = LocalContext.current

    Scaffold(
        topBar = {
            SubyTopAppBar(
                title = { Text("About") },
                upPress = upPress
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            LogoImage(
                modifier = Modifier
                    .padding(16.dp)
                    .size(100.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
            ) {
                Text(
                    text = "Suby",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "v${BuildConfig.VERSION_NAME}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "Questions or feedback?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }

            SubyHugeButton(
                onClick = { context.sendSupportEmail() },
                text = "Contact Us",
            )
        }
    }
}

@Preview
@Composable
fun AboutScreenPreview() {
    SubyTheme {
        AboutScreen(upPress = {})
    }
} 