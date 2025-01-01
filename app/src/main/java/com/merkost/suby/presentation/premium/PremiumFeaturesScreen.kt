package com.merkost.suby.presentation.premium

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.merkost.suby.R
import com.merkost.suby.model.billing.ChooseSubscription
import com.merkost.suby.presentation.base.LogoImage
import com.merkost.suby.presentation.base.SubyTopAppBar
import com.merkost.suby.ui.theme.LocalActivity
import com.merkost.suby.utils.analytics.ScreenLog
import com.merkost.suby.utils.analytics.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumFeaturesScreen(
    onBackClick: () -> Unit,
) {
    ScreenLog(Screens.Premium)
    val activity = LocalActivity.current

    val chooseSubscription = remember {
        ChooseSubscription(activity)
    }

    LaunchedEffect(key1 = true) {
        chooseSubscription.billingSetup()
        chooseSubscription.hasSubscription()
    }

    val currentSubscription by chooseSubscription.subscriptions.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            SubyTopAppBar(upPress = onBackClick)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            HeaderSection()
            FeaturesList()
            SubscribeButton(true) {
                chooseSubscription.checkSubscriptionStatus("basic-plan")
            }
        }
    }
}

@Composable
fun HeaderSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        LogoImage(
            modifier = Modifier.size(120.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.unlock_premium_features),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.unlock_premium_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun FeaturesList() {
    val features = listOf(
        Feature(
            icon = Icons.AutoMirrored.Filled.List,
            title = stringResource(R.string.feature_unlimited_subscriptions_title)
        ),
        Feature(
            icon = Icons.Default.SupportAgent,
            title = stringResource(R.string.feature_priority_support_title)
        ),
        Feature(
            icon = Icons.Default.RemoveCircle,
            title = stringResource(R.string.feature_ad_free_title)
        ),
        Feature(
            icon = Icons.Default.Backup,
            title = stringResource(R.string.feature_backup_sync_title),
            isComingSoon = true
        ),
        Feature(
            icon = Icons.Default.Notifications,
            title = stringResource(R.string.feature_notification_reminder_title),
            isComingSoon = true
        ),
        Feature(
            icon = Icons.Default.Analytics,
            title = stringResource(R.string.feature_subscription_analytics_title),
            isComingSoon = true
        )
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        features.forEach { feature ->
            FeatureRow(feature = feature)
        }
    }
}


@Composable
fun SubscribeButton(isReady: Boolean, onSubscribeClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ElevatedButton(
            enabled = isReady,
            onClick = onSubscribeClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = stringResource(R.string.upgrade_button_text),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.thanks_description),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PremiumFeaturesScreenPreview() {
    MaterialTheme {
        PremiumFeaturesScreen(
            onBackClick = { /* TODO: Handle back navigation */ }
        )
    }
}