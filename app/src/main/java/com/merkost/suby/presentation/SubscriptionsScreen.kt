package com.merkost.suby.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.merkost.suby.R
import com.merkost.suby.SubyShape
import com.merkost.suby.formatDecimal
import com.merkost.suby.model.Currency
import com.merkost.suby.model.Period
import com.merkost.suby.model.room.entity.SubscriptionWithDetails
import com.merkost.suby.presentation.base.Icon
import com.merkost.suby.presentation.base.PlaceholderHighlight
import com.merkost.suby.presentation.base.SubyTopAppBar
import com.merkost.suby.presentation.base.fade
import com.merkost.suby.presentation.base.placeholder3
import com.merkost.suby.presentation.sheets.ServiceSvg
import com.merkost.suby.round
import com.merkost.suby.ui.theme.SubyTheme
import com.merkost.suby.utils.Constants
import com.merkost.suby.viewModel.MainViewModel
import com.merkost.suby.viewModel.TotalPrice
import kotlinx.datetime.toJavaLocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionsScreen(
    onAddClicked: () -> Unit,
    onCurrencyClick: () -> Unit,
    onSubscriptionInfo: (subscriptionId: Int) -> Unit,
) {
    val viewModel = hiltViewModel<MainViewModel>()
    val subscriptions by viewModel.subscriptions.collectAsState()
    val mainCurrency by viewModel.mainCurrency.collectAsState()
    val totalState by viewModel.total.collectAsState()

    val selectedPeriod by viewModel.period.collectAsState()

    Scaffold(contentWindowInsets = WindowInsets(0.dp), topBar = {
        SubyTopAppBar(title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    modifier = Modifier.size(45.dp),
                    painter = painterResource(id = R.drawable.suby_logo_white),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                )
                Text(text = stringResource(R.string.title_subscriptions))
            }
        }, actions = {
            FilledTonalButton(
                modifier = Modifier.padding(end = 8.dp), onClick = onAddClicked
            ) {
                Icon(Icons.Default.Add)
            }
        })
    }) {
        AnimatedContent(
            modifier = Modifier.padding(it), targetState = subscriptions, label = ""
        ) { subs ->
            if (subs.isEmpty()) {
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

                    items(subs) { details ->
                        HorizontalSubscriptionItem(
                            modifier = Modifier
                                .animateItem(fadeInSpec = null, fadeOutSpec = null)
                                .animateContentSize(),
                            sub = details,
                            selectedPeriod = selectedPeriod,
                            onClick = { onSubscriptionInfo(details.subscription.id) }
                        )
                    }
                }
                /*LazyVerticalStaggeredGrid(
                    modifier = Modifier,
                    columns = StaggeredGridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalItemSpacing = 8.dp
                ) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        MainBalance(
                            totalPrice = totalState,
                            mainCurrency = mainCurrency,
                            period = selectedPeriod,
                            onCurrencyClick = onCurrencyClick,
                            onUpdateClick = viewModel::onUpdateRatesClicked,
                            onPeriodClick = viewModel::updateMainPeriod
                        )
                    }
                    items(subs) { subscription ->
                        SubscriptionItem(
                            modifier = Modifier
                                .animateItemPlacement()
                                .animateContentSize(),
                            subscription = subscription,
                            selectedPeriod = selectedPeriod,
                            onClick = onSubscriptionInfo
                        )
                    }
                }*/
            }
        }
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

        FilledTonalButton(onClick = onAddClicked) {
            Text(text = "Add subscription")
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
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
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


            Column {
                Text(
                    modifier = Modifier.placeholder3(
                        totalPrice.isLoading,
                        shape = SubyShape,
                        highlight = PlaceholderHighlight.fade()
                    ),
                    text = buildString {
                        append(totalPrice.total ?: stringResource(R.string.unknown))
                        append(totalPrice.currency.symbol)
                    },
                    style = textStyle
                )
            }

            CurrencyLabel(
                modifier = Modifier
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
                    modifier = Modifier.clickable { onUpdateClick() },
                    text = stringResource(
                        R.string.updated_on,
                        state.lastUpdated.date.toJavaLocalDate().format(Constants.dataFormat)
                    ),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
fun SubscriptionItem(
    modifier: Modifier,
    subDetails: SubscriptionWithDetails,
    selectedPeriod: Period,
    onClick: () -> Unit
) {

    val subscription = subDetails.subscription
    val service = subDetails.serviceWithCategory.service
    BaseItem(
        modifier = modifier, onClick = onClick
    ) {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            subDetails.serviceWithCategory.service.logoLink?.let {
                ServiceSvg(
                    modifier = Modifier.height(48.dp), link = it
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = service.name,
                    modifier = Modifier.weight(1f, false),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.size(4.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        modifier = Modifier.padding(horizontal = 2.dp),
                        text = subscription.price.formatDecimal() + subscription.currency.symbol,
                        textAlign = TextAlign.End
                    )
                    AnimatedContent(
                        targetState = selectedPeriod, label = "priceForPeriodAnim"
                    ) { period ->
                        if (selectedPeriod != subscription.period) Text(
                            text = "~" + subscription.getPriceForPeriod(
                                period
                            ) + subscription.currency.symbol
                        )
                    }
                }
            }

            // TODO:  

//                Row(
//                    modifier = Modifier,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = "Renewal ",
//                        textAlign = TextAlign.End
//                    )
//                    Text(
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis,
//                        text = subscription.getRemainingDurationString(context),
//                        textAlign = TextAlign.End,
//                        style = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold)
//                    )
//                }

        }
    }

}

@Composable
fun HorizontalSubscriptionItem(
    modifier: Modifier,
    sub: SubscriptionWithDetails,
    selectedPeriod: Period,
    onClick: () -> Unit
) {
    val subscription = sub.subscription
    BaseItem(
        modifier = modifier.animateContentSize(), onClick = onClick
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                sub.serviceWithCategory.service.logoLink?.let {
                    ServiceSvg(
                        modifier = Modifier
                            .height(48.dp)
                            .widthIn(max = 96.dp), link = it
                    )
                } ?: Text(
                    text = sub.serviceWithCategory.service.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f, false),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.size(4.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        modifier = Modifier.padding(horizontal = 2.dp),
                        text = subscription.price.formatDecimal() + subscription.currency.symbol,
                        textAlign = TextAlign.End
                    )
                    AnimatedContent(
                        targetState = selectedPeriod, label = "priceForPeriodAnim"
                    ) { period ->
                        if (selectedPeriod.days != subscription.periodDays) {
                            Text(
                                text = "~" + period.let {
                                    subscription.getPriceForPeriod(it)
                                        .round() + subscription.currency.symbol
                                }
                            )
                        }
                    }
                }
            }

            // TODO:

//                Row(
//                    modifier = Modifier,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = "Renewal ",
//                        textAlign = TextAlign.End
//                    )
//                    Text(
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis,
//                        text = subscription.getRemainingDurationString(context),
//                        textAlign = TextAlign.End,
//                        style = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold)
//                    )
//                }

        }
    }

}

@Preview
@Composable
fun SubscriptionsScreenPreview() {
    SubyTheme {
        SubscriptionsScreen({}, {}) {}
    }
}