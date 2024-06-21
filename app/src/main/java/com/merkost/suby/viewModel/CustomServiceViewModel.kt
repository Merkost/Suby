package com.merkost.suby.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merkost.suby.model.entity.full.Category
import com.merkost.suby.model.entity.full.toCategory
import com.merkost.suby.model.room.AppDatabase
import com.merkost.suby.model.room.dao.CategoryDao
import com.merkost.suby.model.room.entity.CustomServiceDb
import com.merkost.suby.use_case.GetServicesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomServiceViewModel @Inject constructor(
    private val appDatabase: AppDatabase,
    private val categoryDao: CategoryDao,
    private val getServicesUseCase: GetServicesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CustomServiceUiState?>(null)
    val uiState = _uiState.asStateFlow()

    val categories = categoryDao.getCategories().map { it.map { it.toCategory() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _customServiceData = MutableStateFlow(CustomServiceData())
    val customServiceData = _customServiceData.asStateFlow()

    fun setServiceName(newName: String) {
        _customServiceData.update { it.copy(name = newName) }
    }

    private fun saveNewCustomService(serviceName: String, selectedCategoryId: Int) {
        viewModelScope.launch {
            val customService = CustomServiceDb(
                name = serviceName,
                categoryId = selectedCategoryId,
//                emoji = null,
            )
            appDatabase.customServiceDao().addCustomService(customService)
            _uiState.update { CustomServiceUiState.Success }
        }
    }

    fun createCustomService(selectedCategory: Category?) {
        val serviceName = customServiceData.value.name

        when {
            serviceName.isBlank() -> {
                _uiState.update { CustomServiceUiState.ServiceNameRequired }
                return
            }

            selectedCategory == null -> {
                _uiState.update { CustomServiceUiState.CategoryRequired }
                return
            }

            else -> {
                saveNewCustomService(serviceName, selectedCategory.id)
            }
        }

    }
}

sealed class CustomServiceUiState {
    data object ServiceNameRequired : CustomServiceUiState()
    data object CategoryRequired : CustomServiceUiState()
    data object Success : CustomServiceUiState()
}

data class CustomServiceData(
    val name: String = "",
)
