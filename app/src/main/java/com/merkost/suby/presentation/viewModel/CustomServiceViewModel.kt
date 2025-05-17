package com.merkost.suby.presentation.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merkost.suby.model.entity.full.Category
import com.merkost.suby.model.entity.full.toCategory
import com.merkost.suby.model.room.dao.CategoryDao
import com.merkost.suby.model.room.dao.ServiceDao
import com.merkost.suby.model.room.entity.ServiceDb
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
import timber.log.Timber
import java.io.IOException

class CustomServiceViewModel(
    private val serviceDao: ServiceDao,
    private val categoryDao: CategoryDao,
    private val imageFileManager: ImageFileManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CustomServiceUiState?>(null)
    val uiState = _uiState.asStateFlow()

    val categories: StateFlow<List<Category>> = categoryDao.getCategories()
        .map { list -> list.map { it.toCategory() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _customServiceData = MutableStateFlow(CustomServiceData())
    val customServiceData = _customServiceData.asStateFlow()

    init {
        cleanupOrphanedImages()
    }
    
    fun setServiceName(newName: String) {
        _customServiceData.update { it.copy(name = newName.trim()) }
    }

    fun setImageUri(uri: Uri) {
        _customServiceData.update {
            it.copy(imageUri = if (uri == Uri.EMPTY) null else uri)
        }
    }

    fun setCategory(category: Category) {
        _customServiceData.update { it.copy(category = category) }
    }
    
    fun resetCustomServiceData() {
        _customServiceData.value = CustomServiceData()
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
        
        viewModelScope.launch {
            try {
                val category = serviceData.category
                val serviceName = serviceData.name
                val imageUriInput = serviceData.imageUri

                val imageUri = imageUriInput?.let {
                    imageFileManager.saveCustomServiceImageToInternalStorage(it, serviceName)
                }

                val customService = ServiceDb(
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
                resetCustomServiceData()
            } catch (e: IOException) {
                _uiState.value = CustomServiceUiState.ImageProcessingError
            } catch (e: Exception) {
                _uiState.value = CustomServiceUiState.UnknownError(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun updateCustomService(serviceId: Int, serviceData: CustomServiceData) {
        val validationState = validateServiceData(serviceData)
        if (validationState != null || serviceData.category == null) {
            _uiState.value = validationState ?: CustomServiceUiState.CategoryRequired
            return
        }

        viewModelScope.launch {
            try {
                val existingService = serviceDao.getCustomServiceById(serviceId)
                
                if (existingService == null) {
                    _uiState.value = CustomServiceUiState.ServiceNotFound
                    return@launch
                }
                
                val imageUri = if (serviceData.imageUri != null) {
                    existingService.customImageUri?.let {
                        imageFileManager.deleteCustomServiceImageFromInternalStorage(it)
                    }
                    imageFileManager.saveCustomServiceImageToInternalStorage(serviceData.imageUri, serviceData.name)
                } else {
                    existingService.customImageUri
                }

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
                resetCustomServiceData()
            } catch (e: IOException) {
                _uiState.value = CustomServiceUiState.ImageProcessingError
            } catch (e: Exception) {
                _uiState.value = CustomServiceUiState.UnknownError(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun resetUiState() {
        _uiState.value = null
    }
    
    fun cleanupOrphanedImages() {
        viewModelScope.launch {
            try {
                val allServices = serviceDao.getCustomServices()
                val activeImagePaths = allServices
                    .mapNotNull { it.customImageUri }
                    .filter { it.isNotBlank() }
                
                imageFileManager.cleanupOrphanedImages(activeImagePaths)
            } catch (e: Exception) {
                Timber.tag("CustomService").w(e, "Failed to cleanup orphaned images")
            }
        }
    }
}

sealed class CustomServiceUiState {
    data object ServiceNameRequired : CustomServiceUiState()
    data object CategoryRequired : CustomServiceUiState()
    data object ServiceNotFound : CustomServiceUiState()
    data object ImageProcessingError : CustomServiceUiState()
    data class UnknownError(val message: String) : CustomServiceUiState()
    data object Success : CustomServiceUiState()
}

data class CustomServiceData(
    val name: String = "",
    val imageUri: Uri? = null,
    val category: Category? = null,
)