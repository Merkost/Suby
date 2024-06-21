package com.merkost.suby.presentation.sheets

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.merkost.suby.R
import com.merkost.suby.SubyShape
import com.merkost.suby.model.entity.full.Category
import com.merkost.suby.model.room.entity.CategoryDb
import com.merkost.suby.presentation.VerticalPicker
import com.merkost.suby.presentation.base.SubyTextField
import com.merkost.suby.presentation.base.TitleColumn
import com.merkost.suby.presentation.rememberPickerState
import com.merkost.suby.viewModel.CustomServiceUiState
import com.merkost.suby.viewModel.CustomServiceViewModel

@Composable
fun CreateCustomServiceSheet(
    onCreated: () -> Unit
) {

    val viewModel: CustomServiceViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val customServiceData by viewModel.customServiceData.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val pickerState = rememberPickerState<Category>()

    val context = LocalContext.current

    LaunchedEffect(uiState) {
        when(uiState) {
            CustomServiceUiState.Success -> {
                onCreated()
            }
            CustomServiceUiState.ServiceNameRequired -> {
                Toast.makeText(context, "Service name is required", Toast.LENGTH_SHORT).show()
            }
            CustomServiceUiState.CategoryRequired -> {
                Toast.makeText(context, "Category is required", Toast.LENGTH_SHORT).show()
            }

            else -> {}
        }
    }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Create Custom Service",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        TitleColumn(title = "What's your service?") {
            SubyTextField(
                value = customServiceData.name,
                onValueChange = viewModel::setServiceName,
                modifier = Modifier
                    .fillMaxWidth(),
                singleLine = true,
                placeholder = { Text(stringResource(R.string.service_name)) }
            )
        }


        TitleColumn(title = "Pick a category") {
            AnimatedContent(categories) { categoriesList ->
                if (categoriesList.size > 1) {
                    VerticalPicker(
                        items = categoriesList,
                        state = pickerState,
                        visibleItemsCount = 3,
                        modifier = Modifier.padding(16.dp),
                        pickerItem = { item, modifier ->
                            CategoryLabel(
                                modifier = modifier.padding(4.dp),
                                category = item,
                            )
                        }
                    )
                }
            }

        }

//        Text(
//            text = "Pick a color:",
//            style = MaterialTheme.typography.bodyMedium
//        )
//
//        ColorPicker(
//            selectedColor = selectedColor,
//            onColorSelected = { selectedColor = it }
//        )
//
        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                    viewModel.createCustomService(pickerState.selectedItem)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = customServiceData.name.isNotBlank()
        ) {
            Text(stringResource(R.string.btn_create_service))
        }
    }
}

@Composable
fun ColorPicker(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    // Example colors, adjust as needed
    val colors = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Magenta)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color)
                    .clickable { onColorSelected(color) }
                    .border(
                        width = 2.dp,
                        color = if (color == selectedColor) Color.Black else Color.Transparent,
                        shape = MaterialTheme.shapes.small
                    )
            )
        }
    }
}


@Composable
fun CategoryLabel(
    modifier: Modifier = Modifier,
    category: Category
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .background(MaterialTheme.colorScheme.surface, SubyShape)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = category.emoji ?: "❓",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = category.name,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}