package com.merkost.suby.presentation.select

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.merkost.suby.R
import com.merkost.suby.model.entity.full.Service
import com.merkost.suby.presentation.base.DeleteConfirmationDialog
import com.merkost.suby.presentation.base.components.SheetDialog
import com.merkost.suby.presentation.base.components.service.CustomServiceRowItem
import com.merkost.suby.presentation.home.AddMoreServicesItem
import com.merkost.suby.presentation.sheets.CreateCustomServiceSheet
import com.merkost.suby.presentation.sheets.EditCustomServiceSheet
import com.merkost.suby.ui.theme.LocalAppState
import com.merkost.suby.utils.Constants
import com.merkost.suby.utils.all
import com.merkost.suby.utils.analytics.ScreenLog
import com.merkost.suby.utils.analytics.Screens

@Composable
internal fun CustomServicesList(
    customServices: List<Service>,
    onCustomServiceSelected: (Service) -> Unit,
    onDeleteCustomService: (Service) -> Unit,
    onPremiumClicked: () -> Unit
) {
    ScreenLog(Screens.CustomServices)
    var createCustomServiceSheet by remember { mutableStateOf(false) }

    val appState = LocalAppState.current
    val canAddMoreCustomServices =
        (customServices.size < Constants.MAX_FREE_CUSTOM_SERVICES || appState.hasPremium)

    SheetDialog(
        isShown = createCustomServiceSheet,
        onDismiss = { createCustomServiceSheet = false }
    ) {
        CreateCustomServiceSheet(
            onCreated = {
                createCustomServiceSheet = false
            }
        )
    }

    Column(modifier = Modifier.fillMaxHeight()) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding =
                WindowInsets.navigationBars.add(
                    WindowInsets.all(16.dp)
                ).asPaddingValues()
        ) {
            if (canAddMoreCustomServices.not()) {
                item {
                    AddMoreServicesItem { onPremiumClicked() }
                }
            } else {
                item {
                    Button(
                        onClick = { createCustomServiceSheet = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Text(stringResource(R.string.btn_add_custom_service))
                    }
                }
            }
            items(customServices, key = { "custom_" + it.id }) { service ->
                var deleteDialog by remember { mutableStateOf(false) }
                var editDialog by remember { mutableStateOf(false) }


                if (deleteDialog) {
                    DeleteConfirmationDialog(
                        title = String.format(
                            stringResource(R.string.delete_custom_service_title),
                            service.name
                        ),
                        message = stringResource(R.string.delete_custom_service_description),
                        onDismissRequest = {
                            deleteDialog = false
                        }, onConfirm = {
                            onDeleteCustomService(service)
                            deleteDialog = false
                        }
                    )
                }

                SheetDialog(
                    isShown = editDialog,
                    onDismiss = { editDialog = false }
                ) {
                    EditCustomServiceSheet(
                        service = service,
                        onCreated = {
                            editDialog = false
                        }
                    )
                }

                CustomServiceRowItem(
                    modifier = Modifier.animateItem(),
                    service = service,
                    onCustomServiceSelected = onCustomServiceSelected,
                    onEditService = { editDialog = true },
                    onDeleteService = { deleteDialog = true }
                )
            }
        }
    }
}