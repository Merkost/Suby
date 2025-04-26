package com.merkost.suby.presentation.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.model.entity.Period
import com.merkost.suby.presentation.base.PlaceholderHighlight
import com.merkost.suby.presentation.base.fade
import com.merkost.suby.presentation.base.noRippleClickable
import com.merkost.suby.presentation.base.placeholder3
import com.merkost.suby.presentation.screens.CurrencyLabel
import com.merkost.suby.presentation.viewModel.TotalPrice
import com.merkost.suby.utils.AndroidConstants.SubyShape
import com.merkost.suby.utils.toRelativeTimeString
import org.jetbrains.compose.resources.stringResource
import suby.app.generated.resources.Res
import suby.app.generated.resources.unknown
import suby.app.generated.resources.updated
import suby.app.generated.resources.updating_state

@Composable
fun MainBalance(
    modifier: Modifier = Modifier,
    totalPrice: TotalPrice,
    period: Period,
    mainCurrency: Currency,
    onCurrencyClick: () -> Unit,
    onUpdateClick: () -> Unit,
    onPeriodClick: () -> Unit,
) {
    Column(modifier = modifier) {
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

            Text(
                modifier = Modifier
                    .weight(1f, false)
                    .placeholder3(
                        totalPrice.isLoading,
                        shape = SubyShape,
                        highlight = PlaceholderHighlight.fade()
                    ),
                text = totalPrice.total ?: stringResource(Res.string.unknown),
                style = textStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            CurrencyLabel(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clip(SubyShape)
                    .clickable { onCurrencyClick() },
                currency = mainCurrency,
                flipCurrencyArrow = false
            )
        }

        AnimatedContent(targetState = totalPrice, label = "") { state ->
            if (state.isUpdating) {
                Text(
                    text = stringResource(Res.string.updating_state),
                    style = MaterialTheme.typography.labelSmall
                )
            } else if (state.lastUpdated != null) {
                Text(
                    modifier = Modifier
                        .clip(SubyShape)
                        .noRippleClickable { onUpdateClick() },
                    text = stringResource(
                        Res.string.updated,
                        state.lastUpdated.toRelativeTimeString()
                    ),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}