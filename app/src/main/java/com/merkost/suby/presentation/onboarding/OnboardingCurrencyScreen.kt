package com.merkost.suby.presentation.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.merkost.suby.R
import com.merkost.suby.model.Currency
import com.merkost.suby.presentation.CurrencyLabel
import com.merkost.suby.presentation.Picker
import com.merkost.suby.presentation.base.LogoImage
import com.merkost.suby.presentation.rememberPickerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingCurrencyScreen(onCurrencySelected: (Currency) -> Unit) {
    val pickerState = rememberPickerState<Currency>()
    var selectedCurrency by remember { mutableStateOf<Currency?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LogoImage(
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.app_name),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                )
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(64.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.select_currency),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Picker(
                    items = Currency.entries,
                    state = pickerState,
                    visibleItemsCount = 5,
                    modifier = Modifier.padding(16.dp),
                    pickerItem = { item, modifier ->
                        CurrencyLabel(
                            modifier = modifier.padding(8.dp),
                            currency = item,
                            showArrow = false
                        )
                    }
                )

                FilledTonalButton(
                    onClick = {
                        selectedCurrency = pickerState.selectedItem
                        selectedCurrency?.let { onCurrencySelected(it) }
                    },
                    enabled = pickerState.selectedItem != null,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.confirm),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}
