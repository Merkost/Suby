package com.merkost.suby.presentation.screens

import CurrencyToggle
import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.merkost.suby.R
import com.merkost.suby.SubyShape
import com.merkost.suby.domain.ui.LocalCurrencyFormatter
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.model.entity.Status
import com.merkost.suby.model.entity.full.Subscription
import com.merkost.suby.model.entity.full.upcomingPayments
import com.merkost.suby.presentation.base.BaseUiState
import com.merkost.suby.presentation.base.DeleteConfirmationDialog
import com.merkost.suby.presentation.base.Icon
import com.merkost.suby.presentation.base.SubyIconButton
import com.merkost.suby.presentation.base.SubyLargeTopAppBar
import com.merkost.suby.presentation.base.components.DetailsRow
import com.merkost.suby.presentation.base.components.ScreenStateHandler
import com.merkost.suby.presentation.base.components.service.ServiceLogo
import com.merkost.suby.presentation.base.components.subscription.StatusBubble
import com.merkost.suby.presentation.base.components.subscription.TrialBubble
import com.merkost.suby.presentation.viewModel.SubscriptionDetailsState
import com.merkost.suby.presentation.viewModel.SubscriptionDetailsViewModel
import com.merkost.suby.roundToBigDecimal
import com.merkost.suby.showToast
import com.merkost.suby.utils.analytics.ScreenLog
import com.merkost.suby.utils.analytics.Screens
import com.merkost.suby.utils.dateString
import com.merkost.suby.utils.now
import com.merkost.suby.utils.transition.SharedTransitionKeys
import com.merkost.suby.utils.transition.sharedElement
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import org.koin.androidx.compose.koinViewModel
import java.time.temporal.ChronoUnit

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionDetailsScreen(
    upPress: () -> Unit,
    onEditClick: () -> Unit,
    onCalendarClick: () -> Unit
) {
    ScreenLog(Screens.SubscriptionDetails)
    val viewModel: SubscriptionDetailsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val statsState by viewModel.statsState.collectAsState()
    val context = LocalContext.current
    val subscriptionId by remember(uiState) { derivedStateOf { (uiState as? BaseUiState.Success<Subscription>)?.data?.id } }
    val showActions by remember(uiState) { derivedStateOf { uiState is BaseUiState.Success } }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

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
            })
    }

    Scaffold(
        topBar = {
            SubyLargeTopAppBar(
                title = {
                    val subscription = (uiState as? BaseUiState.Success<Subscription>)?.data
                    val titleText = subscription?.serviceName.orEmpty()

                    Text(text = titleText)
                },
                upPress = upPress,
                actions = {
                    if (showActions.not()) return@SubyLargeTopAppBar
                    SubscriptionDetailsActionMenu(
                        onEditClick = onEditClick, onDeleteClick = { deleteDialog.value = true })
                },
                scrollBehavior = scrollBehavior,
            )
        }, containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        ScreenStateHandler(
            screenState = uiState,
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.navigationBars),
        ) { data ->
            SubscriptionInfo(
                subscription = data,
                statsState = statsState,
                onCalendarClick = onCalendarClick,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun SubscriptionDetailsActionMenu(
    onEditClick: () -> Unit, onDeleteClick: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { menuExpanded = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.cd_more_options),
            )
        }
        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            shape = SubyShape,
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier,
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.option_edit),
                    )
                }, onClick = {
                    menuExpanded = false
                    onEditClick()
                }, contentPadding = PaddingValues(horizontal = 16.dp)
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.action_delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }, onClick = {
                    menuExpanded = false
                    onDeleteClick()
                }, contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
internal fun SubscriptionInfo(
    subscription: Subscription,
    statsState: SubscriptionDetailsState,
    onCalendarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = 32.dp), verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        HeroSection(subscription, Modifier)

        SubscriptionDetailsSection(
            modifier = Modifier.padding(horizontal = 16.dp), subscription = subscription
        )

        SubscriptionCategorySection(
            modifier = Modifier.padding(horizontal = 16.dp),
            subscription = subscription,
            categorySubscriptionsCount = statsState.categorySubscriptionsCount,
        )

        SubscriptionStatsSection(
            modifier = Modifier.padding(horizontal = 16.dp),
            subscription = subscription,
            statsState = statsState,
        )

        UpcomingPaymentsListSection(
            currency = subscription.currency,
            upcomingPayments = subscription.upcomingPayments(),
            getPriceForDate = { subscription.price },
            modifier = Modifier.padding(horizontal = 16.dp),
            onCalendarClick = onCalendarClick
        )
    }
}

@Composable
private fun SubscriptionCategorySection(
    modifier: Modifier = Modifier,
    subscription: Subscription,
    categorySubscriptionsCount: Int,
) {
    SectionColumn(modifier = modifier) {
        SectionTitle(text = stringResource(R.string.category_section_title))

        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 2.dp,
                    shape = MaterialTheme.shapes.medium,
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ),
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailsRow(
                    label = stringResource(R.string.category_label),
                    value = subscription.category.beautifulName,
                    icon = Icons.Default.Category
                )

                if (categorySubscriptionsCount > 1) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )

                    DetailsRow(
                        label = stringResource(R.string.subscriptions_in_category),
                        value = categorySubscriptionsCount.toString(),
                        icon = Icons.Default.Info
                    )
                }
            }
        }
    }
}

@Composable
private fun SubscriptionStatsSection(
    modifier: Modifier = Modifier,
    subscription: Subscription,
    statsState: SubscriptionDetailsState,
) {
    var showOriginalCurrency by remember { mutableStateOf(false) }
    val shouldShowCurrencyToggle = subscription.currency != statsState.mainCurrency

    val currencyFormatter = LocalCurrencyFormatter.current

    val budgetPercentage = statsState.budgetPercentage

    SectionColumn(modifier = modifier) {
        SectionTitle(text = stringResource(R.string.subscription_stats_title))

        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 2.dp,
                    shape = MaterialTheme.shapes.medium,
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ),
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PieChart,
                        contentDescription = stringResource(R.string.budget_impact),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.budget_impact),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )

                            Text(
                                text = statsState.mainCurrency.code,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        val progress = budgetPercentage / 100f
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = stringResource(
                                R.string.budget_percent, budgetPercentage
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (statsState.hasBillingStartDate) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timeline,
                            contentDescription = stringResource(R.string.payment_history),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.payment_history),
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                    )
                                }
                                if (shouldShowCurrencyToggle) {
                                    CurrencyToggle(
                                        mainCode = statsState.mainCurrency.code,
                                        originalCode = subscription.currency.code,
                                        showOriginal = showOriginalCurrency,
                                        onToggle = { showOriginalCurrency = it })
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = stringResource(
                                        R.string.total_spent, currencyFormatter.formatCurrencyStyle(
                                            if (showOriginalCurrency) {
                                                val conversionRate =
                                                    if (statsState.mainCurrency != subscription.currency) {
                                                        statsState.monthlyNormalizedPrice / statsState.originalMonthlyPrice
                                                    } else 1.0
                                                (statsState.totalSpentEstimate / conversionRate).roundToBigDecimal()
                                            } else {
                                                statsState.totalSpentEstimate.roundToBigDecimal()
                                            },
                                            if (showOriginalCurrency) subscription.currency.code else statsState.mainCurrency.code
                                        )
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = stringResource(
                                        R.string.per_year, currencyFormatter.formatCurrencyStyle(
                                            if (showOriginalCurrency) {
                                                val conversionRate =
                                                    if (statsState.mainCurrency != subscription.currency) {
                                                        statsState.monthlyNormalizedPrice / statsState.originalMonthlyPrice
                                                    } else 1.0
                                                (statsState.annualCost / conversionRate).roundToBigDecimal()
                                            } else {
                                                statsState.annualCost.roundToBigDecimal()
                                            },
                                            if (showOriginalCurrency) subscription.currency.code else statsState.mainCurrency.code
                                        )
                                    ),
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.primary
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
private fun SubscriptionDetailsSection(modifier: Modifier, subscription: Subscription) {
    SectionColumn(modifier = modifier) {
        SectionTitle(text = stringResource(R.string.subscription_details))

        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 2.dp,
                    shape = MaterialTheme.shapes.medium,
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ),
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DetailsRow(
                    label = stringResource(R.string.renewal_period),
                    value = "${subscription.period.duration} " + subscription.period.type.getTitle(
                        subscription.period.duration.toInt()
                    ),
                    icon = Icons.Default.CurrencyExchange
                )

                if (subscription.status == Status.ACTIVE) {
                    val nextPaymentDateString =
                        subscription.nextPaymentDate.toJavaLocalDate().dateString()
                    DetailsRow(
                        label = stringResource(R.string.next_payment_date),
                        value = nextPaymentDateString,
                        icon = Icons.Default.Event
                    )

                    val daysUntilNext = ChronoUnit.DAYS.between(
                        LocalDate.now().toJavaLocalDate(),
                        subscription.nextPaymentDate.toJavaLocalDate()
                    )
                    if (daysUntilNext > 0) {
                        DetailsRow(
                            label = stringResource(R.string.days_until_next_payment),
                            value = pluralStringResource(
                                R.plurals.days, daysUntilNext.toInt(), daysUntilNext
                            ),
                            icon = Icons.Default.Schedule
                        )
                    }
                }

                if (subscription.description.isNotBlank()) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                    DetailsRow(
                        label = stringResource(R.string.title_description),
                        value = subscription.description,
                        icon = Icons.Default.Info
                    )
                }
            }
        }
    }
}

@Composable
fun PriceWithCurrency(price: Double, currency: Currency) {
    val currencyFormatter = LocalCurrencyFormatter.current
    Column(
        horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = "${currency.code} ${currency.flagEmoji}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Text(
            text = currencyFormatter.formatCurrencyStyle(
                price.roundToBigDecimal(), currency.code
            ),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun UpcomingPaymentsListSection(
    modifier: Modifier = Modifier,
    currency: Currency,
    upcomingPayments: List<LocalDate>,
    getPriceForDate: (LocalDate) -> Double,
    onCalendarClick: () -> Unit
) {
    if (upcomingPayments.isNotEmpty()) {
        val currencyFormatter = LocalCurrencyFormatter.current

        SectionColumn(modifier = modifier) {
            SectionTitle(text = stringResource(R.string.upcoming_payments_title))

            OutlinedCard(
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 2.dp,
                        shape = MaterialTheme.shapes.medium,
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    upcomingPayments.forEachIndexed { i, paymentDate ->
                        val price = getPriceForDate(paymentDate)
                        DetailsRow(
                            label = paymentDate.toJavaLocalDate().dateString(),
                            value = currencyFormatter.formatCurrencyStyle(
                                price.roundToBigDecimal(), currency.code
                            ),
                            icon = Icons.Default.Event
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

            Spacer(modifier = Modifier.height(24.dp))

            SubyIconButton(
                icon = Icons.Default.CalendarMonth,
                text = stringResource(R.string.view_in_calendar),
                contentDescription = stringResource(R.string.cd_view_calendar),
                fullWidth = true,
                onClick = onCalendarClick
            )
        }
    }
}

@Composable
fun SectionTitle(
    text: String, modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(24.dp)
                .background(
                    MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(4.dp)
                )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun SectionColumn(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp), content = content
    )
}

@Composable
fun HeroSection(
    subscription: Subscription,
    modifier: Modifier = Modifier
) {
    val heroBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(0.35f),
            MaterialTheme.colorScheme.background
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                heroBrush, shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
            )
    ) {
        var visible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) { visible = true }

        AnimatedVisibility(
            visible = visible, enter = fadeIn(animationSpec = tween(500)) + scaleIn(
                initialScale = 0.85f, animationSpec = tween(500)
            ), exit = fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (subscription.isTrial) {
                        TrialBubble(
                            textStyle = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            padding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                    StatusBubble(
                        status = subscription.status,
                        textStyle = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        padding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ServiceLogo(
                        modifier = Modifier
                            .height(72.dp)
                            .widthIn(min = 72.dp)
                            .weight(1f, false)
                            .sharedElement(
                                SharedTransitionKeys.Subscription.serviceLogoFromHome(
                                    subscription.id
                                )
                            ),
                        service = subscription.toService()
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    PriceWithCurrency(
                        price = subscription.price, currency = subscription.currency
                    )
                }
            }
        }
    }
}