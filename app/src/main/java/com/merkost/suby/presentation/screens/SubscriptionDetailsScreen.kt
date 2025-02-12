package com.merkost.suby.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.merkost.suby.R
import com.merkost.suby.SubySmallShape
import com.merkost.suby.domain.ui.LocalCurrencyFormatter
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.model.entity.Status
import com.merkost.suby.model.entity.full.Subscription
import com.merkost.suby.model.entity.full.upcomingPayments
import com.merkost.suby.presentation.base.BaseUiState
import com.merkost.suby.presentation.base.DeleteConfirmationDialog
import com.merkost.suby.presentation.base.Icon
import com.merkost.suby.presentation.base.SubyTopAppBar
import com.merkost.suby.presentation.base.TitleColumn
import com.merkost.suby.presentation.base.components.ScreenStateHandler
import com.merkost.suby.presentation.base.components.service.ServiceLogo
import com.merkost.suby.presentation.home.StatusBubble
import com.merkost.suby.presentation.viewModel.SubscriptionDetailsViewModel
import com.merkost.suby.showToast
import com.merkost.suby.utils.analytics.ScreenLog
import com.merkost.suby.utils.analytics.Screens
import com.merkost.suby.utils.dateString
import com.merkost.suby.utils.now
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import org.koin.androidx.compose.koinViewModel
import java.time.temporal.ChronoUnit

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionDetailsScreen(
    upPress: () -> Unit,
    onEditClick: () -> Unit
) {
    ScreenLog(Screens.SubscriptionDetails)
    val viewModel: SubscriptionDetailsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val subscriptionId by remember(uiState) { derivedStateOf { (uiState as? BaseUiState.Success<Subscription>)?.data?.id } }
    val showActions by remember(uiState) { derivedStateOf { uiState is BaseUiState.Success } }

    LaunchedEffect(Unit) {
        viewModel.loadSubscription()
    }

    val deleteDialog = remember { mutableStateOf(false) }
    if (deleteDialog.value) {
        DeleteConfirmationDialog(
            title = stringResource(R.string.delete_subscription_title),
            message = stringResource(R.string.delete_subscription_description),
            onDismissRequest = { deleteDialog.value = false },
            onConfirm = {
                subscriptionId?.let {
                    viewModel.deleteSubscription(it)
                    context.showToast(R.string.toast_subscription_deleted)
                }
                upPress()
                deleteDialog.value = false
            }
        )
    }

    Scaffold(
        topBar = {
            SubyTopAppBar(
                title = {
                    Text(
                        text = (uiState as? BaseUiState.Success<Subscription>)?.data?.serviceName.orEmpty(),
                    )
                },
                upPress = upPress,
                actions = {
                    if (showActions.not()) return@SubyTopAppBar
                    SubscriptionDetailsActionMenu(
                        onEditClick = onEditClick,
                        onDeleteClick = { deleteDialog.value = true }
                    )
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        ScreenStateHandler(
            screenState = uiState,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding),
        ) { data ->
            SubscriptionInfo(
                modifier = Modifier.fillMaxSize(),
                subscription = data
            )
        }
    }
}

@Composable
fun SubscriptionDetailsActionMenu(onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    val menuExpanded = remember { mutableStateOf(false) }

    IconButton(onClick = { menuExpanded.value = true }) {
        androidx.compose.material3.Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = stringResource(R.string.cd_more_options),
            tint = MaterialTheme.colorScheme.onSurface
        )
    }

    DropdownMenu(
        shape = SubySmallShape,
        expanded = menuExpanded.value,
        onDismissRequest = { menuExpanded.value = false }
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.option_edit)) },
            onClick = {
                menuExpanded.value = false
                onEditClick()
            }
        )

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        DropdownMenuItem(
            text = { Text(stringResource(R.string.action_delete)) },
            onClick = {
                menuExpanded.value = false
                onDeleteClick()
            },
        )
    }
}

@Composable
internal fun SubscriptionInfo(
    subscription: Subscription,
    modifier: Modifier = Modifier
) {
    val heroBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.background
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(heroBrush)
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                StatusBubble(
                    status = subscription.status,
                    textStyle = MaterialTheme.typography.labelLarge,
                    padding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ServiceLogo(
                        modifier = Modifier
                            .heightIn(max = 64.dp)
                            .weight(1f, false),
                        service = subscription.toService(),
                    )
                    Spacer(modifier = Modifier.width(32.dp))
                    Column(
                        modifier = Modifier,
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        PriceWithCurrency(
                            price = subscription.price,
                            currency = subscription.currency,
                        )
                    }
                }
            }
        }

        TitleColumn(
            title = stringResource(R.string.subscription_details),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {

                    DetailRowWithIcon(
                        label = stringResource(R.string.renewal_period),
                        value = "${subscription.period.duration} ${
                            subscription.period.type.getTitle(subscription.period.duration.toInt())
                        }",
                        icon = Icons.Default.CurrencyExchange
                    )

                    if (subscription.status == Status.ACTIVE) {
                        val nextPaymentDateString =
                            subscription.nextPaymentDate.toJavaLocalDate().dateString()
                        DetailRowWithIcon(
                            label = stringResource(R.string.next_payment_date),
                            value = nextPaymentDateString,
                            icon = Icons.Default.Event
                        )

                        val daysUntilNext = ChronoUnit.DAYS.between(
                            LocalDate.now().toJavaLocalDate(),
                            subscription.nextPaymentDate.toJavaLocalDate()
                        )
                        if (daysUntilNext > 0) {
                            DetailRowWithIcon(
                                label = stringResource(R.string.days_until_next_payment),
                                value = pluralStringResource(
                                    R.plurals.days,
                                    daysUntilNext.toInt(),
                                    daysUntilNext
                                ),
                                icon = Icons.Default.Schedule
                            )
                        }
                    }


                    if (subscription.description.isNotBlank()) {
                        HorizontalDivider()

                        DetailRowWithIcon(
                            label = stringResource(R.string.title_description),
                            value = subscription.description,
                            icon = Icons.Default.Info
                        )
                    }
                }
            }
        }

        UpcomingPaymentsList(
            currency = subscription.currency,
            upcomingPayments = subscription.upcomingPayments(),
            getPriceForDate = { subscription.price },
            modifier = Modifier
                .padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun PriceWithCurrency(price: Double, currency: Currency) {
    val currencyFormatter = LocalCurrencyFormatter.current
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = currency.code + " " + currency.flagEmoji,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Text(
            text = currencyFormatter.formatCurrencyStyle(
                price.toBigDecimal(),
                currency.code
            ),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun DetailRowWithIcon(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f, false),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                modifier = Modifier,
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
            )
        }
        Spacer(
            modifier = Modifier.width(8.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun UpcomingPaymentsList(
    currency: Currency,
    upcomingPayments: List<LocalDate>,
    getPriceForDate: (LocalDate) -> Double,
    modifier: Modifier = Modifier
) {
    if (upcomingPayments.isNotEmpty()) {
        val currencyFormatter = LocalCurrencyFormatter.current

        TitleColumn(
            modifier = modifier,
            title = stringResource(R.string.upcoming_payments_title)
        ) {

            ElevatedCard(
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        upcomingPayments.forEachIndexed { i, paymentDate ->
                            val price = getPriceForDate(paymentDate)
                            UpcomingPaymentItem(
                                paymentDate = paymentDate,
                                priceString = currencyFormatter.formatCurrencyStyle(
                                    price.toBigDecimal(),
                                    currency.code
                                )
                            )
                            if (i != upcomingPayments.lastIndex) {
                                HorizontalDivider(
                                    thickness = 1.dp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UpcomingPaymentItem(
    paymentDate: LocalDate,
    priceString: String,
    modifier: Modifier = Modifier
) {
    val dateString = paymentDate.toJavaLocalDate().dateString()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Event,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = dateString,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Text(
            text = priceString,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

