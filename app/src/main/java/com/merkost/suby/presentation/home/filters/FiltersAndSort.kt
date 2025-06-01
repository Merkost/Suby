package com.merkost.suby.presentation.home.filters

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.merkost.suby.R
import com.merkost.suby.SubyShape
import com.merkost.suby.presentation.base.SubyButton
import com.merkost.suby.presentation.model.FilterOption
import com.merkost.suby.presentation.model.SelectedSortState
import com.merkost.suby.presentation.model.SortDirection
import com.merkost.suby.presentation.model.SortOption
import com.merkost.suby.presentation.model.SortState
import com.merkost.suby.ui.theme.subySpacing
import com.merkost.suby.utils.analytics.ScreenLog
import com.merkost.suby.utils.analytics.Screens

private object ChipSizes {
    val TinyChipHeight = 36.dp
    val RegularChipHeight = 40.dp
    val IconSize = 18.dp
}

@Composable
fun TinySortFilterRow(
    selectedFilters: List<FilterOption>,
    selectedSort: SelectedSortState,
    onSortClick: () -> Unit,
    onFilterClick: () -> Unit,
    onFiltersReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedFilterCount by remember(selectedFilters) {
        derivedStateOf { selectedFilters.size }
    }
    val isFilterActive = selectedFilterCount > 0 && !selectedFilters.contains(FilterOption.ALL)
    val sortRotation by animateFloatAsState(
        targetValue = if (selectedSort.direction == SortDirection.ASCENDING) 180f else 0f,
        label = "sort_rotation"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        ChipRow(
            selectedFilters = selectedFilters,
            selectedSort = selectedSort,
            sortRotation = sortRotation,
            onFilterClick = onFilterClick,
            onSortClick = onSortClick,
            modifier = Modifier.weight(1f, fill = false)
        )

        ClearFiltersButton(
            visible = isFilterActive,
            onClick = onFiltersReset
        )
    }
}

@Composable
private fun ChipRow(
    selectedFilters: List<FilterOption>,
    selectedSort: SelectedSortState,
    sortRotation: Float,
    onFilterClick: () -> Unit,
    onSortClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.subySpacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChipButton(
            selectedFilters = selectedFilters,
            onClick = onFilterClick
        )

        SortChipButton(
            selectedSort = selectedSort,
            sortRotation = sortRotation,
            onClick = onSortClick
        )
    }
}

@Composable
private fun FilterChipButton(
    selectedFilters: List<FilterOption>,
    onClick: () -> Unit
) {
    val selectedFilterCount = selectedFilters.size
    val isSelected = selectedFilterCount > 0

    FilterChip(
        shape = SubyShape,
        selected = isSelected,
        onClick = onClick,
        label = {
            AnimatedContent(
                targetState = selectedFilters,
                label = "filter_label"
            ) { filters ->
                FilterLabel(filters = filters)
            }
        },
        colors = getFilterChipColors(isSelected = isSelected),
        modifier = Modifier.height(ChipSizes.TinyChipHeight)
    )
}

@Composable
private fun FilterLabel(filters: List<FilterOption>) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        when {
            filters.contains(FilterOption.ALL) -> {
                Text(text = "All")
            }
            filters.size == 1 -> {
                Text(text = filters.first().displayName)
            }
            else -> {
                Text(text = "Filter")
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "(${filters.size})")
            }
        }
    }
}

@Composable
private fun SortChipButton(
    selectedSort: SelectedSortState,
    sortRotation: Float,
    onClick: () -> Unit
) {
    FilterChip(
        selected = true,
        onClick = onClick,
        leadingIcon = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Sort,
                contentDescription = null,
                modifier = Modifier
                    .rotate(sortRotation)
                    .size(ChipSizes.IconSize)
            )
        },
        label = {
            AnimatedContent(
                targetState = selectedSort.selectedOption,
                label = "sort_label"
            ) { option ->
                Text(
                    text = option.displayName,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
            labelColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier.height(ChipSizes.TinyChipHeight)
    )
}

@Composable
private fun ClearFiltersButton(
    visible: Boolean,
    onClick: () -> Unit
) {
    AnimatedVisibility(visible = visible) {
        IconButton(
            onClick = onClick,
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.size(ChipSizes.TinyChipHeight)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Clear Filters",
                modifier = Modifier.size(ChipSizes.IconSize)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
        BottomSheetContent(
            selectedFilters = selectedFilters,
            selectedSortState = selectedSortState,
            onFilterSelected = onFilterSelected,
            onSortSelected = onSortSelected,
            onDismissRequest = onDismissRequest
        )
    }
}

@Composable
private fun BottomSheetContent(
    selectedFilters: List<FilterOption>,
    selectedSortState: SelectedSortState,
    onFilterSelected: (FilterOption) -> Unit,
    onSortSelected: (SortOption, SortState) -> Unit,
    onDismissRequest: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = MaterialTheme.subySpacing.large)
            .padding(bottom = MaterialTheme.subySpacing.large)
            .navigationBarsPadding()
    ) {
        BottomSheetHeader()
        
        FilterSection(
            selectedFilters = selectedFilters,
            onFilterSelected = onFilterSelected
        )
        
        SectionDivider()
        
        SortSection(
            selectedSortState = selectedSortState,
            onSortSelected = onSortSelected
        )
        
        Spacer(modifier = Modifier.height(MaterialTheme.subySpacing.extraLarge))

        SubyButton(
            text = stringResource(R.string.apply_button_text),
            onClick = onDismissRequest,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun BottomSheetHeader() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = MaterialTheme.subySpacing.small, top = 4.dp)
    ) {
        Text(
            text = stringResource(R.string.filter_and_sort_title),
            style = MaterialTheme.typography.headlineSmall
        )
    }

    HorizontalDivider(
        thickness = DividerDefaults.Thickness,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    )

    Spacer(modifier = Modifier.height(MaterialTheme.subySpacing.medium))
}

@Composable
private fun FilterSection(
    selectedFilters: List<FilterOption>,
    onFilterSelected: (FilterOption) -> Unit
) {
    SectionTitle(text = stringResource(R.string.filter_by))

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.subySpacing.small),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.subySpacing.small)
    ) {
        FilterOption.entries.forEach { option ->
            FilterChipComponent(
                filterOption = option,
                selectedFilters = selectedFilters,
                onFilterSelected = onFilterSelected
            )
        }
    }
}

@Composable
private fun SortSection(
    selectedSortState: SelectedSortState,
    onSortSelected: (SortOption, SortState) -> Unit
) {
    SectionTitle(text = stringResource(R.string.sort_by))

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.subySpacing.small),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.subySpacing.small)
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
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = MaterialTheme.subySpacing.small)
    )
}

@Composable
private fun SectionDivider() {
    Spacer(modifier = Modifier.height(MaterialTheme.subySpacing.large))
    HorizontalDivider(
        thickness = DividerDefaults.Thickness,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    )
    Spacer(modifier = Modifier.height(MaterialTheme.subySpacing.large))
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
    val nextSortState = getNextSortState(sortState)
    val rotation by animateFloatAsState(
        targetValue = if (sortState == SortState.ASCENDING) 180f else 0f,
        label = "sort_chip_rotation"
    )

    AssistChip(
        onClick = { onSortSelected(sortOption, nextSortState) },
        label = {
            Text(
                text = sortOption.displayName,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        },
        leadingIcon = {
            AnimatedContent(
                targetState = isSelected,
                label = "sort_icon"
            ) { selected ->
                if (selected) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Sort,
                        contentDescription = if (sortState == SortState.ASCENDING) "Asc" else "Desc",
                        modifier = Modifier
                            .rotate(rotation)
                            .size(ChipSizes.IconSize),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        colors = getAssistChipColors(isSelected = isSelected),
        modifier = Modifier.height(ChipSizes.RegularChipHeight)
    )
}

@Composable
fun FilterChipComponent(
    filterOption: FilterOption,
    selectedFilters: List<FilterOption>,
    onFilterSelected: (FilterOption) -> Unit
) {
    val isSelected = selectedFilters.contains(filterOption)

    FilterChip(
        selected = isSelected,
        onClick = { onFilterSelected(filterOption) },
        label = {
            Text(
                text = filterOption.displayName,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        },
        colors = getFilterChipComponentColors(isSelected = isSelected),
        modifier = Modifier.height(ChipSizes.RegularChipHeight)
    )
}

@Composable
private fun getFilterChipColors(isSelected: Boolean) = if (isSelected) {
    FilterChipDefaults.filterChipColors(
        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
        labelColor = MaterialTheme.colorScheme.onSurface
    )
} else {
    FilterChipDefaults.filterChipColors().copy(
        containerColor = Color.Transparent,
        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun getFilterChipComponentColors(isSelected: Boolean) = if (isSelected) {
    FilterChipDefaults.filterChipColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        labelColor = MaterialTheme.colorScheme.onPrimaryContainer
    )
} else {
    FilterChipDefaults.filterChipColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun getAssistChipColors(isSelected: Boolean) = if (isSelected) {
    AssistChipDefaults.assistChipColors(
        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
        labelColor = MaterialTheme.colorScheme.primary,
        leadingIconContentColor = MaterialTheme.colorScheme.primary
    )
} else {
    AssistChipDefaults.assistChipColors(
        containerColor = Color.Transparent,
        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        leadingIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

private fun getNextSortState(currentState: SortState): SortState = when (currentState) {
    SortState.NONE -> SortState.ASCENDING
    SortState.ASCENDING -> SortState.DESCENDING
    SortState.DESCENDING -> SortState.ASCENDING
}