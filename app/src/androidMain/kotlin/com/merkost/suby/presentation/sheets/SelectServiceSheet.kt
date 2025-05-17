package com.merkost.suby.presentation.sheets

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.merkost.suby.model.entity.full.Service
import com.merkost.suby.presentation.screens.AbsentItem
import com.merkost.suby.presentation.select.CustomServicesList
import com.merkost.suby.presentation.select.ServicesList
import com.merkost.suby.presentation.viewModel.SelectServiceViewModel
import com.merkost.suby.utils.AndroidConstants.SubyShape
import com.merkost.suby.utils.BaseViewState
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import suby.app.generated.resources.Res
import suby.app.generated.resources.custom_services
import suby.app.generated.resources.list

@Composable
fun SelectServiceSheet(
    selectedService: Service?,
    selectServiceViewModel: SelectServiceViewModel,
    onServiceSelected: (Service) -> Unit,
    onSuggestService: (name: String) -> Unit,
    onCustomServiceSelected: (Service) -> Unit,
    onPremiumClicked: () -> Unit
) {
    val customServices by selectServiceViewModel.customServices.collectAsStateWithLifecycle()
    val servicesState by selectServiceViewModel.servicesState.collectAsStateWithLifecycle()
    var initialPage = rememberSaveable { if (selectedService?.isCustomService == true) 1 else 0 }
    val pagerState =
        rememberPagerState(initialPage = initialPage, initialPageOffsetFraction = 0f) { 2 }
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
                        Button(onClick = {
                            selectServiceViewModel.refreshServices()
                        }) {
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
                                text = stringResource(Res.string.list),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        Tab(selected = pagerState.currentPage == 1, onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        }, modifier = Modifier.clip(SubyShape)) {
                            Text(
                                text = stringResource(Res.string.custom_services),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    HorizontalPager(
                        modifier = Modifier.fillMaxHeight(), state = pagerState
                    ) { page ->
                        SideEffect { initialPage = page }

                        if (page == 1) {
                            CustomServicesList(
                                customServices = customServices,
                                onCustomServiceSelected = onCustomServiceSelected,
                                onDeleteCustomService = {
                                    selectServiceViewModel.deleteCustomService(it)
                                },
                                onPremiumClicked = onPremiumClicked
                            )
                        } else {
                            ServicesList(
                                services = state.data,
                                onServiceSelected = onServiceSelected,
                                onSuggestService = onSuggestService,
                                viewModel = selectServiceViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun OtherServiceOption(onClick: () -> Unit) {
    AbsentItem(text = "Looking for the other service?", onClick = onClick)
}