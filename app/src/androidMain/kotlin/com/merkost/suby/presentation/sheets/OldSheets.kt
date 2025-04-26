package com.merkost.suby.presentation.sheets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.model.entity.NewSubscription
import com.merkost.suby.presentation.base.SaveButton
import com.merkost.suby.presentation.base.SubyTextField
import com.merkost.suby.presentation.base.TitleColumn
import com.merkost.suby.presentation.screens.CurrencyLabel
import com.merkost.suby.presentation.viewModel.NewSubscriptionViewModel
import com.merkost.suby.utils.AndroidConstants.SubyShape

@Composable
fun PriceSheet(
    viewModel: NewSubscriptionViewModel,
    currency: Currency,
    selectedValues: NewSubscription,
    onCurrencyClicked: () -> Unit,
) {
    val textStyle = MaterialTheme.typography.displaySmall.copy(
        fontWeight = FontWeight.Bold
    )
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
            .imePadding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Row(
            modifier = Modifier.weight(6f, false),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SubyTextField(modifier = Modifier
                .weight(1f, false)
                .padding(vertical = 8.dp),
                value = selectedValues.price,
                prefix = {
                    Text(
                        text = currency.symbol,
                        style = textStyle,
                        overflow = TextOverflow.Visible,
                        maxLines = 1
                    )
                },
                suffix = {
                    CurrencyLabel(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .clip(SubyShape)
                            .clickable { onCurrencyClicked() },
                        currency,
                        showArrow = false,
                        flipCurrencyArrow = false
                    )
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
                        style = textStyle.copy(color = textStyle.color.copy(0.2f)),
                        maxLines = 1,
                        overflow = TextOverflow.Visible
                    )
                })

        }
        SaveButton(modifier = Modifier.weight(3f, false), enabled = true) {
            viewModel.saveNewSubscription(currency)
        }
    }
}

@Composable
fun DescriptionSheet(
    description: String,
    onDescriptionChanged: (String) -> Unit,
    onSaveClicked: () -> Unit
) {
    val focusRequester = FocusRequester()

    SideEffect {
        focusRequester.requestFocus()
    }

    TitleColumn(
        title = "Description",
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
            .imePadding(),
    ) {
        Card {
            BasicTextField(value = description,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                onValueChange = onDescriptionChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .heightIn(100.dp),
                decorationBox = {
                    Box(modifier = Modifier.padding(16.dp)) { it() }
                })
        }
        Button(
            onClick = onSaveClicked, modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = "Save")
        }
    }
}