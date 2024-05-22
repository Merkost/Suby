package com.merkost.suby.presentation.sheets

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.merkost.suby.R
import com.merkost.suby.SubyShape
import com.merkost.suby.asWindowInsets
import com.merkost.suby.model.entity.dto.ServiceDto
import com.merkost.suby.model.room.entity.CategoryDb
import com.merkost.suby.model.room.entity.ServiceDb
import com.merkost.suby.model.room.entity.ServiceWithCategory
import com.merkost.suby.presentation.AbsentItem
import com.merkost.suby.presentation.BaseItem
import com.merkost.suby.presentation.base.Icon
import com.merkost.suby.presentation.base.PlaceholderHighlight
import com.merkost.suby.presentation.base.fade
import com.merkost.suby.presentation.base.placeholder3
import com.merkost.suby.utils.BaseViewState
import kotlinx.coroutines.launch

@Composable
fun SelectServiceSheet(
    servicesState: BaseViewState<List<ServiceWithCategory>>,
    onServiceSelected: (ServiceWithCategory) -> Unit,
    onServiceAbsent: (inputText: String) -> Unit,
    onRetryLoadServices: () -> Unit,
) {
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
                if (state.data.isEmpty()) {
                    AbsentItem(
                        text = "No services found",
                        onClick = {
                            onServiceAbsent("")
                        }
                    )
                } else {
                    Column {
                        val pagerState = rememberPagerState(
                            initialPage = 0,
                            initialPageOffsetFraction = 0f
                        ) { 2 }
                        val coroutineScope = rememberCoroutineScope()
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
                                    text = stringResource(id = R.string.categories),
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                        HorizontalPager(state = pagerState) { page ->
                            if (page == 1) {
                                CategoriesPage(state.data, onServiceSelected, onServiceAbsent)
                            } else {
                                ListPage(state.data, onServiceSelected, onServiceAbsent) {}
                            }
                        }
                    }
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListPage(
    servicesWithCategories: List<ServiceWithCategory>,
    onServiceSelected: (ServiceWithCategory) -> Unit,
    onServiceAbsent: (inputText: String) -> Unit,
    onSearch: (String) -> Unit,
) {

    var searchString by remember {
        mutableStateOf("")
    }
    var searchEnabled by remember {
        mutableStateOf(false)
    }
    Column {
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            query = searchString,
            onQueryChange = {
                searchString = it
                if (it.isNotEmpty()) searchEnabled = true
            },
            onSearch = onSearch,
            active = searchEnabled,
            onActiveChange = {},
            placeholder = {
                Text(text = "Find a service")
            },
            trailingIcon = {
                if (searchEnabled) {
                    IconButton(onClick = { searchEnabled = false; searchString = "" }) {
                        Icon(Icons.Default.Close)
                    }
                }
            }
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(servicesWithCategories.filter { it.service.name.contains(searchString, true) }
                    .sortedBy { serviceWithCategory ->
                        when {
                            serviceWithCategory.service.name.startsWith(
                                searchString,
                                ignoreCase = true
                            ) -> 0

                            else -> 1
                        }
                    }) {
                    ServiceHorizontalItem(
                        service = it.service,
                        onClick = { onServiceSelected(it) }
                    )
                }
                item {
                    AbsentItem(
                        text = "Want to add another service?",
                        onClick = { onServiceAbsent(searchString) })
                }
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(servicesWithCategories) {
                ServiceHorizontalItem(service = it.service, onClick = { onServiceSelected(it) })
            }
        }
    }

}

@Composable
fun CategoriesPage(
    servicesWithCategories: List<ServiceWithCategory>,
    onServiceSelected: (ServiceWithCategory) -> Unit,
    onServiceAbsent: (inputText: String) -> Unit
) {
    LazyVerticalGrid(
        contentPadding = WindowInsets.systemBars.add(16.dp.asWindowInsets)
            .asPaddingValues(),
        columns = GridCells.Adaptive(150.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        servicesWithCategories.groupBy { it.category }.forEach { service ->
            val category = service.key
            val services = service.value
            item(span = { GridItemSpan(this.maxLineSpan) }) {
                CategoryName(modifier = Modifier.padding(bottom = 8.dp), category)
            }
            items(services) { service ->
                ServiceVerticalItem(service.service, onClick = { onServiceSelected(service) })
            }
            item(span = { GridItemSpan(this.maxLineSpan) }) {
                Spacer(modifier = Modifier.size(30.dp))
            }

        }
        item(span = { GridItemSpan(this.maxLineSpan) }) {
            AbsentItem(text = "Can't find your service?", onClick = {
                onServiceAbsent("")
            })
        }
    }
}

@Composable
fun CategoryName(
    modifier: Modifier = Modifier,
    category: CategoryDb,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ProvideTextStyle(value = textStyle) {
            Text(text = category.emoji)
            Text(text = category.name)
        }
    }
}

@Composable
fun ServiceVerticalItem(service: ServiceDb, onClick: () -> Unit) {
    BaseItem(onClick = onClick) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            service.logoLink?.let {
                ServiceSvg(
                    modifier = Modifier.height(50.dp),
                    link = it,
                )
            }
            Text(
                text = service.name,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
fun ServiceHorizontalItem(
    modifier: Modifier = Modifier,
    service: ServiceDb,
    cardColors: CardColors = CardDefaults.cardColors(),
    onClick: (() -> Unit)? = null
) {
    BaseItem(modifier = modifier, onClick = onClick, colors = cardColors) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = service.name,
                textAlign = TextAlign.Start,
                maxLines = 1,
            )
            service.logoLink?.let {
                ServiceSvg(
                    modifier = Modifier
                        .height(40.dp)
                        .padding(start = 32.dp)
                        .weight(1f, false),
                    link = it,
                )
            }
        }
    }
}

@Composable
fun ServiceSvg(
    modifier: Modifier = Modifier,
    link: String,
    contentScale: ContentScale = ContentScale.Fit
) {
    SubcomposeAsyncImage(
        modifier = modifier,
        model = ImageRequest.Builder(LocalContext.current)
            .data(link)
            .decoderFactory(SvgDecoder.Factory())
            .build(),
        loading = {
            Surface(
                modifier = modifier.placeholder3(
                    true,
                    shape = SubyShape,
                    highlight = PlaceholderHighlight.fade()
                ),
            ) {}
        },
        contentScale = contentScale,
        contentDescription = ""
    )
}