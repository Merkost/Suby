package com.merkost.suby.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.hilt.navigation.compose.hiltViewModel
import com.merkost.suby.R
import com.merkost.suby.SubyShape
import com.merkost.suby.model.Currency
import com.merkost.suby.model.Period
import com.merkost.suby.model.Status
import com.merkost.suby.presentation.base.BaseItem
import com.merkost.suby.presentation.base.Icon
import com.merkost.suby.presentation.base.SaveButton
import com.merkost.suby.presentation.base.SubyTopAppBar
import com.merkost.suby.presentation.base.TitleColumn
import com.merkost.suby.presentation.base.components.ServiceRowItem
import com.merkost.suby.presentation.sheets.CreateCustomServiceSheet
import com.merkost.suby.presentation.sheets.SelectServiceSheet
import com.merkost.suby.presentation.states.NewSubscriptionUiState
import com.merkost.suby.showToast
import com.merkost.suby.viewModel.NewSubscriptionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSubscriptionScreen(
    pickedCurrency: Currency?,
    onCurrencyClicked: () -> Unit,
    onSuggestService: (inputText: String) -> Unit,
    upPress: () -> Unit
) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<NewSubscriptionViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    val mainCurrency by viewModel.mainCurrency.collectAsState()
    val couldSave by viewModel.couldSave.collectAsState()
    val customServices by viewModel.customServices.collectAsState()
    val servicesState by viewModel.servicesState.collectAsState()
    val selectedValues by viewModel.selectedValues.collectAsState()

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is NewSubscriptionUiState.Success -> upPress()
            is NewSubscriptionUiState.Error -> context.showToast(R.string.error_try_later)
            is NewSubscriptionUiState.Requirement -> {
                context.showToast(state.stringResId)
            }

            else -> {}
        }
    }

    val currency by remember {
        derivedStateOf { pickedCurrency ?: mainCurrency }
    }
    var descriptionEnabled by rememberSaveable { mutableStateOf(false) }
    val selectedPeriod by remember { derivedStateOf { selectedValues.period } }
    val selectedStatus by remember { derivedStateOf { selectedValues.status } }
    val billingDate by remember { derivedStateOf { selectedValues.billingDate } }
    val description by remember { derivedStateOf { selectedValues.description } }

    var selectServiceSheet by remember { mutableStateOf(false) }
    var createCustomServiceSheet by remember { mutableStateOf(false) }
    val selectServiceSheetState = rememberModalBottomSheetState(true)
    val createCustomServiceSheetState = rememberModalBottomSheetState(true)

    if (selectServiceSheet) {
        ModalBottomSheet(
            sheetState = selectServiceSheetState,
            onDismissRequest = { selectServiceSheet = false },
            windowInsets = WindowInsets.statusBars
        ) {
            SelectServiceSheet(
                customServices = customServices,
                servicesState = servicesState,
                onServiceSelected = { service ->
                    viewModel.onServiceSelected(service)
                    selectServiceSheet = false
                },
                onSuggestService = onSuggestService,
                onCustomServiceSelected = { service ->
                    viewModel.onCustomServiceSelected(service)
                    selectServiceSheet = false
                },
                onAddCustomService = {
                    createCustomServiceSheet = true
                },
                onRetryLoadServices = viewModel::onReloadServices
            )
        }
    }

    if (createCustomServiceSheet) {
        ModalBottomSheet(
            sheetState = createCustomServiceSheetState,
            onDismissRequest = { createCustomServiceSheet = false },
            windowInsets = WindowInsets.statusBars
        ) {
            CreateCustomServiceSheet(onCreated = { createCustomServiceSheet = false })
        }
    }

    Scaffold(topBar = {
        SubyTopAppBar(
            title = {
                Text(text = stringResource(R.string.new_subscription))
            }, upPress = upPress
        )
    }, floatingActionButton = {
        SaveButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .imePadding(),
            enabled = couldSave
        ) {
            viewModel.saveNewSubscription(currency)
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
            TitleColumn(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
                title = stringResource(R.string.title_service),
                actions = {
                    AnimatedVisibility(descriptionEnabled.not()) {
                        Text(
                            stringResource(R.string.add_description),
                            modifier = Modifier.clickable {
                                descriptionEnabled = true
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AnimatedContent(
                        targetState = selectedValues.service, label = "serviceAnim"
                    ) { service ->
                        if (service != null) {
                            ServiceRowItem(
                                modifier = Modifier.fillMaxWidth(),
                                service = service,
                                showCategory = true,
                                onClick = {
                                    selectServiceSheet = true
                                }
                            )
                        } else {
                            SelectServiceButton(onClick = { selectServiceSheet = true })
                        }
                    }
                }
            }

            AnimatedVisibility(visible = descriptionEnabled) {
                DescriptionView(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    description = description,
                    onDescriptionChanged = viewModel::onDescriptionChanged
                )
            }

            PriceAndCurrencyRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                price = selectedValues.price,
                flipCurrencyArrow = false,
                currency = currency,
                onPriceInput = viewModel::onPriceInput,
                onCurrencyClicked = onCurrencyClicked
            )

            TitleColumn(modifier = Modifier.padding(horizontal = 16.dp),
                title = stringResource(R.string.title_billing_date),
                infoInformation = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                        append(stringResource(R.string.info_billing_date))
                    }
                }) {
                BillingDate(
                    selectedValues = selectedValues,
                    billingDate = billingDate,
                    onBillingDateSelected = viewModel::onBillingDateSelected
                )
            }

            TitleColumn(modifier = Modifier.padding(horizontal = 16.dp),
                title = stringResource(R.string.title_status),
                infoInformation = buildAnnotatedString {
                    Status.entries.fastForEachIndexed { i, status ->
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(status.statusName)
                        }
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                            append(" - ")
                            append(status.description)
                        }
                        if (i != Status.entries.lastIndex) {
                            append("\n\n")
                        }
                    }
                }) {

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(Status.entries) { status ->
                        StatusItem(modifier = Modifier.width(IntrinsicSize.Max),
                            status,
                            isSelected = selectedStatus == status,
                            onClick = { viewModel.onStatusClicked(status) })
                    }
                }
            }

            TitleColumn(modifier = Modifier.padding(horizontal = 16.dp),
                title = stringResource(R.string.title_period),
                infoInformation = buildAnnotatedString {
                    Period.entries.forEachIndexed { i, period ->
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(period.periodName)
                        }
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                            append(" - ")
                            append(period.description)
                        }

                        if (i != Period.entries.lastIndex) {
                            append("\n\n")
                        }
                    }
                }) {
                Period(
                    selectedPeriod,
                    onPeriodSelected = viewModel::onPeriodSelected,
                    onCustomPeriodSelected = viewModel::onCustomPeriodSelected,
                )
            }

            Spacer(modifier = Modifier.size(356.dp))
        }
    }
}

@Composable
internal fun SelectServiceButton(
    onClick: () -> Unit
) {
    BaseItem(
        onClick = onClick, modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.select_service),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.List,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
internal fun PriceAndCurrencyRow(
    modifier: Modifier = Modifier,
    price: String,
    currency: Currency,
    flipCurrencyArrow: Boolean = false,
    onPriceInput: ((String) -> Unit)?,
    onCurrencyClicked: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        TitleColumn(
            title = stringResource(R.string.title_currency), modifier = Modifier.fillMaxHeight()

        ) {
            CurrencyLabel(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clip(SubyShape)
                    .clickable(onCurrencyClicked != null) { onCurrencyClicked?.let { onCurrencyClicked() } },
                currency,
                textStyle = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                flipCurrencyArrow = flipCurrencyArrow,
                showArrow = onCurrencyClicked != null
            )
        }

        TitleColumn(
            title = stringResource(R.string.title_price), modifier = Modifier.weight(1f, false)
        ) {
            PriceField(
                modifier = Modifier,
                price = price,
                currency = currency,
                onPriceInput = onPriceInput
            )
        }
    }
}

@Composable
fun PeriodItem(period: Period, isSelected: Boolean, onClick: () -> Unit) {

    val color by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
    )

    BaseItem(
        modifier = Modifier,
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = period.periodName)
        }
    }
}

@Composable
fun StatusItem(
    modifier: Modifier = Modifier, status: Status, isSelected: Boolean, onClick: () -> Unit
) {

    val color by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
    )

    BaseItem(
        modifier = modifier, colors = CardDefaults.cardColors(containerColor = color),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(imageVector = status.icon)
            Text(text = status.statusName)
        }

    }
}


@Composable
fun CurrencyLabel(
    modifier: Modifier = Modifier,
    currency: Currency,
    // FIXME:
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    showArrow: Boolean = true,
    flipCurrencyArrow: Boolean = false
) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val currencyCodeTextStyle =
            MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)

        val currencyTextStyle =
            MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)

        if (flipCurrencyArrow && showArrow) Icon(Icons.Default.ArrowDropDown)

        Column(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .padding(bottom = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = currency.flagEmoji, style = currencyTextStyle)
            Text(
                text = currency.code, style = currencyCodeTextStyle
            )
        }

        if (flipCurrencyArrow.not() && showArrow) Icon(Icons.Default.ArrowDropDown)
    }
}