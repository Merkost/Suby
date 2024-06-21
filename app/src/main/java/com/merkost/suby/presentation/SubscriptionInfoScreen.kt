package com.merkost.suby.presentation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.merkost.suby.R
import com.merkost.suby.formatDecimal
import com.merkost.suby.model.entity.full.Subscription
import com.merkost.suby.presentation.base.DeleteConfirmationDialog
import com.merkost.suby.presentation.base.SubyTopAppBar
import com.merkost.suby.presentation.base.components.ServiceItem
import com.merkost.suby.utils.BaseViewState
import com.merkost.suby.viewModel.SubscriptionInfoViewModel
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionInfoScreen(subscriptionId: Int, upPress: () -> Unit) {
    val viewModel: SubscriptionInfoViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = subscriptionId) {
        viewModel.loadSubscription(subscriptionId)
    }

    val name by remember(uiState) {
        mutableStateOf((uiState as? BaseViewState.Success)?.data?.serviceName)
    }

    Scaffold(
        topBar = {
            SubyTopAppBar(
                title = { Text(name.orEmpty()) },
                upPress = upPress,
                actions = {
                    IconButton(onClick = { /* Handle Edit */ }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(targetState = uiState) { state ->
                when (state) {
                    is BaseViewState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    is BaseViewState.Error -> {
                        // TODO: Handle Error
                    }

                    is BaseViewState.Success -> {
                        SubscriptionInfo(
                            subscription = state.data,
                            onDelete = {
                                viewModel.deleteSubscription(state.data)
                            }
                        )
                    }
                }
            }
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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ServiceItem(
                service = subscription.toService(),
                modifier = Modifier
            )

            Column {
                Text(
                    text = subscription.serviceName,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = subscription.status.icon,
                        contentDescription = subscription.status.statusName,
                        tint = subscription.status.color
                    )
                    Text(
                        text = subscription.status.statusName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = subscription.status.color
                    )
                }
            }
        }

        // Details Section
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DetailRow(
                "Price",
                "${subscription.price.formatDecimal()}${subscription.currency.symbol}"
            )
            DetailRow("Next Payment Date", subscription.paymentDate.toFormattedDate())
            DetailRow("Renewal Period", "${subscription.period.days} days")
            if (subscription.description.isNotBlank()) {
                DetailRow("Description", subscription.description)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Actions Section
        Button(
            onClick = { deleteDialog.value = true },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Delete, contentDescription = null)
            Text(text = stringResource(R.string.delete_btn))
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

fun Long.toFormattedDate(): String {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    return Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).format(formatter)
}

fun LocalDateTime.toFormattedDate(): String {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    return this.toJavaLocalDateTime().format(formatter)
}

