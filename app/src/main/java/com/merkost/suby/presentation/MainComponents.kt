package com.merkost.suby.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.merkost.suby.R
import com.merkost.suby.SubyShape
import com.merkost.suby.utils.dateString
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.model.entity.CustomPeriod
import com.merkost.suby.model.entity.NewSubscription
import com.merkost.suby.model.entity.Period
import com.merkost.suby.presentation.base.BaseItem
import com.merkost.suby.presentation.base.Icon
import com.merkost.suby.presentation.base.SubyTextField
import com.merkost.suby.presentation.base.TitleColumn
import com.merkost.suby.ui.theme.subyColors
import com.merkost.suby.utils.Constants.DEFAULT_CUSTOM_PERIOD

@Composable
fun DescriptionView(
    modifier: Modifier = Modifier,
    description: String,
    onDescriptionChanged: ((String) -> Unit)? = null,
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    TitleColumn(
        title = stringResource(id = R.string.title_description),
        modifier = modifier,
    ) {
        SubyTextField(
            value = description,
            readOnly = onDescriptionChanged == null,
            onValueChange = { newValue ->
                onDescriptionChanged?.let { onDescriptionChanged(newValue) }
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
        )

    }
}


@Composable
fun PriceField(
    modifier: Modifier = Modifier,
    price: String,
    currency: Currency,
    textStyle: TextStyle = MaterialTheme.typography.displaySmall.copy(
        fontWeight = FontWeight.Bold,
    ),
    onPriceInput: ((String) -> Unit)? = null,
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    SubyTextField(
        modifier = modifier
            .focusRequester(focusRequester),
        value = price,
        readOnly = onPriceInput == null,
        prefix = {
            Text(
                text = currency.symbol,
                style = textStyle,
                overflow = TextOverflow.Visible,
                maxLines = 1
            )
        },
        onValueChange = { newValue ->
            onPriceInput?.let { onPriceInput(newValue) }
        },
        textStyle = textStyle,
        singleLine = true,
        shape = SubyShape,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() }
        ),
        placeholder = {
            Text(
                text = "0.00",
                style = textStyle.copy(color = MaterialTheme.subyColors.textPlaceholderColor),
                maxLines = 1,
                overflow = TextOverflow.Visible
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingDate(
    modifier: Modifier = Modifier,
    selectedValues: NewSubscription,
    billingDate: Long?,
    onBillingDateSelected: (Long?) -> Unit,
) {
    val datePickerDate = rememberDatePickerState()
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = {
            TextButton(onClick = {
                onBillingDateSelected(datePickerDate.selectedDateMillis)
                showDatePicker = false
            }) {
                Text("Ok")
            }
        }, dismissButton = {
            TextButton(onClick = { showDatePicker = false }) {
                Text("Cancel")
            }
        }) {
            DatePicker(state = datePickerDate)
        }
    }

    val iconTint by animateColorAsState(
        if (selectedValues.billingDate != null) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.primary
        }
    )

    val color by animateColorAsState(
        targetValue = if (selectedValues.billingDate != null) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
    )

    BaseItem(
        onClick = { showDatePicker = true },
        colors = CardDefaults.cardColors(
            containerColor = color,
        ),
        modifier = modifier
            .fillMaxWidth()
    ) {
        AnimatedContent(
            targetState = billingDate, label = "selectedDateAnim"
        ) { selectedDate ->
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedDate != null) {
                    Text(
                        text = selectedDate.dateString(),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                } else {
                    Text(
                        text = stringResource(R.string.select_payday),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                Icon(
                    imageVector = Icons.Rounded.CalendarToday,
                    tint = iconTint
                )
            }
        }
    }

    AnimatedContent(targetState = selectedValues) { values ->
        // TODO: Improve by extracting the logic to viewModel
        if (values.billingDate != null && values.period != null && values.status != null) {
            values.billingDateInfo?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Period(
    selectedPeriod: Period?,
    onPeriodSelected: (Period) -> Unit,
    onCustomPeriodSelected: (CustomPeriod, duration: Long) -> Unit,
) {

    FlowRow(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Period.entries.forEach { period ->
            PeriodItem(
                period,
                isSelected = selectedPeriod == period,
                onClick = { onPeriodSelected(period) })
        }
    }

    AnimatedContent(targetState = selectedPeriod) { selectedPeriod ->
        if (selectedPeriod == Period.CUSTOM) {
            CustomPeriodInput(
                modifier = Modifier.imePadding(),
                onPeriodSelected = onCustomPeriodSelected
            )
        }
    }
}

@Composable
fun CustomPeriodInput(
    modifier: Modifier = Modifier,
    onPeriodSelected: (CustomPeriod, Long) -> Unit,
) {
    var number by remember { mutableStateOf("") }
    var selectedPeriodType by remember { mutableStateOf(CustomPeriod.DAYS) }

    var selectionOpened by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(text = "Every ")

        SubyTextField(
            modifier = Modifier
                .animateContentSize()
                .width(80.dp),
            value = number,
            onValueChange = {
                number = it
                onPeriodSelected(selectedPeriodType, it.toLongOrNull() ?: DEFAULT_CUSTOM_PERIOD)
            },
            placeholder = {
                Text("$DEFAULT_CUSTOM_PERIOD",)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        )

        Box(
            modifier = Modifier
                .animateContentSize()
                .weight(1f),
            contentAlignment = Alignment.CenterEnd
        ) {
            BaseItem(onClick = { selectionOpened = true }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        selectedPeriodType.getTitle(
                            number.toIntOrNull() ?: DEFAULT_CUSTOM_PERIOD.toInt()
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Icon(Icons.Default.ArrowDropDown)
                }
            }

            DropdownMenu(
                modifier = Modifier,
                expanded = selectionOpened,
                onDismissRequest = { selectionOpened = false }
            ) {
                CustomPeriod.entries.forEach { type ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = type.getTitle(
                                    number.toIntOrNull() ?: DEFAULT_CUSTOM_PERIOD.toInt()
                                )
                            )
                        },
                        onClick = {
                            selectedPeriodType = type
                            selectionOpened = false
                        }
                    )
                }
            }
        }
    }
}