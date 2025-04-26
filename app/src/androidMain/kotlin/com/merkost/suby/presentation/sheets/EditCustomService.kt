package com.merkost.suby.presentation.sheets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.net.toUri
import com.merkost.suby.model.entity.full.Service
import com.merkost.suby.presentation.viewModel.CustomServiceData
import com.merkost.suby.presentation.viewModel.CustomServiceViewModel
import com.merkost.suby.utils.analytics.ScreenLog
import com.merkost.suby.utils.analytics.Screens
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import suby.app.generated.resources.Res
import suby.app.generated.resources.btn_save
import suby.app.generated.resources.edit_custom_service

@Composable
fun EditCustomServiceSheet(
    service: Service,
    onCreated: () -> Unit
) {
    ScreenLog(Screens.EditCustomService)
    val viewModel: CustomServiceViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val categories by viewModel.categories.collectAsState()

    CustomServiceUiStateHandler(uiState) {
        onCreated()
        viewModel.resetUiState()
    }

    CustomServiceForm(
        initialServiceData = CustomServiceData(
            name = service.name,
            imageUri = service.logoUrl?.toUri(),
            category = service.category
        ),
        categories = categories,
        onSave = { newServiceData ->
            viewModel.updateCustomService(service.id, newServiceData)
        },
        title = stringResource(Res.string.edit_custom_service),
        saveButtonText = stringResource(Res.string.btn_save)
    )
}