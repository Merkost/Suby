package com.merkost.suby.presentation.premium

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.merkost.suby.BuildConfig
import com.merkost.suby.presentation.base.BaseUiState
import com.merkost.suby.presentation.base.Icon
import com.merkost.suby.presentation.base.LogoImage
import com.merkost.suby.presentation.base.SubyTopAppBar
import com.merkost.suby.presentation.viewModel.BillingViewModel
import com.merkost.suby.presentation.viewModel.UiEvent
import com.merkost.suby.ui.theme.AppState
import com.merkost.suby.ui.theme.LocalAppState
import com.merkost.suby.ui.theme.StatusGreen
import com.merkost.suby.utils.analytics.ScreenLog
import com.merkost.suby.utils.analytics.Screens
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import suby.app.generated.resources.Res
import suby.app.generated.resources.error_title
import suby.app.generated.resources.feature_ad_free_title
import suby.app.generated.resources.feature_backup_sync_title
import suby.app.generated.resources.feature_frequent_currency_updates_title
import suby.app.generated.resources.feature_notification_reminder_title
import suby.app.generated.resources.feature_subscription_analytics_title
import suby.app.generated.resources.feature_unlimited_subscriptions_title
import suby.app.generated.resources.manage_subscription
import suby.app.generated.resources.okay
import suby.app.generated.resources.restore_purchase
import suby.app.generated.resources.subscription_active
import suby.app.generated.resources.subscription_active_message
import suby.app.generated.resources.success_title
import suby.app.generated.resources.unlock_premium_description
import suby.app.generated.resources.unlock_premium_features
import suby.app.generated.resources.upgrade_button_text

@Composable
fun PremiumFeaturesScreen(
    onBackClick: () -> Unit,
) {
    ScreenLog(Screens.Premium)
    val viewModel = koinViewModel<BillingViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalActivity.current

    LaunchedEffect(Unit) {
        if (BuildConfig.DEBUG.not()) {
            viewModel.loadProducts()
        }
    }
    HandlePurchaseDialogs(viewModel)

    PremiumScreenContent(
        uiState = uiState,
        onSubscribe = { activity?.let { viewModel.purchase(it) } },
        onRestorePurchase = viewModel::restorePurchase,
        onBackClick = onBackClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumScreenContent(
    uiState: BaseUiState<Unit>,
    onSubscribe: () -> Unit,
    onRestorePurchase: () -> Unit,
    onBackClick: () -> Unit,
) {
    val appState = LocalAppState.current
    val scrollBehaviour = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehaviour.nestedScrollConnection),
        topBar = {
            SubyTopAppBar(
                upPress = onBackClick,
                scrollBehavior = scrollBehaviour
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            HeaderSection(hasSubscription = appState.hasPremium)
            FeaturesList()
            UpgradeButtons(
                isLoading = uiState is BaseUiState.Loading,
                onSubscribeClick = onSubscribe,
                onRestoreClick = onRestorePurchase
            )
        }
    }
}

@Composable
fun UpgradeButtons(
    isLoading: Boolean,
    onSubscribeClick: () -> Unit,
    onRestoreClick: () -> Unit
) {
    val appState = LocalAppState.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (appState.hasPremium.not()) {
            SubscribeButton(
                isLoading = isLoading,
                onSubscribeClick = onSubscribeClick
            )
            TextButton(onClick = onRestoreClick, enabled = !isLoading) {
                Text(
                    text = stringResource(Res.string.restore_purchase),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        } else {
            TextButton(onClick = onSubscribeClick, enabled = !isLoading) {
                Text(
                    text = stringResource(Res.string.manage_subscription),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
fun HandlePurchaseDialogs(viewModel: BillingViewModel) {
    var showErrorDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    var messageRes by remember { mutableStateOf<StringResource?>(null) }

    LaunchedEffect(key1 = viewModel.uiEvent) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.ShowError -> {
                    messageRes = event.messageRes
                    showErrorDialog = true
                }

                is UiEvent.ShowSuccess -> {
                    messageRes = event.messageRes
                    showSuccessDialog = true
                }
            }
        }
    }

    if (showErrorDialog) {
        ErrorDialog(
            title = Res.string.error_title,
            messageRes = messageRes,
            onDismiss = { showErrorDialog = false }
        )
    }

    if (showSuccessDialog) {
        SuccessDialog(
            title = Res.string.success_title,
            messageRes = messageRes,
            onDismiss = { showSuccessDialog = false }
        )
    }
}


@Composable
fun HeaderSection(hasSubscription: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        LogoImage(
            modifier = Modifier.size(120.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (hasSubscription) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = stringResource(Res.string.subscription_active_message),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = stringResource(Res.string.subscription_active),
                tint = Color.StatusGreen,
                modifier = Modifier.size(40.dp)
            )

        } else {
            Text(
                text = stringResource(Res.string.unlock_premium_features),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.unlock_premium_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun FeaturesList() {
    val features = listOf(
        Feature(
            icon = Icons.AutoMirrored.Filled.List,
            title = stringResource(Res.string.feature_unlimited_subscriptions_title)
        ),
        Feature(
            icon = Icons.Default.CurrencyExchange,
            title = stringResource(Res.string.feature_frequent_currency_updates_title),
        ),
//        Feature(
//            icon = Icons.Default.SupportAgent,
//            title = stringResource(R.string.feature_priority_support_title)
//        ),
        Feature(
            icon = Icons.Default.RemoveCircle,
            title = stringResource(Res.string.feature_ad_free_title)
        ),
        Feature(
            icon = Icons.Default.Backup,
            title = stringResource(Res.string.feature_backup_sync_title),
            isComingSoon = true
        ),
        Feature(
            icon = Icons.Default.Notifications,
            title = stringResource(Res.string.feature_notification_reminder_title),
            isComingSoon = true
        ),
        Feature(
            icon = Icons.Default.Analytics,
            title = stringResource(Res.string.feature_subscription_analytics_title),
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
fun SubscribeButton(
    isLoading: Boolean,
    onSubscribeClick: () -> Unit,
) {
    ElevatedButton(
        enabled = !isLoading,
        onClick = onSubscribeClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AnimatedContent(isLoading) {
                if (it) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = stringResource(Res.string.upgrade_button_text),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
internal fun ErrorDialog(
    title: StringResource,
    messageRes: StringResource?,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(title))
        },
        text = {
            messageRes?.let {
                Text(text = stringResource(messageRes))
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(Res.string.okay))
            }
        }
    )
}

@Composable
internal fun SuccessDialog(
    title: StringResource,
    messageRes: StringResource?,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(title))
        },
        text = {
            messageRes?.let {
                Text(text = stringResource(messageRes))
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(Res.string.okay))
            }
        }
    )
}

@Preview
@Composable
fun PremiumFeaturesScreenPreview() {
    MaterialTheme {
        CompositionLocalProvider(LocalAppState provides AppState()) {
            PremiumScreenContent(
                uiState = BaseUiState.Success(Unit),
                onBackClick = {},
                onSubscribe = {},
                onRestorePurchase = {}
            )
        }
    }
}

@Preview
@Composable
fun PremiumFeaturesScreenSubscribedPreview() {
    MaterialTheme {
        CompositionLocalProvider(LocalAppState provides AppState(hasPremium = true)) {
            PremiumScreenContent(
                uiState = BaseUiState.Success(Unit),
                onBackClick = {},
                onSubscribe = {},
                onRestorePurchase = {}
            )
        }
    }
}