package com.merkost.suby.presentation.base.components.subscription

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.merkost.suby.SubyShape
import com.merkost.suby.domain.ui.LocalCurrencyFormatter
import com.merkost.suby.model.entity.BasePeriod
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.model.entity.CustomPeriod
import com.merkost.suby.model.entity.Period
import com.merkost.suby.model.entity.Status
import com.merkost.suby.model.entity.full.Category
import com.merkost.suby.model.entity.full.Subscription
import com.merkost.suby.presentation.base.components.service.ServiceLogo
import com.merkost.suby.roundToBigDecimal
import com.merkost.suby.ui.theme.SubyTheme
import com.merkost.suby.utils.now
import kotlinx.datetime.LocalDateTime

@Composable
fun HorizontalSubscriptionItem(
    modifier: Modifier = Modifier,
    subscription: Subscription,
    selectedPeriod: Period? = null,
    shouldShowDurationLeft: Boolean = true,
    onClick: () -> Unit
) {
    val currencyFormat = LocalCurrencyFormatter.current
    val formattedAmount by remember(subscription.price, subscription.currency) {
        derivedStateOf {
            currencyFormat.formatCurrencyStyle(
                subscription.price.roundToBigDecimal(),
                subscription.currency.code
            )
        }
    }

    Surface(
        modifier = modifier
            .animateContentSize()
            .fillMaxWidth(),
        onClick = onClick,
        shape = SubyShape,
        tonalElevation = 2.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .height(IntrinsicSize.Max),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ServiceLogo(
                modifier = Modifier.size(54.dp),
                service = subscription.toService()
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = subscription.serviceName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subscription.status != Status.ACTIVE || shouldShowDurationLeft) {
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (subscription.status != Status.ACTIVE) {
                            StatusBubble(
                                modifier = Modifier,
                                status = subscription.status
                            )
                        }
                        if (shouldShowDurationLeft) {
                            Text(
                                text = subscription.getRemainingDurationString(LocalContext.current),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = formattedAmount,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (selectedPeriod != null) {
                    AnimatedContent(
                        targetState = subscription.getPriceForPeriod(selectedPeriod),
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        label = "priceForPeriodAnim"
                    ) { priceForPeriod ->
                        val formattedAmountForPeriod by remember(
                            subscription.price,
                            selectedPeriod
                        ) {
                            derivedStateOf {
                                currencyFormat.formatCurrencyStyle(
                                    priceForPeriod.roundToBigDecimal(),
                                    subscription.currency.code
                                )
                            }
                        }

                        if (selectedPeriod.approxDays != subscription.period.approxDays) {
                            Column {
                                Spacer(modifier = Modifier.size(8.dp))
                                Text(
                                    text = "~$formattedAmountForPeriod",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewHorizontalSubscriptionItem() {
    SubyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            HorizontalSubscriptionItem(
                subscription = Subscription(
                    id = 1702,
                    serviceId = 8913,
                    serviceName = "Kaisha",
                    serviceLogoUrl = null,
                    serviceCreatedAt = LocalDateTime.now(),
                    serviceLastUpdated = LocalDateTime.now(),
                    isCustomService = false,
                    price = 96.060,
                    currency = Currency.CHF,
                    category = Category(
                        id = 3238,
                        name = "Emmanual",
                        emoji = "Effie",
                        createdAt = LocalDateTime.now(),
                        lastUpdated = LocalDateTime.now()
                    ),
                    period = BasePeriod(type = CustomPeriod.WEEKS, duration = 1L),
                    status = Status.ACTIVE,
                    paymentDate = LocalDateTime.now(),
                    createdDate = LocalDateTime.now(),
                    description = "Alvaro"
                ),
                onClick = {}
            )
        }
    }
}