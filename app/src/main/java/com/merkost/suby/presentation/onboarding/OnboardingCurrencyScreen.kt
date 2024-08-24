package com.merkost.suby.presentation.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.merkost.suby.R
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.presentation.HorizontalPicker
import com.merkost.suby.presentation.base.LogoImage
import com.merkost.suby.presentation.base.SubyButton
import com.merkost.suby.presentation.rememberPickerState
import com.merkost.suby.presentation.screens.CurrencyLabel
import com.merkost.suby.viewModel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingCurrencyScreen(onCurrencySelected: (Currency) -> Unit) {
    val pickerState = rememberPickerState<Currency>()
    var selectedCurrency by remember { mutableStateOf<Currency?>(null) }

    val viewModel = hiltViewModel<AppViewModel>()

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
                .padding(padding)
                .padding(PaddingValues(32.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.select_currency),
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )

                // TODO: Fix the huge padding issue
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

                Spacer(modifier = Modifier.height(16.dp))

            }

            SubyButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                shape = CircleShape,
                onClick = {
                    selectedCurrency = pickerState.selectedItem
                    selectedCurrency?.let {
                        viewModel.updateFirstTimeOpening()
                        onCurrencySelected(it)
                    }
                },
                text = stringResource(id = R.string.confirm),
                enabled = pickerState.selectedItem != null,
            )
        }
    }
}
