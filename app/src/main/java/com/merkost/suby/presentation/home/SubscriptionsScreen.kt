package com.merkost.suby.presentation.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.merkost.suby.R
import com.merkost.suby.presentation.base.DoubleBackPressHandler
import com.merkost.suby.presentation.base.Icon
import com.merkost.suby.presentation.base.SubyTopAppBar
import com.merkost.suby.presentation.base.components.EmptyStateView
import com.merkost.suby.presentation.base.components.subscription.HorizontalSubscriptionItem
import com.merkost.suby.presentation.viewModel.MainViewModel
import com.merkost.suby.ui.theme.LocalAppState
import com.merkost.suby.ui.theme.SubyTheme
import com.merkost.suby.utils.Constants
import com.merkost.suby.utils.Constants.MAX_FREE_SERVICES
import com.merkost.suby.utils.Destinations
import com.merkost.suby.utils.all
import com.merkost.suby.utils.analytics.ScreenLog
import com.merkost.suby.utils.analytics.Screens
import com.merkost.suby.utils.anim.AnimatedVisibilityCrossfade
import com.merkost.suby.utils.transition.SharedTransitionKeys
import com.merkost.suby.utils.transition.sharedElement
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SubscriptionsScreen(
    onNavigate: (Destinations) -> Unit
) {
    ScreenLog(Screens.Main)
    val appState = LocalAppState.current
    val viewModel = koinViewModel<MainViewModel>()

    val hasSubscriptions = appState.hasSubscriptions
    val subscriptions by viewModel.filteredAndSortedSubscriptions.collectAsState()
    val mainCurrency by viewModel.mainCurrency.collectAsState()
    val totalState by viewModel.total.collectAsState()

    val selectedPeriod by viewModel.period.collectAsState()

    DoubleBackPressHandler(true)

    Scaffold(
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            SubyTopAppBar(
                title = {
                    MainScreenTitle(onLogoClick = { onNavigate(Destinations.PremiumFeatures) })
                },
                actions = {
                    ElevatedButton(
                        modifier = Modifier.padding(end = 8.dp),
                        onClick = {
                            if (appState.hasPremium || subscriptions.size < MAX_FREE_SERVICES) {
                                onNavigate(Destinations.NewSubscription)
                            } else {
                                onNavigate(Destinations.PremiumFeatures)
                            }
                        }
                    ) { Icon(Icons.Default.Add) }
                }
            )
        },
        floatingActionButton = {
            AnimatedVisibilityCrossfade(hasSubscriptions) {
                FloatingActionButton(
                    modifier = Modifier.systemBarsPadding(),
                    onClick = { onNavigate(Destinations.CalendarView) }
                ) {
                    Icon(Icons.Default.CalendarMonth)
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) {
        AnimatedContent(
            modifier = Modifier.padding(it),
            targetState = hasSubscriptions,
            label = ""
        ) { hasSubs ->
            if (hasSubs.not()) {
                EmptySubscriptions { onNavigate(Destinations.NewSubscription) }
            } else {
                LazyColumn(
                    modifier = Modifier,
                    contentPadding = WindowInsets.safeDrawing.exclude(WindowInsets.statusBars)
                        .add(WindowInsets.all(16.dp)).asPaddingValues(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        MainBalance(
                            totalPrice = totalState,
                            mainCurrency = mainCurrency,
                            period = selectedPeriod,
                            onCurrencyClick = { onNavigate(Destinations.CurrencyPick(true)) },
                            onUpdateClick = viewModel::onUpdateRatesClicked,
                            onPeriodClick = viewModel::updateMainPeriod
                        )
                    }

                    item {
                        SortingSection(viewModel)
                    }

                    if (subscriptions.size >= MAX_FREE_SERVICES && !appState.hasPremium) {
                        item { AddMoreServicesItem(onClick = { onNavigate(Destinations.PremiumFeatures) }) }
                    }

                    items(subscriptions, key = { it.id }) { subscription ->
                        HorizontalSubscriptionItem(
                            modifier = Modifier
                                .animateItem(),
                            imageTransitionModifier = Modifier.sharedElement(
                                SharedTransitionKeys.Subscription.serviceLogoFromHome(subscription.id),
                            ),
                            subscription = subscription,
                            selectedPeriod = selectedPeriod,
                            onClick = { onNavigate(Destinations.SubscriptionInfo(subscription.id)) }
                        )
                    }

                    if (subscriptions.isEmpty()) {
                        item {
                            EmptyStateView(
                                modifier = Modifier.fillMaxSize(),
                                message = stringResource(R.string.no_subscriptions_by_filter),
                                onResetFilters = viewModel::onFiltersReset
                            )
                        }
                    }

                    item {
                        TextButton(
                            onClick = { onNavigate(Destinations.About) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = "About",
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                                )
                                Spacer(modifier = Modifier.size(width = 8.dp, height = 0.dp))
                                Text(
                                    "About Suby",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(Constants.LAZY_PADDING))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun SubscriptionsScreenPreview() {
    SubyTheme {
        SubscriptionsScreen {}
    }
}