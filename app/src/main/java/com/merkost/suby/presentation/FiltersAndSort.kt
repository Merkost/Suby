package com.merkost.suby.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.merkost.suby.R
import com.merkost.suby.SubyShape
import com.merkost.suby.utils.analytics.ScreenLog
import com.merkost.suby.utils.analytics.Screens

@Composable
fun TinySortFilterRow(
    selectedFilters: List<FilterOption>,
    selectedSort: SelectedSortState,
    onSortClick: () -> Unit,
    onFilterClick: () -> Unit,
    onFiltersReset: () -> Unit,
) {
    val selectedFilterCount by remember(selectedFilters) {
        derivedStateOf { selectedFilters.size }
    }
    val rotation by animateFloatAsState(
        targetValue = if (selectedSort.direction == SortDirection.ASCENDING) 180f else 0f
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .weight(1f, false),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                shape = SubyShape,
                selected = selectedFilterCount > 0,
                onClick = onFilterClick,
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (selectedFilters.contains(FilterOption.ALL)) {
                            Text(text = "All")
                        } else if (selectedFilterCount == 1) {
                            Text(text = selectedFilters.firstOrNull()?.displayName.orEmpty())
                        } else {
                            Text(text = "Filter")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "($selectedFilterCount)")
                        }
                    }
                }
            )

            FilterChip(
                selected = true,
                onClick = onSortClick,
                leadingIcon = {
                    Icon(
                        Icons.AutoMirrored.Filled.Sort, contentDescription = null,
                        modifier = Modifier.rotate(rotation)
                    )
                },
                label = {
                    Text(text = selectedSort.selectedOption.displayName)
                }
            )

        }

        AnimatedVisibility(
            selectedFilters.contains(FilterOption.ALL).not()
        ) {
            IconButton(onClick = onFiltersReset, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Close, "", modifier = Modifier.size(20.dp))
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SortFilterBottomSheet(
    sheetState: SheetState,
    selectedFilters: List<FilterOption>,
    selectedSortState: SelectedSortState,
    onFilterSelected: (FilterOption) -> Unit,
    onSortSelected: (SortOption, SortState) -> Unit,
    onDismissRequest: () -> Unit
) {
    ScreenLog(Screens.FiltersAndSort)
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.filter_by),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                FilterOption.entries.forEach { option ->
                    FilterChip(
                        filterOption = option,
                        selectedFilters = selectedFilters,
                        onFilterSelected = { filter ->
                            if (filter == FilterOption.ALL) {
                                onFilterSelected(FilterOption.ALL)
                            } else {
                                onFilterSelected(filter)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.sort_by),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                SortOption.entries.forEach { option ->
                    SortAssistChip(
                        sortOption = option,
                        currentSortOption = selectedSortState.selectedOption,
                        currentDirection = selectedSortState.direction,
                        onSortSelected = onSortSelected
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onDismissRequest,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Apply")
            }
        }
    }
}

@Composable
fun SortAssistChip(
    sortOption: SortOption,
    currentSortOption: SortOption?,
    currentDirection: SortDirection,
    onSortSelected: (SortOption, SortState) -> Unit
) {
    val isSelected = currentSortOption == sortOption
    val sortState = if (isSelected) currentDirection.sortState else SortState.NONE

    val nextSortState = when (sortState) {
        SortState.NONE -> SortState.ASCENDING
        SortState.ASCENDING -> SortState.DESCENDING
        SortState.DESCENDING -> SortState.ASCENDING
    }

    val rotation by animateFloatAsState(
        targetValue = if (sortState == SortState.ASCENDING) 180f else 0f
    )

    AssistChip(
        onClick = { onSortSelected(sortOption, nextSortState) },
        label = { Text(sortOption.displayName) },
        leadingIcon = {
            AnimatedContent(isSelected) {
                if (it) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Sort,
                        contentDescription = if (sortState == SortState.ASCENDING) "Ascending" else "Descending",
                        modifier = Modifier.rotate(rotation)
                    )
                }
            }
        },
        colors = if (isSelected) {
            AssistChipDefaults.assistChipColors().copy(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                leadingIconContentColor = MaterialTheme.colorScheme.primary
            )
        } else {
            AssistChipDefaults.assistChipColors()
        }
    )
}

@Composable
fun FilterChip(
    filterOption: FilterOption,
    selectedFilters: List<FilterOption>,
    onFilterSelected: (FilterOption) -> Unit
) {
    val isSelected = selectedFilters.contains(filterOption)

    FilterChip(
        selected = isSelected,
        onClick = {
            if (filterOption == FilterOption.ALL) {
                onFilterSelected(FilterOption.ALL)
            } else {
                onFilterSelected(filterOption)
            }
        },
        label = { Text(filterOption.displayName) },
        colors = if (isSelected) {
            FilterChipDefaults.filterChipColors().copy(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                labelColor = MaterialTheme.colorScheme.primary
            )
        } else {
            FilterChipDefaults.filterChipColors()
        }
    )
}

enum class SortState {
    NONE, ASCENDING, DESCENDING
}

enum class SortDirection() {
    ASCENDING, DESCENDING;

    val sortState: SortState
        get() = when (this) {
            ASCENDING -> SortState.ASCENDING
            DESCENDING -> SortState.DESCENDING
        }
}

data class SelectedSortState(
    val selectedOption: SortOption,
    val direction: SortDirection
)


enum class SortOption(val displayName: String) {
    NAME("Name"),
    PRICE("Price"),
    STATUS("Status"),
    PAYMENT_DATE("Payment Date");

    override fun toString() = displayName
}

enum class FilterOption(val displayName: String) {
    ALL("All"),
    ACTIVE("Active"),
    CANCELLED("Cancelled"),
    EXPIRED("Expired"),
    TRIAL("Trial");

    override fun toString() = displayName
}