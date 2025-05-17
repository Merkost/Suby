package com.merkost.suby.presentation.select

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.merkost.suby.R
import com.merkost.suby.model.entity.full.Service
import com.merkost.suby.presentation.base.Icon
import com.merkost.suby.presentation.base.components.service.ServiceRowItem
import com.merkost.suby.presentation.sheets.OtherServiceOption
import com.merkost.suby.presentation.viewModel.SelectServiceViewModel
import com.merkost.suby.utils.analytics.ScreenLog
import com.merkost.suby.utils.analytics.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ServicesList(
    services: List<Service>,
    onServiceSelected: (Service) -> Unit,
    onSuggestService: (name: String) -> Unit,
    viewModel: SelectServiceViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    ScreenLog(Screens.Services)
    var searchString by remember {
        mutableStateOf("")
    }
    var searchEnabled by remember {
        mutableStateOf(false)
    }
    Column {
        val searchBarColors = SearchBarDefaults.colors()
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = searchString,
                    onQueryChange = {
                        searchString = it
                        if (it.isNotEmpty()) searchEnabled = true
                    },
                    onSearch = {},
                    expanded = searchEnabled,
                    onExpandedChange = { searchEnabled = it },
                    enabled = true,
                    placeholder = {
                        Text(text = stringResource(R.string.label_find_a_service))
                    },
                    leadingIcon = null,
                    trailingIcon = {
                        if (searchEnabled) {
                            IconButton(onClick = { searchEnabled = false; searchString = "" }) {
                                Icon(Icons.Default.Close)
                            }
                        }
                    },
                    interactionSource = null,
                )
            },
            expanded = searchEnabled,
            onExpandedChange = { searchEnabled = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = SearchBarDefaults.inputFieldShape,
            colors = searchBarColors,
            tonalElevation = SearchBarDefaults.TonalElevation,
            shadowElevation = SearchBarDefaults.ShadowElevation,
            windowInsets = WindowInsets(0, 0, 0, 0),
            content = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    val filteredServices = viewModel.filterServices(services, searchString)
                    items(filteredServices, key = { it.id }) {
                        ServiceRowItem(service = it, onClick = { onServiceSelected(it) })
                    }
                    item {
                        OtherServiceOption(onClick = {
                            onSuggestService(searchString)
                        })
                    }
                }
            },
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(16.dp)
        ) {
            items(services, key = { "service_" + it.id }) {
                ServiceRowItem(service = it, onClick = { onServiceSelected(it) })
            }
            item {
                OtherServiceOption(onClick = { onSuggestService(searchString) })
            }
        }
    }

}