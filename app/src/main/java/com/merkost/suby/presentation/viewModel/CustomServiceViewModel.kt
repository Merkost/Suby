package com.merkost.suby.presentation.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merkost.suby.model.entity.full.Category
import com.merkost.suby.model.entity.full.toCategory
import com.merkost.suby.model.room.dao.CategoryDao
import com.merkost.suby.model.room.dao.ServiceDao
import com.merkost.suby.model.room.entity.Service
import com.merkost.suby.utils.ImageFileManager
import com.merkost.suby.utils.analytics.Analytics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class CustomServiceViewModel(
    private val serviceDao: ServiceDao,
    private val categoryDao: CategoryDao,
    private val imageFileManager: ImageFileManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CustomServiceUiState?>(null)
    val uiState = _uiState.asStateFlow()

    val categories: StateFlow<List<Category>> = categoryDao.getCategories()
        .map { list -> list.map { it.toCategory() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _customServiceData = MutableStateFlow(CustomServiceData())
    val customServiceData = _customServiceData.asStateFlow()

    fun setServiceName(newName: String) {
        _customServiceData.update { it.copy(name = newName) }
    }

    fun setImageUri(uri: Uri) {
        _customServiceData.update {
            it.copy(imageUri = if (uri == Uri.EMPTY) null else uri)
        }
    }

    fun setCategory(category: Category) {
        _customServiceData.update { it.copy(category = category) }
    }

    private fun validateServiceData(serviceData: CustomServiceData): CustomServiceUiState? {
        return when {
            serviceData.name.isBlank() -> CustomServiceUiState.ServiceNameRequired
            serviceData.category == null -> CustomServiceUiState.CategoryRequired
            else -> null
        }
    }

    fun createCustomService(serviceData: CustomServiceData) {
        val validationState = validateServiceData(serviceData)
        if (validationState != null || serviceData.category == null) {
            _uiState.value = validationState ?: CustomServiceUiState.CategoryRequired
            return
        }
        val category = serviceData.category
        val serviceName = serviceData.name
        val imageUriInput = serviceData.imageUri

        viewModelScope.launch {
            val imageUri = imageUriInput?.let {
                imageFileManager.saveCustomServiceImageToInternalStorage(it, serviceName)
            }

            val customService = Service(
                name = serviceName,
                categoryId = category.id,
                customImageUri = imageUri
            )
            serviceDao.insertService(customService)
            Analytics.logCreatedCustomService(
                serviceName = serviceName,
                categoryName = category.name
            )

            _uiState.value = CustomServiceUiState.Success
            _customServiceData.value = CustomServiceData()
        }
    }

    fun updateCustomService(serviceId: Int, serviceData: CustomServiceData) {
        val validationState = validateServiceData(serviceData)
        if (validationState != null || serviceData.category == null) {
            _uiState.value = validationState ?: CustomServiceUiState.CategoryRequired
            return
        }

        viewModelScope.launch {
            val existingService = serviceDao.getServiceById(serviceId)
            if (existingService != null) {

                val imageUri = serviceData.imageUri?.let {
                    existingService.customImageUri?.let {
                        imageFileManager.deleteCustomServiceImageFromInternalStorage(it)
                    }

                    imageFileManager.saveCustomServiceImageToInternalStorage(it, serviceData.name)
                } ?: existingService.customImageUri

                val updatedService = existingService.copy(
                    name = serviceData.name,
                    categoryId = serviceData.category.id,
                    customImageUri = imageUri
                )

                serviceDao.updateService(updatedService)
                Analytics.logUpdatedCustomService(
                    oldServiceName = existingService.name,
                    serviceName = serviceData.name,
                    categoryName = serviceData.category.name
                )

                _uiState.value = CustomServiceUiState.Success
                _customServiceData.value = CustomServiceData()
            } else {
                _uiState.value = CustomServiceUiState.ServiceNotFound
            }
        }
    }

    fun resetUiState() {
        _uiState.value = null
    }
}

sealed class CustomServiceUiState {
    data object ServiceNameRequired : CustomServiceUiState()
    data object CategoryRequired : CustomServiceUiState()
    data object ServiceNotFound : CustomServiceUiState()
    data object Success : CustomServiceUiState()
}

data class CustomServiceData(
    val name: String = "",
    val imageUri: Uri? = null,
    val category: Category? = null,
)