package com.merkost.suby.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
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
import com.merkost.suby.model.room.entity.Subscription
import com.merkost.suby.presentation.base.Icon
import com.merkost.suby.presentation.base.PlaceholderHighlight
import com.merkost.suby.presentation.base.SubyTopAppBar
import com.merkost.suby.presentation.base.fade
import com.merkost.suby.presentation.base.placeholder3
import com.merkost.suby.presentation.sheets.ServiceSvg
import com.merkost.suby.ui.theme.SubyTheme
import com.merkost.suby.viewModel.MainViewModel
import com.merkost.suby.viewModel.TotalPrice

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SubscriptionsScreen(onAddClicked: () -> Unit, onCurrencyClick: () -> Unit) {

    val viewModel = hiltViewModel<MainViewModel>()
    val subscriptions by viewModel.subscriptions.collectAsState()
    val mainCurrency by viewModel.mainCurrency.collectAsState()
    val totalState by viewModel.total.collectAsState()

    Scaffold(contentWindowInsets = WindowInsets(0.dp), topBar = {
        SubyTopAppBar(title = {
            Text(text = "Your subscriptions")
        }, actions = {
            FilledTonalButton(
                modifier = Modifier.padding(end = 8.dp),
                onClick = onAddClicked
            ) {
                Icon(Icons.Default.Add)
            }
        })
    }) {
        AnimatedContent(
            modifier = Modifier.padding(it),
            targetState = subscriptions, label = ""
        ) { subs ->
            if (subs.isEmpty()) {
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
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item(span = { GridItemSpan(2) }) {
                        MainBalance(
                            totalPrice = totalState,
                            mainCurrency = mainCurrency,
                            period = viewModel.period.collectAsState().value,
                            onCurrencyClick = onCurrencyClick,
                            onUpdateClick = viewModel::onUpdateRatesClicked,
                            onPeriodClick = viewModel::updateMainPeriod
                        )
                    }
                    items(subs) { subscription ->
                        SubscriptionItem(
                            modifier = Modifier.animateItemPlacement(),
                            subscription = subscription,
                            onClick = {
                                viewModel.deleteSubscription(subscription)
                            }
                        )
                    }
                }
            }
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
        Row(
            modifier = Modifier
                .clip(SubyShape)
                .clickable { onPeriodClick() }
                .padding(2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            AnimatedContent(targetState = period, label = "periodAnim") {
                Text(
                    text = it.periodName,
                    modifier = Modifier
                        .clip(SubyShape),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
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
                    text = totalPrice.total.formatDecimal() + mainCurrency.symbol,
                    style = textStyle
                )
            }

            Row(
                modifier = Modifier
                    .clip(SubyShape)
                    .clickable { onCurrencyClick() }
                    .padding(2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val currencyTextStyle =
                    MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                Text(text = mainCurrency.flagEmoji, style = currencyTextStyle)
                Text(
                    text = mainCurrency.code,
                    style = currencyTextStyle
                )
            }
        }

        AnimatedContent(targetState = totalPrice, label = "") { state ->
            if (state.isUpdating) {
                Text(text = "Updating...", style = MaterialTheme.typography.labelSmall)
            } else {
                Text(
                    modifier = Modifier.clickable { onUpdateClick() },
                    text = "Updated on 13.08.2023",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }

}

@Composable
fun SubscriptionItem(
    modifier: Modifier,
    subscription: Subscription,
    onClick: () -> Unit
) {

    BaseItem(
        modifier = modifier,
        onClick = onClick
    ) {

        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            subscription.service.logoLink?.let {
                ServiceSvg(
                    modifier = Modifier.height(50.dp),
                    link = it
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = subscription.service.name, modifier = Modifier.weight(1f, false))
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        ,
                    text = subscription.price.formatDecimal() + subscription.currency.symbol,
                    textAlign = TextAlign.End
                )
            }

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
        SubscriptionsScreen({}) {}
    }
}