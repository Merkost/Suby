package com.merkost.suby.presentation.sheets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.merkost.suby.R
import com.merkost.suby.model.entity.full.Service
import com.merkost.suby.presentation.viewModel.CustomServiceData
import com.merkost.suby.presentation.viewModel.CustomServiceViewModel

@Composable
fun EditCustomServiceSheet(
    service: Service,
    onCreated: () -> Unit
) {
    val viewModel: CustomServiceViewModel = hiltViewModel()
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
        title = stringResource(R.string.edit_custom_service),
        saveButtonText = stringResource(R.string.btn_save)
    )
}