package com.merkost.suby.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merkost.suby.model.entity.full.Service
import com.merkost.suby.repository.room.ServiceRepository
import com.merkost.suby.use_case.GetServicesUseCase
import com.merkost.suby.utils.BaseViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class SelectServiceViewModel(
    private val serviceRepository: ServiceRepository,
    private val getServicesUseCase: GetServicesUseCase
) : ViewModel() {

    private val _servicesState =
        MutableStateFlow<BaseViewState<List<Service>>>(BaseViewState.Loading)
    val servicesState = _servicesState.asStateFlow()

    val customServices = serviceRepository.customServices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    init {
        getServices()
    }

    private fun getServices() {
        viewModelScope.launch {
            getServicesUseCase.invoke().first().fold(
                onSuccess = { result ->
                    _servicesState.update {
                        BaseViewState.Success(result)
                    }
                },
                onFailure = { error ->
                    _servicesState.update { BaseViewState.Error(error = error) }
                }
            )
        }
    }

    fun refreshServices() {
        getServices()
    }

    fun deleteCustomService(customService: Service) {
        viewModelScope.launch {
            serviceRepository.deleteCustomService(customService.id)
        }
    }
}