package com.merkost.suby.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.merkost.suby.presentation.home.filters.SortFilterBottomSheet
import com.merkost.suby.presentation.home.filters.TinySortFilterRow
import com.merkost.suby.presentation.viewModel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortingSection(viewModel: MainViewModel) {
    val selectedFilters by viewModel.selectedFilters.collectAsState()
    val sortState by viewModel.sortState.collectAsState()

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var isBottomSheetOpened by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        TinySortFilterRow(
            selectedFilters = selectedFilters,
            selectedSort = sortState,
            onSortClick = {
                isBottomSheetOpened = true
            },
            onFilterClick = {
                isBottomSheetOpened = true
            },
            onFiltersReset = viewModel::onFiltersReset
        )

        if (isBottomSheetOpened) SortFilterBottomSheet(
            sheetState = sheetState,
            selectedFilters = selectedFilters,
            selectedSortState = sortState,
            onFilterSelected = { filter -> viewModel.toggleFilter(filter) },
            onSortSelected = { sortOption, _ -> viewModel.selectSortOption(sortOption) },
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                    isBottomSheetOpened = false
                }
            }
        )
    }
}
