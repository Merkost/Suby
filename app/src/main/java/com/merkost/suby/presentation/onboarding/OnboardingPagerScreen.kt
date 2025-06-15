package com.merkost.suby.presentation.onboarding

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.merkost.suby.R
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.presentation.HorizontalPicker
import com.merkost.suby.presentation.rememberPickerState
import com.merkost.suby.presentation.screens.CurrencyLabel
import com.merkost.suby.presentation.viewModel.AppViewModel
import com.merkost.suby.ui.theme.SubyTheme
import com.merkost.suby.utils.analytics.ScreenLog
import com.merkost.suby.utils.analytics.Screens
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import dev.icerock.moko.permissions.notifications.REMOTE_NOTIFICATION
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

private enum class PermissionState {
    Unknown, Granted, Denied, DeniedAlways
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OnboardingPagerScreen(
    onComplete: (Currency) -> Unit,
    onEnableNotifications: () -> Unit,
    upPress: () -> Unit
) {
    ScreenLog(Screens.Onboarding)
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    var selectedCurrency by remember { mutableStateOf<Currency?>(null) }

    BackHandler {
        if (pagerState.currentPage == 0) {
            upPress()
        } else {
            scope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage - 1)
            }
        }
    }

    Scaffold(
        topBar = {
            Column {
                OnboardingProgressIndicator(
                    totalSteps = pagerState.pageCount,
                    currentStep = pagerState.currentPage + 1,
                    modifier = Modifier.statusBarsPadding()
                )
            }
        },
        modifier = Modifier.fillMaxSize(),
    ) { padding ->
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                userScrollEnabled = true
            ) { page ->
                when (page) {
                    0 -> CurrencySelectionPageContent(
                        onCurrencySelected = { currency ->
                            selectedCurrency = currency
                            scope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        }
                    )

                    1 -> NotificationSetupPageContent(
                        onEnable = {
                            onEnableNotifications()
                            scope.launch {
                                pagerState.animateScrollToPage(2)
                            }
                        },
                        onSkip = {
                            scope.launch {
                                pagerState.animateScrollToPage(2)
                            }
                        }
                    )

                    2 -> CompletePageContent(
                        onComplete = {
                            onComplete(selectedCurrency ?: Currency.USD)
                        }
                    )
                }
            }
        }
    }
}


@Composable
private fun CurrencySelectionPageContent(onCurrencySelected: (Currency) -> Unit) {
    val pickerState = rememberPickerState<Currency>()
    var selectedCurrency by remember { mutableStateOf<Currency?>(null) }
    val viewModel = koinViewModel<AppViewModel>()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.7f))

        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(500, delayMillis = 200)) + slideInVertically(
                animationSpec = tween(500, delayMillis = 200),
                initialOffsetY = { -it / 4 }
            )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "💰",
                    style = MaterialTheme.typography.displayMedium,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = stringResource(R.string.onboarding_currency_title),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = stringResource(R.string.onboarding_currency_description),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(500, delayMillis = 500)) + scaleIn(
                animationSpec = tween(500, delayMillis = 500),
                initialScale = 0.9f
            )
        ) {
            HorizontalPicker(
                items = Currency.entries,
                state = pickerState,
                visibleItemsCount = 5,
                modifier = Modifier,
                pickerItem = { item, modifier ->
                    CurrencyLabel(
                        modifier = modifier
                            .padding(vertical = 6.dp)
                            .padding(horizontal = 8.dp),
                        currency = item,
                        showArrow = false
                    )
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(400, delayMillis = 800)) + slideInVertically(
                animationSpec = tween(400, delayMillis = 800),
                initialOffsetY = { it / 2 }
            )
        ) {
            OnboardingPrimaryButton(
                text = stringResource(R.string.btn_proceed),
                onClick = {
                    selectedCurrency = pickerState.selectedItem
                    Timber.d("Selected currency: $selectedCurrency")
                    selectedCurrency?.let {
                        viewModel.updateFirstTimeOpening()
                        onCurrencySelected(it)
                    }
                },
                enabled = pickerState.selectedItem != null,
            )
        }
    }
}

@Composable
private fun NotificationSetupPageContent(
    onEnable: () -> Unit,
    onSkip: () -> Unit
) {

    val permissionsControllerFactory = rememberPermissionsControllerFactory()
    val permissionsController = remember(permissionsControllerFactory) {
        permissionsControllerFactory.createPermissionsController()
    }
    val scope = rememberCoroutineScope()
    
    var permissionState by remember { mutableStateOf(PermissionState.Unknown) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    
    BindEffect(permissionsController)
    
    LaunchedEffect(permissionsController) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val isGranted = permissionsController.isPermissionGranted(Permission.REMOTE_NOTIFICATION)
                permissionState = if (isGranted) PermissionState.Granted else PermissionState.Unknown
            } else {
                permissionState = PermissionState.Granted
            }
        } catch (_: Exception) {
            permissionState = PermissionState.Unknown
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.8f))

        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(500, delayMillis = 200)) + scaleIn(
                animationSpec = tween(500, delayMillis = 200),
                initialScale = 0.8f
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "🔔",
                    style = MaterialTheme.typography.displayMedium,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(500, delayMillis = 500)) + slideInVertically(
                animationSpec = tween(500, delayMillis = 500),
                initialOffsetY = { it / 4 }
            )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.onboarding_notifications_title),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = stringResource(R.string.onboarding_notifications_subtitle),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(500, delayMillis = 700)) + slideInVertically(
                animationSpec = tween(500, delayMillis = 700),
                initialOffsetY = { it / 3 }
            )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(400, delayMillis = 800)) +
                            slideInVertically(animationSpec = tween(400, delayMillis = 800)) +
                            scaleIn(
                                animationSpec = tween(400, delayMillis = 800),
                                initialScale = 0.8f
                            )
                ) {
                    BenefitCard(
                        icon = Icons.Default.NotificationsActive,
                        title = stringResource(R.string.benefit_smart_reminders_title),
                        description = stringResource(R.string.benefit_smart_reminders_description)
                    )
                }

                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(400, delayMillis = 1000)) +
                            slideInVertically(animationSpec = tween(400, delayMillis = 1000)) +
                            scaleIn(
                                animationSpec = tween(400, delayMillis = 1000),
                                initialScale = 0.8f
                            )
                ) {
                    BenefitCard(
                        icon = Icons.Default.Shield,
                        title = stringResource(R.string.benefit_avoid_surprises_title),
                        description = stringResource(R.string.benefit_avoid_surprises_description)
                    )
                }

                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(400, delayMillis = 1200)) +
                            slideInVertically(animationSpec = tween(400, delayMillis = 1200)) +
                            scaleIn(
                                animationSpec = tween(400, delayMillis = 1200),
                                initialScale = 0.8f
                            )
                ) {
                    BenefitCard(
                        icon = Icons.Default.AccountBalanceWallet,
                        title = stringResource(R.string.benefit_budget_control_title),
                        description = stringResource(R.string.benefit_budget_control_description)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1.2f))

        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(400, delayMillis = 800)) + slideInVertically(
                animationSpec = tween(400, delayMillis = 800),
                initialOffsetY = { it / 2 }
            )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OnboardingPrimaryButton(
                    text = when (permissionState) {
                        PermissionState.DeniedAlways -> stringResource(R.string.open_settings)
                        else -> stringResource(R.string.btn_enable_notifications)
                    },
                    onClick = {
                        when (permissionState) {
                            PermissionState.DeniedAlways -> {
                                permissionsController.openAppSettings()
                            }
                            else -> {
                                scope.launch {
                                    try {
                                        permissionsController.providePermission(Permission.REMOTE_NOTIFICATION)
                                        permissionState = PermissionState.Granted
                                        onEnable()
                                    } catch (e: DeniedAlwaysException) {
                                        permissionState = PermissionState.DeniedAlways
                                        showPermissionDialog = true
                                    } catch (e: DeniedException) {
                                        permissionState = PermissionState.Denied
                                        showPermissionDialog = true
                                    }
                                }
                            }
                        }
                    }
                )

                OnboardingSecondaryButton(
                    text = stringResource(R.string.btn_skip_notifications),
                    onClick = onSkip
                )
            }
        }
    }
    
    if (showPermissionDialog) {
        when (permissionState) {
            PermissionState.Denied -> {
                AlertDialog(
                    onDismissRequest = { showPermissionDialog = false },
                    title = { Text(stringResource(R.string.notification_permission_required)) },
                    text = { Text(stringResource(R.string.notification_permission_description)) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showPermissionDialog = false
                                scope.launch {
                                    try {
                                        permissionsController.providePermission(Permission.REMOTE_NOTIFICATION)
                                        permissionState = PermissionState.Granted
                                        onEnable()
                                    } catch (e: DeniedAlwaysException) {
                                        permissionState = PermissionState.DeniedAlways
                                        showPermissionDialog = true
                                    } catch (e: DeniedException) {
                                        permissionState = PermissionState.Denied
                                    }
                                }
                            }
                        ) {
                            Text(stringResource(R.string.grant_permission))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showPermissionDialog = false }) {
                            Text(stringResource(android.R.string.cancel))
                        }
                    }
                )
            }
            PermissionState.DeniedAlways -> {
                AlertDialog(
                    onDismissRequest = { showPermissionDialog = false },
                    title = { Text(stringResource(R.string.notification_permission_required)) },
                    text = { Text(stringResource(R.string.permission_denied_message)) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showPermissionDialog = false
                                permissionsController.openAppSettings()
                            }
                        ) {
                            Text(stringResource(R.string.open_settings))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showPermissionDialog = false }) {
                            Text(stringResource(android.R.string.cancel))
                        }
                    }
                )
            }
            else -> {}
        }
    }
}

@Composable
private fun CompletePageContent(onComplete: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        AnimatedVisibility(
            visible = true,
            enter = scaleIn(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow,
                )
            ) + fadeIn(animationSpec = tween(500, delayMillis = 200))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "🎉",
                    style = MaterialTheme.typography.displayLarge,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(500, delayMillis = 600)) + slideInVertically(
                animationSpec = tween(500, delayMillis = 600),
                initialOffsetY = { it / 4 }
            )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.onboarding_ready_title),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = stringResource(R.string.onboarding_ready_description),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.weight(1.5f))

        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(400, delayMillis = 1000)) + slideInVertically(
                animationSpec = tween(400, delayMillis = 1000),
                initialOffsetY = { it / 2 }
            )
        ) {
            OnboardingPrimaryButton(
                text = stringResource(R.string.btn_get_started),
                onClick = onComplete
            )
        }
    }
}

@Composable
private fun BenefitCard(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview
@Composable
fun OnboardingPagerScreenPreview() {
    SubyTheme {
        OnboardingPagerScreen(
            onComplete = { },
            upPress = {},
            onEnableNotifications = { },
        )
    }
} 