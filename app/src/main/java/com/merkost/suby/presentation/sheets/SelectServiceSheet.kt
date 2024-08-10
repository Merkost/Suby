package com.merkost.suby.presentation.sheets

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.merkost.suby.R
import com.merkost.suby.SubyShape
import com.merkost.suby.model.entity.full.Service
import com.merkost.suby.presentation.AbsentItem
import com.merkost.suby.presentation.base.Icon
import com.merkost.suby.presentation.base.components.ServiceRowItem
import com.merkost.suby.utils.BaseViewState
import kotlinx.coroutines.launch

@Composable
fun SelectServiceSheet(
    customServices: List<Service>,
    servicesState: BaseViewState<List<Service>>,
    onServiceSelected: (Service) -> Unit,
    onSuggestService: (name: String) -> Unit,
    onCustomServiceSelected: (Service) -> Unit,
    onAddCustomService: () -> Unit,
    onRetryLoadServices: () -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = 0, initialPageOffsetFraction = 0f) { 2 }
    val coroutineScope = rememberCoroutineScope()

    AnimatedContent(targetState = servicesState, label = "") { state ->
        when (state) {
            BaseViewState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is BaseViewState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Error Occurred")
                        Button(onClick = onRetryLoadServices) {
                            Text(text = "Retry")
                        }
                    }
                }
            }

            is BaseViewState.Success -> {
                Column {

                    TabRow(selectedTabIndex = pagerState.currentPage) {
                        Tab(selected = pagerState.currentPage == 0, onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(0)
                            }
                        }, modifier = Modifier.clip(SubyShape)) {
                            Text(
                                text = stringResource(id = R.string.list),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        Tab(selected = pagerState.currentPage == 1, onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        }, modifier = Modifier.clip(SubyShape)) {
                            Text(
                                text = stringResource(id = R.string.custom_services),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    HorizontalPager(
                        modifier = Modifier.fillMaxHeight(), state = pagerState
                    ) { page ->
                        if (page == 1) {
                            CustomServicesList(
                                customServices = customServices,
                                onCustomServiceSelected = onCustomServiceSelected,
                                onAddCustomService = onAddCustomService
                            )
                        } else {
                            ServicesList(
                                services = state.data,
                                onServiceSelected = onServiceSelected,
                                onSuggestService = onSuggestService
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun CustomServicesList(
    customServices: List<Service>,
    onCustomServiceSelected: (Service) -> Unit,
    onAddCustomService: () -> Unit
) {
    Column(modifier = Modifier.fillMaxHeight()) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(16.dp)
        ) {
            items(customServices) { service ->
                ServiceRowItem(
                    modifier = Modifier.animateItem(),
                    showCreatedAt = true,
                    showCategory = true,
                    service = service,
                    onClick = { onCustomServiceSelected(service) })
            }
            item {
                Button(
                    onClick = onAddCustomService,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Text(stringResource(R.string.btn_add_custom_service))
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ServicesList(
    services: List<Service>,
    onServiceSelected: (Service) -> Unit,
    onSuggestService: (name: String) -> Unit,
) {
    var searchString by remember {
        mutableStateOf("")
    }
    var searchEnabled by remember {
        mutableStateOf(false)
    }
    Column {
        SearchBar(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
            query = searchString,
            onQueryChange = {
                searchString = it
                if (it.isNotEmpty()) searchEnabled = true
            },
            onSearch = {},
            active = searchEnabled,
            onActiveChange = {},
            placeholder = {
                Text(text = stringResource(R.string.label_find_a_service))
            },
            trailingIcon = {
                if (searchEnabled) {
                    IconButton(onClick = { searchEnabled = false; searchString = "" }) {
                        Icon(Icons.Default.Close)
                    }
                }
            }) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(services.filter {
                    // TODO: Extract to viewModel
                    it.name.contains(
                        searchString, true
                    )
                }.sortedBy { service ->
                    when {
                        service.name.startsWith(
                            searchString, ignoreCase = true
                        ) -> 0

                        else -> 1
                    }
                }) {
                    ServiceRowItem(service = it, onClick = { onServiceSelected(it) })
                }
                item {
                    OtherServiceOption(onClick = {
                        onSuggestService(searchString)
                    })
                }
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(16.dp)
        ) {
            items(services) {
                ServiceRowItem(service = it, onClick = { onServiceSelected(it) })
            }
            item {
                OtherServiceOption(onClick = {
                    onSuggestService(searchString)
                })
            }
        }
    }

}

@Composable
internal fun OtherServiceOption(onClick: () -> Unit) {
    AbsentItem(text = "Looking for the other service?", onClick = onClick)
}