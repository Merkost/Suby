package com.merkost.suby.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.merkost.suby.SubyShape
import com.merkost.suby.asWindowInsets
import com.merkost.suby.model.Currency
import com.merkost.suby.presentation.base.Icon
import com.merkost.suby.presentation.base.SubyTopAppBar
import com.merkost.suby.viewModel.MainViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickCurrencyScreen(
    isMainCurrency: Boolean = false,
    onCurrencySelected: (Currency) -> Unit,
    upPress: () -> Unit
) {
    // TODO: search bar

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val mainViewModel = hiltViewModel<MainViewModel>()
    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            SubyTopAppBar(title = {
                Text(text = "Currencies")
            }, upPress = upPress, scrollBehavior = scrollBehavior)
        }) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(it),
            contentPadding = WindowInsets.navigationBars.add(16.dp.asWindowInsets)
                .asPaddingValues(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(Currency.values().sortedBy { it.fullName }) { currency ->
                CurrencyItem(
                    modifier = Modifier.fillMaxWidth(),
                    currency = currency,
                    onClick = {
                        if (isMainCurrency) {
                            mainViewModel.updateMainCurrency(currency)
                        }
                        onCurrencySelected(currency)
                    }
                )
            }
            item {
                CurrencyAbsentItem()
            }
        }
    }
}

@Composable
fun CurrencyAbsentItem(modifier: Modifier = Modifier) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = modifier
                .padding(8.dp)
                .clip(SubyShape)
                .clickable {
                    // TODO:
                }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Can't find your currency?",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Icon(Icons.Default.Search)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyItem(modifier: Modifier = Modifier, currency: Currency, onClick: () -> Unit) {
    ElevatedCard(modifier = modifier, onClick = onClick) {
        Row(
            modifier = modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = currency.flagEmoji,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(text = currency.fullName + " (${currency.code})")
            }
            Row {
                Text(
                    text = currency.symbol.uppercase(),
                    style = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

