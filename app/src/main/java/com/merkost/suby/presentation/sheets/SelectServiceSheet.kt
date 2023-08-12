package com.merkost.suby.presentation.sheets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DashboardCustomize
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.merkost.suby.asWindowInsets
import com.merkost.suby.model.Category
import com.merkost.suby.model.Service
import com.merkost.suby.presentation.BaseItem
import com.merkost.suby.presentation.base.Icon
import com.merkost.suby.servicesByCategory

@Composable
fun SelectServiceSheet(onServiceSelected: (Category, Service) -> Unit) {
    LazyVerticalGrid(
        contentPadding = WindowInsets.systemBars.add(16.dp.asWindowInsets)
            .asPaddingValues(),
        columns = GridCells.Adaptive(150.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        servicesByCategory.forEach { (category, services) ->
            item(span = { GridItemSpan(this.maxLineSpan) }) {
                Row(
                    modifier = Modifier.padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ProvideTextStyle(value = MaterialTheme.typography.titleLarge) {
                        Text(text = category.emoji)
                        Text(text = category.categoryName)
                    }
                }
            }
            items(services) { service ->
                ServiceItem(service, onClick = { onServiceSelected(category, service) })
            }
            item {
                AddOwnServiceItem(
                    onClick = {
                        onServiceSelected(category, Service.CUSTOM)
                    }
                )
            }
            item(span = { GridItemSpan(this.maxLineSpan) }) {
                Spacer(modifier = Modifier.size(50.dp))
            }
        }
    }
}

@Composable
fun ServiceItem(service: Service, onClick: () -> Unit) {
    BaseItem(onClick = onClick) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            service.iconResource?.let {
                Image(
                    modifier = Modifier.size(50.dp),
                    painter = painterResource(id = service.iconResource),
                    contentDescription = ""
                )
            }
            Text(
                text = service.serviceName,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddOwnServiceItem(service: Service = Service.CUSTOM, onClick: () -> Unit) {
    Card(onClick = onClick) {
        Row(
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier,
                imageVector = Icons.Default.DashboardCustomize
            )
            Text(
                text = service.serviceName,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }

    }
}