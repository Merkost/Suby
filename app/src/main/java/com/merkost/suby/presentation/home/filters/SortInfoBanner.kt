package com.merkost.suby.presentation.home.filters

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.merkost.suby.R
import com.merkost.suby.presentation.base.components.banner.BannerType
import com.merkost.suby.presentation.base.components.banner.InfoBanner
import com.merkost.suby.presentation.model.SelectedSortState
import com.merkost.suby.presentation.model.SortState

@Composable
fun SortInfoBanner(
    selectedSortState: SelectedSortState,
    modifier: Modifier = Modifier
) {
    val sortState = selectedSortState.direction.sortState
    val isActiveSort = sortState != SortState.NONE

    val directionText = if (sortState == SortState.ASCENDING)
        stringResource(R.string.sort_state_ascending)
    else
        stringResource(R.string.sort_state_descending)

    val message = stringResource(
        R.string.sorting_state_text,
        selectedSortState.selectedOption.displayName,
        directionText
    )

    InfoBanner(
        visible = isActiveSort,
        modifier = modifier,
        message = message,
        bannerType = BannerType.INFO
    )
}