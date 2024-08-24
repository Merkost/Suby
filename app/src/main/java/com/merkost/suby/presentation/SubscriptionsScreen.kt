package com.merkost.suby.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.merkost.suby.R
import com.merkost.suby.SubyShape
import com.merkost.suby.formatDecimal
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.model.entity.Period
import com.merkost.suby.model.entity.Status
import com.merkost.suby.model.entity.full.Subscription
import com.merkost.suby.presentation.base.DoubleBackPressHandler
import com.merkost.suby.presentation.base.Icon
import com.merkost.suby.presentation.base.PlaceholderHighlight
import com.merkost.suby.presentation.base.SubyTopAppBar
import com.merkost.suby.presentation.base.components.EmptyStateView
import com.merkost.suby.presentation.base.components.ServiceNameImage
import com.merkost.suby.presentation.base.components.ServiceSvg
import com.merkost.suby.presentation.base.fade
import com.merkost.suby.presentation.base.placeholder3
import com.merkost.suby.presentation.base.rotatingOnClick
import com.merkost.suby.round
import com.merkost.suby.ui.theme.SubyTheme
import com.merkost.suby.utils.hasSubscriptions
import com.merkost.suby.utils.toRelativeTimeString
import com.merkost.suby.viewModel.MainViewModel
import com.merkost.suby.viewModel.TotalPrice
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionsScreen(
    onAddClicked: () -> Unit,
    onCurrencyClick: () -> Unit,
    onSubscriptionInfo: (subscriptionId: Int) -> Unit,
) {
    val viewModel = hiltViewModel<MainViewModel>()

    val hasSubscriptions by hasSubscriptions()
    val subscriptions by viewModel.filteredAndSortedSubscriptions.collectAsState()
    val mainCurrency by viewModel.mainCurrency.collectAsState()
    val totalState by viewModel.total.collectAsState()

    val selectedPeriod by viewModel.period.collectAsState()

    DoubleBackPressHandler(true)

    Scaffold(contentWindowInsets = WindowInsets(0.dp), topBar = {
        SubyTopAppBar(title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    modifier = Modifier.size(45.dp).rotatingOnClick(onClick = {}),
                    painter = painterResource(id = R.drawable.suby_logo_white),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                )
                Text(text = stringResource(R.string.title_subscriptions))
            }
        }, actions = {
            OutlinedButton(
                modifier = Modifier.padding(end = 8.dp), onClick = onAddClicked
            ) {
                Icon(Icons.Default.Add)
            }
        })
    }) {
        AnimatedContent(
            modifier = Modifier.padding(it),
            targetState = hasSubscriptions,
            label = ""
        ) { hasSubs ->
            if (hasSubs.not()) {
                EmptySubscriptions(onAddClicked)
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        MainBalance(
                            totalPrice = totalState,
                            mainCurrency = mainCurrency,
                            period = selectedPeriod,
                            onCurrencyClick = onCurrencyClick,
                            onUpdateClick = viewModel::onUpdateRatesClicked,
                            onPeriodClick = viewModel::updateMainPeriod
                        )
                    }

                    item {
                        Sorting(viewModel)
                    }

                    items(subscriptions, key = { it.id }) { subscription ->
                        HorizontalSubscriptionItem(
                            modifier = Modifier
                                .animateItem(fadeInSpec = null, fadeOutSpec = null)
                                .animateContentSize(),
                            subscription = subscription,
                            selectedPeriod = selectedPeriod,
                            onClick = { onSubscriptionInfo(subscription.id) }
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
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Sorting(viewModel: MainViewModel) {
    val selectedFilters by viewModel.selectedFilters.collectAsState()
    val sortState by viewModel.sortState.collectAsState()

    val sheetState = rememberModalBottomSheetState()
    var isBottomSheetOpened by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        TinySortFilterRow(
            selectedFilters = selectedFilters,
            selectedSort = sortState,
            onSortClick = {
                isBottomSheetOpened = true
            },
            onFilterClick = {
                isBottomSheetOpened = true
            },
            onFiltersReset = viewModel::onFiltersReset
        )

        if (isBottomSheetOpened) SortFilterBottomSheet(
            sheetState = sheetState,
            selectedFilters = selectedFilters,
            selectedSortState = sortState,
            onFilterSelected = { filter -> viewModel.toggleFilter(filter) },
            onSortSelected = { sortOption, _ -> viewModel.selectSortOption(sortOption) },
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                    isBottomSheetOpened = false
                }
            }
        )
    }
}


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

@Composable
fun MainBalance(
    totalPrice: TotalPrice,
    period: Period,
    mainCurrency: Currency,
    onCurrencyClick: () -> Unit,
    onUpdateClick: () -> Unit,
    onPeriodClick: () -> Unit,
) {
    Column(modifier = Modifier) {
        Row(modifier = Modifier
            .clip(SubyShape)
            .clickable { onPeriodClick() }
            .padding(2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            AnimatedContent(targetState = period, label = "periodAnim") {
                Text(
                    text = it.periodName,
                    modifier = Modifier.clip(SubyShape),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val textStyle =
                MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold)

            Text(
                modifier = Modifier
                    .weight(1f, false)
                    .placeholder3(
                        totalPrice.isLoading,
                        shape = SubyShape,
                        highlight = PlaceholderHighlight.fade()
                    ),
                text = buildString {
                    append(totalPrice.total ?: stringResource(R.string.unknown))
                    append(totalPrice.currency.symbol)
                },
                style = textStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            CurrencyLabel(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clip(SubyShape)
                    .clickable { onCurrencyClick() },
                currency = mainCurrency,
                textStyle = LocalTextStyle.current,
                flipCurrencyArrow = false
            )
        }

        AnimatedContent(targetState = totalPrice, label = "") { state ->
            if (state.isUpdating) {
                Text(
                    text = stringResource(R.string.updating_state),
                    style = MaterialTheme.typography.labelSmall
                )
            } else if (state.lastUpdated != null) {
                Text(
                    modifier = Modifier
                        .clip(SubyShape)
                        .clickable { onUpdateClick() },
                    text = stringResource(
                        R.string.updated,
                        state.lastUpdated.toRelativeTimeString()
                    ),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
fun HorizontalSubscriptionItem(
    modifier: Modifier = Modifier,
    subscription: Subscription,
    selectedPeriod: Period,
    onClick: () -> Unit
) {
    Box(modifier = modifier) {
        Surface(
            modifier = Modifier
                .animateContentSize()
                .fillMaxWidth(),
            onClick = onClick,
            shape = SubyShape,
            tonalElevation = 2.dp,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                subscription.serviceLogoUrl?.let {
                    ServiceSvg(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        link = it
                    )
                } ?: ServiceNameImage(
                    modifier = Modifier.size(52.dp),
                    name = subscription.serviceName,
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = subscription.serviceName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (subscription.status != Status.ACTIVE) {
                            StatusBubble(
                                modifier = Modifier,
                                status = subscription.status
                            )
                        }
                        Text(
                            text = subscription.getRemainingDurationString(LocalContext.current),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End,
                ) {
                    Text(
                        text = "${subscription.price.formatDecimal()}${subscription.currency.symbol}",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.End,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    AnimatedContent(
                        targetState = subscription.getPriceForPeriod(selectedPeriod),
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        label = "priceForPeriodAnim"
                    ) { priceForPeriod ->
                        if (selectedPeriod.days != subscription.period.toApproximateDays()) {
                            Column {
                                Spacer(modifier = Modifier.size(8.dp))
                                Text(
                                    text = "~${priceForPeriod.round()}${subscription.currency.symbol}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun StatusBubble(
    modifier: Modifier = Modifier,
    status: Status
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(status.color.copy(alpha = 0.1f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(status.color)
        )

        Text(
            text = status.statusName,
            style = MaterialTheme.typography.bodySmall,
            color = status.color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
fun SubscriptionsScreenPreview() {
    SubyTheme {
        SubscriptionsScreen({}, {}, { _ -> })
    }
}