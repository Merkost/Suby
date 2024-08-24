package com.merkost.suby.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.merkost.suby.R
import com.merkost.suby.SubyShape
import com.merkost.suby.formatDecimal
import com.merkost.suby.model.entity.full.Subscription
import com.merkost.suby.presentation.base.BaseItem
import com.merkost.suby.presentation.base.DeleteConfirmationDialog
import com.merkost.suby.presentation.base.SubyTopAppBar
import com.merkost.suby.presentation.base.components.ServiceLogo
import com.merkost.suby.utils.BaseViewState
import com.merkost.suby.utils.dateString
import com.merkost.suby.viewModel.SubscriptionInfoViewModel
import kotlinx.datetime.toJavaLocalDate

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionInfoScreen(subscriptionId: Int, upPress: () -> Unit) {
    val viewModel: SubscriptionInfoViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = subscriptionId) {
        viewModel.loadSubscription(subscriptionId)
    }

    Scaffold(
        topBar = {
            SubyTopAppBar(
                upPress = upPress,
                title = {},
                actions = {
                    // TODO: Add edit functionality
//                    IconButton(onClick = { /* Handle Edit */ }) {
//                        Icon(Icons.Default.Edit, contentDescription = "Edit")
//                    }
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
                                // TODO: Show snackbar and get deletion result
                                upPress()
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
        verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.Top)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            ServiceLogo(
                modifier = Modifier
                    .padding(16.dp)
                    .height(48.dp)
                    .widthIn(min = 56.dp),
                service = subscription.toService()
            )
        }

        BaseItem {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
            ) {


                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {

                        Text(
                            modifier = Modifier
                                .weight(1f, false)
                                .padding(end = 8.dp),
                            text = subscription.serviceName,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Column(
                            Modifier,
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
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

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(SubyShape),
                    thickness = 1.dp
                )

                // Details Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DetailRow(
                        stringResource(R.string.price),
                        "${subscription.price.formatDecimal()}${subscription.currency.symbol}"
                    )
                    DetailRow(
                        stringResource(R.string.renewal_period),
                        "${subscription.period.duration} ${
                            subscription.period.type.getTitle(
                                subscription.period.duration.toInt()
                            )
                        }"
                    )
                    DetailRow(
                        stringResource(R.string.next_payment_date),
                        subscription.paymentDate.date.toJavaLocalDate().dateString()
                    )
                    if (subscription.description.isNotBlank()) {
                        DetailRow(
                            stringResource(R.string.title_description),
                            subscription.description
                        )
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = { deleteDialog.value = true },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.align(Alignment.BottomCenter)
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

