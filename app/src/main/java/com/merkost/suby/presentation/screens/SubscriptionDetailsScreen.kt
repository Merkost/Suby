package com.merkost.suby.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.merkost.suby.R
import com.merkost.suby.SubyShape
import com.merkost.suby.formatDecimal
import com.merkost.suby.model.entity.Status
import com.merkost.suby.model.entity.full.Subscription
import com.merkost.suby.presentation.base.DeleteConfirmationDialog
import com.merkost.suby.presentation.base.Icon
import com.merkost.suby.presentation.base.SubyTopAppBar
import com.merkost.suby.presentation.base.UiState
import com.merkost.suby.presentation.base.components.ScreenStateHandler
import com.merkost.suby.presentation.base.components.service.ServiceLogo
import com.merkost.suby.presentation.viewModel.SubscriptionDetailsViewModel
import com.merkost.suby.showToast
import com.merkost.suby.utils.dateString
import kotlinx.datetime.toJavaLocalDate

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionDetailsScreen(upPress: () -> Unit, onEditClick: () -> Unit) {
    val viewModel: SubscriptionDetailsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadSubscription()
    }

    Scaffold(
        topBar = {
            SubyTopAppBar(
                upPress = upPress,
                actions = {
                    if (uiState is UiState.Success) {
                        IconButton(onClick = onEditClick) {
                            Icon(Icons.Default.EditNote)
                        }
                    }
                }
            )
        },
    ) { innerPadding ->
        ScreenStateHandler(
            screenState = uiState,
            modifier = Modifier.padding(innerPadding)
        ) { data ->
            SubscriptionInfo(
                subscription = data,
                onDelete = {
                    viewModel.deleteSubscription(data)
                    context.showToast(R.string.toast_subscription_deleted)
                    upPress()
                }
            )
        }
    }
}

@Composable
internal fun SubscriptionInfo(
    modifier: Modifier = Modifier,
    subscription: Subscription,
    onDelete: () -> Unit
) {
    val deleteDialog = remember { mutableStateOf(false) }

    if (deleteDialog.value) {
        DeleteConfirmationDialog(
            title = stringResource(R.string.delete_subscription_title),
            message = stringResource(R.string.delete_subscription_description),
            onDismissRequest = { deleteDialog.value = false },
            onConfirm = {
                onDelete()
                deleteDialog.value = false
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            ServiceLogo(
                modifier = Modifier
                    .size(84.dp)
                    .clip(SubyShape)
                    .padding(bottom = 8.dp),
                service = subscription.toService()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = subscription.serviceName,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                StatusBubble(
                    status = subscription.status,
                    modifier = Modifier,
                    textStyle = MaterialTheme.typography.titleSmall,
                    padding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DetailRowWithIcon(
                    label = stringResource(R.string.title_price),
                    value = "${subscription.currency.symbol}${subscription.price.formatDecimal()}",
                    icon = Icons.Default.AttachMoney
                )
                DetailRowWithIcon(
                    label = stringResource(R.string.renewal_period),
                    value = "${subscription.period.duration} ${
                        subscription.period.type.getTitle(subscription.period.duration.toInt())
                    }",
                    icon = Icons.Default.CurrencyExchange
                )
                if (subscription.status == Status.ACTIVE) {
                    DetailRowWithIcon(
                        label = stringResource(R.string.next_payment_date),
                        value = subscription.nextPaymentDate.toJavaLocalDate().dateString(),
                        icon = Icons.Default.Event
                    )
                }
                if (subscription.description.isNotBlank()) {
                    DetailRowWithIcon(
                        label = stringResource(R.string.title_description),
                        value = subscription.description,
                        icon = Icons.Default.Info
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { deleteDialog.value = true },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = ButtonDefaults.ContentPadding
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(text = stringResource(R.string.delete_btn))
        }
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

