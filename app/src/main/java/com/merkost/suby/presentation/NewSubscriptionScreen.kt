package com.merkost.suby.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.merkost.suby.SubyShape
import com.merkost.suby.formatDateLongToDate
import com.merkost.suby.model.Category
import com.merkost.suby.model.Currency
import com.merkost.suby.model.Period
import com.merkost.suby.model.Service
import com.merkost.suby.presentation.base.Icon
import com.merkost.suby.presentation.base.SubyTextField
import com.merkost.suby.presentation.base.SubyTopAppBar
import com.merkost.suby.presentation.sheets.SelectServiceSheet
import com.merkost.suby.presentation.sheets.ServiceItem
import com.merkost.suby.showToast
import com.merkost.suby.viewModel.NewSubscriptionViewModel
import com.merkost.suby.viewModel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSubscriptionScreen(
    pickedCurrency: Currency?,
    onCurrencyClicked: () -> Unit,
    upPress: () -> Unit
) {
    val context = LocalContext.current

    val viewModel = hiltViewModel<NewSubscriptionViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    val mainCurrency by viewModel.mainCurrency.collectAsState()
    val selectedValues by viewModel.selectedValues.collectAsState()

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is UiState.Success -> upPress()
            is UiState.Error -> context.showToast("Error")
            else -> {}
        }
    }

    val currency by remember {
        derivedStateOf { pickedCurrency ?: mainCurrency }
    }
    val selectedCategory by remember { derivedStateOf { selectedValues.category } }
    val selectedPeriod by remember { derivedStateOf { selectedValues.period } }

    var selectServiceSheet by remember {
        mutableStateOf(false)
    }

    var customCategoryName by rememberSaveable {
        mutableStateOf("")
    }
    var customServiceName by rememberSaveable {
        mutableStateOf("")
    }

    if (selectServiceSheet) {
        ModalBottomSheet(
            onDismissRequest = { selectServiceSheet = false },
            windowInsets = WindowInsets(0.dp)
        ) {
            SelectServiceSheet(onServiceSelected = { category, service ->
                viewModel.onServiceSelected(category, service)
                selectServiceSheet = false
            })
        }
    }

    val datePickerDate = rememberDatePickerState()

    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Ok")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerDate, dateValidator = {
                viewModel.onBillingDateSelected(it)
                true
            })
        }
    }


    Scaffold(contentWindowInsets = WindowInsets.navigationBars, topBar = {
        SubyTopAppBar(title = {
            Text(text = "New subscription")
        }, upPress = upPress)
    },
        floatingActionButton = {
            FloatingActionButton(modifier = Modifier.imePadding(),
                onClick = {
                    viewModel.saveNewSubscription(
                        currency,
                    )
                }) {
                Icon(imageVector = Icons.Default.Done)
            }
        }) {

        Column(
            modifier = Modifier.padding(it),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val textStyle = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Bold
            )


            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                SubyTextField(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .weight(1f, false),
                    value = selectedValues.price,
                    prefix = {
                        Text(text = currency.symbol, style = textStyle)
                    },
                    onValueChange = viewModel::onPriceInput,
                    textStyle = textStyle,
                    singleLine = true,
                    colors = TextFieldDefaults.colors(),
                    shape = SubyShape,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    placeholder = {
                        Text(
                            text = "0.00",
                            style = textStyle.copy(color = textStyle.color.copy(0.2f))
                        )
                    })
                CurrencyLabel(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .clip(SubyShape)
                        .clickable { onCurrencyClicked() },
                    currency,
                    textStyle,
                )
            }

            AnimatedContent(
                modifier = Modifier,
                targetState = selectedValues.service,
                label = "",
            ) { service ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    service?.let {
                        Row(
                            modifier = Modifier,
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            InputChip(
                                modifier = Modifier,
                                selected = true,
                                onClick = { selectServiceSheet = true },
                                label = {
                                    Text(
                                        text = selectedCategory?.categoryName
                                            ?: service.category.categoryName
                                    )
                                },
                                leadingIcon = {
                                    Text(text = selectedCategory?.emoji ?: service.category.emoji)
                                })

                            if (service.category == Category.CUSTOM) {
                                InputChip(
                                    modifier = Modifier,
                                    selected = true,
                                    onClick = { selectServiceSheet = true },
                                    label = {
                                        Text(
                                            text = selectedCategory?.categoryName
                                                ?: service.category.categoryName
                                        )
                                    })
                            }
                        }
                        ServiceItem(service) { selectServiceSheet = true }
                        if (selectedCategory == Category.CUSTOM) {
                            SubyTextField(
                                modifier = Modifier.fillMaxWidth(),
                                label = {
                                    Text(text = "Category name")
                                },
                                value = customCategoryName,
                                onValueChange = { customCategoryName = it }
                            )
                        }

                        if (service == Service.CUSTOM) {
                            SubyTextField(
                                modifier = Modifier.fillMaxWidth(),
                                label = {
                                    Text(text = "Service name")
                                },
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Sentences
                                ),
                                value = customServiceName,
                                onValueChange = { customServiceName = it }
                            )
                        }
                    } ?: run {
                        SelectServiceItem(onClick = { selectServiceSheet = true })
                    }
                }
            }

            AnimatedContent(targetState = selectedPeriod, label = "") { period ->
                period?.let {
                    Row(
                        modifier = Modifier.padding(PaddingValues(horizontal = 16.dp)),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        InputChip(
                            selected = true,
                            onClick = viewModel::onResetPeriod,
                            label = {
                                Text(text = it.periodName)
                            }
                        )

                        if (period == Period.CUSTOM) {
                            AssistChip(
                                onClick = { /* TODO: period picker for custom */ },
                                label = { Text(text = "Billing Cycle") },
                                leadingIcon = { Icon(imageVector = Icons.Default.EventRepeat) }
                            )
                        }

                        datePickerDate.selectedDateMillis?.let { selectedDate ->
                            InputChip(
                                selected = true,
                                onClick = { showDatePicker = true },
                                label = {
                                    Text(text = selectedDate.formatDateLongToDate())
                                }
                            )
                        } ?: run {
                            AssistChip(
                                onClick = { showDatePicker = true },
                                label = { Text(text = "Billing Date") },
                                leadingIcon = { Icon(imageVector = Icons.Default.CalendarMonth) }
                            )
                        }

                    }
                } ?: run {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(Period.values()) { period ->
                            InputChip(
                                selected = period == selectedPeriod,
                                onClick = { viewModel.onPeriodSelected(period) },
                                label = {
                                    Text(text = period.periodName)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CurrencyLabel(
    modifier: Modifier = Modifier,
    currency: Currency,
    textStyle: TextStyle,
) {
    Row(
        modifier = modifier.padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = currency.code, style = textStyle)
        Text(
            text = currency.flagEmoji,
            style = MaterialTheme.typography.headlineLarge
        )
    }
}

@Composable
fun SelectServiceItem(onClick: () -> Unit) {
    BaseItem(onClick = onClick) {
        Column(
            Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.Assignment)
            Text(text = "Select Service")
        }
    }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BaseItem(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    onClick?.let {
        Card(
            modifier = modifier,
        ) {
            Box(
                modifier = Modifier
                    .combinedClickable(
                        onClick = onClick,
                        onLongClick = onLongClick,
                        role = Role.Tab,
                    )
                    .padding(16.dp)
            ) {
                content()
            }
        }
    } ?: run {
        Card {
            Box(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}



