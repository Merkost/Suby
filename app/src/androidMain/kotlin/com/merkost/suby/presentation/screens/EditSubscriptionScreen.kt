package com.merkost.suby.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.merkost.suby.domain.EditableSubscription
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.presentation.DescriptionView
import com.merkost.suby.presentation.base.SaveButton
import com.merkost.suby.presentation.base.SubyTopAppBar
import com.merkost.suby.presentation.base.TitleColumn
import com.merkost.suby.presentation.base.components.ScreenStateHandler
import com.merkost.suby.presentation.base.components.service.ServiceRowItem
import com.merkost.suby.presentation.states.EditSubscriptionEvent
import com.merkost.suby.presentation.viewModel.EditSubscriptionViewModel
import com.merkost.suby.showToast
import com.merkost.suby.utils.analytics.ScreenLog
import com.merkost.suby.utils.analytics.Screens
import com.merkost.suby.utils.toEpochMillis
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import suby.app.generated.resources.Res
import suby.app.generated.resources.edit_subscription
import suby.app.generated.resources.subscription_saved
import suby.app.generated.resources.title_service

@Composable
fun EditSubscriptionScreen(
    viewModel: EditSubscriptionViewModel = koinViewModel(),
    subscriptionId: Int,
    pickedCurrency: Currency?,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    onCurrencyClicked: () -> Unit
) {
    ScreenLog(Screens.EditSubscription)
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val couldSave by viewModel.couldSave.collectAsState()
    val subscription by viewModel.subscriptionEdit.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect {
            when (it) {
                is EditSubscriptionEvent.SubscriptionSaved -> {
                    context.showToast(getString(Res.string.subscription_saved))
                    onSave()
                }

                null -> {}
            }
        }
    }

    LaunchedEffect(subscriptionId, pickedCurrency) {
        viewModel.loadSubscription(subscriptionId, pickedCurrency)
    }

    ScreenStateHandler(uiState) {
        subscription?.let {
            EditSubscriptionContent(
                viewModel = viewModel,
                subscription = it,
                onCancel = onCancel,
                couldSave = couldSave,
                onCurrencyClicked = onCurrencyClicked
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSubscriptionContent(
    viewModel: EditSubscriptionViewModel,
    subscription: EditableSubscription,
    onCancel: () -> Unit,
    onCurrencyClicked: () -> Unit,
    couldSave: Boolean
) {
    val billingDate by remember(subscription) { derivedStateOf { subscription.billingDate.toEpochMillis() } }

    Scaffold(
        topBar = {
            SubyTopAppBar(
                title = {
                    Text(text = stringResource(Res.string.edit_subscription))
                }, upPress = onCancel
            )
        }, floatingActionButton = {
            SaveButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                enabled = couldSave
            ) {
                viewModel.saveSubscription()
            }

        },
        floatingActionButtonPosition = FabPosition.Center
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(it),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TitleColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                title = stringResource(Res.string.title_service)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ServiceRowItem(
                        modifier = Modifier.fillMaxWidth(),
                        service = subscription.service,
                        showCategory = true,
                        onClick = {}
                    )
                }
            }

            DescriptionView(
                modifier = Modifier.padding(horizontal = 16.dp),
                description = subscription.description,
                onDescriptionChanged = viewModel::onDescriptionChanged
            )

            PriceAndCurrencyRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                price = subscription.price.toString(),
                flipCurrencyArrow = false,
                currency = subscription.currency,
                onPriceInput = viewModel::onPriceChanged,
                onCurrencyClicked = onCurrencyClicked
            )

            BillingDateComponent(
                billingDate = billingDate,
                billingDateInfo = null,
                onBillingDateSelected = viewModel::onBillingDateChanged
            )

            StatusComponent(
                selectedStatus = subscription.status,
                onStatusClicked = viewModel::onStatusChanged
            )

            PeriodComponent(
                selectedPeriod = subscription.period,
                onPeriodSelected = viewModel::onPeriodChanged
            )

            Spacer(modifier = Modifier.size(356.dp))
        }
    }
}
