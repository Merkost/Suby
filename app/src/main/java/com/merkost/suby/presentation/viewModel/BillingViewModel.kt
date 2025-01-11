package com.merkost.suby.presentation.viewModel

import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merkost.suby.R
import com.merkost.suby.model.billing.BillingService
import com.merkost.suby.presentation.base.BaseUiState
import com.merkost.suby.repository.datastore.AppSettings
import com.qonversion.android.sdk.dto.offerings.QOffering
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UiEvent {
    data class ShowError(
        val message: String? = null,
        @StringRes val messageRes: Int
    ) : UiEvent()

    data class ShowSuccess(
        val message: String? = null,
        @StringRes val messageRes: Int
    ) : UiEvent()
}

class BillingViewModel(
    private val appSettings: AppSettings,
    private val billingService: BillingService
) : ViewModel() {

    private val _offering = MutableStateFlow<QOffering?>(null)

    private val _uiState = MutableStateFlow<BaseUiState<Unit>>(BaseUiState.Loading)
    val uiState: StateFlow<BaseUiState<Unit>> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = BaseUiState.Loading
            billingService.getMainOffering().fold(
                onSuccess = { offering ->
                    _offering.value = offering
                },
                onFailure = {
                    _uiEvent.emit(
                        UiEvent.ShowError(messageRes = R.string.error_loading_products)
                    )
                }
            )
            _uiState.value = BaseUiState.Success(Unit)
        }
    }

    fun purchase(activity: ComponentActivity) {
        viewModelScope.launch {
            val product = _offering.value?.products?.firstOrNull()
            if (product == null) {
                _uiEvent.emit(
                    UiEvent.ShowError(messageRes = R.string.error_loading_products)
                )
                return@launch
            }
            _uiState.value = BaseUiState.Loading
            billingService.purchase(activity, product, null).fold(
                onSuccess = { entitlement ->
                    appSettings.saveHasPremium(entitlement.isActive)
                },
                onFailure = {
                    _uiEvent.emit(
                        UiEvent.ShowError(messageRes = R.string.error_purchase_failed)
                    )
                }
            )
            _uiState.value = BaseUiState.Success(Unit)
        }
    }

    fun restorePurchase() {
        viewModelScope.launch {
            _uiState.value = BaseUiState.Loading
            billingService.restorePurchase().fold(
                onSuccess = { entitlements ->
                    if (entitlements.none { it.isActive }) {
                        _uiEvent.emit(
                            UiEvent.ShowError(messageRes = R.string.error_no_purchase_to_restore)
                        )
                        return@fold
                    }
                    appSettings.saveHasPremium(entitlements.any { it.isActive })
                    _uiEvent.emit(
                        UiEvent.ShowSuccess(messageRes = R.string.purchase_restored)
                    )
                },
                onFailure = {
                    _uiEvent.emit(
                        UiEvent.ShowError(messageRes = R.string.error_restore_purchase)
                    )
                },
            )
            _uiState.value = BaseUiState.Success(Unit)
        }
    }
}