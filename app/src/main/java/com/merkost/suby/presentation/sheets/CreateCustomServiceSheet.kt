package com.merkost.suby.presentation.sheets

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.rememberAsyncImagePainter
import com.merkost.suby.R
import com.merkost.suby.SubyShape
import com.merkost.suby.model.entity.full.Category
import com.merkost.suby.presentation.VerticalPicker
import com.merkost.suby.presentation.base.Icon
import com.merkost.suby.presentation.base.SubyTextField
import com.merkost.suby.presentation.base.TitleColumn
import com.merkost.suby.presentation.rememberPickerState
import com.merkost.suby.presentation.viewModel.CustomServiceData
import com.merkost.suby.presentation.viewModel.CustomServiceUiState
import com.merkost.suby.presentation.viewModel.CustomServiceViewModel
import com.merkost.suby.utils.analytics.ScreenLog
import com.merkost.suby.utils.analytics.Screens
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateCustomServiceSheet(
    onCreated: () -> Unit
) {
    ScreenLog(Screens.CreateCustomService)
    val viewModel: CustomServiceViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val categories by viewModel.categories.collectAsState()

    CustomServiceUiStateHandler(uiState) {
        onCreated()
        viewModel.resetUiState()
    }

    CustomServiceForm(
        initialServiceData = null,
        categories = categories,
        onSave = { newServiceData ->
            viewModel.createCustomService(newServiceData)
        },
        title = stringResource(R.string.create_custom_service),
        saveButtonText = stringResource(R.string.btn_create_service)
    )
}

@Composable
fun CustomServiceUiStateHandler(uiState: CustomServiceUiState?, onCreated: () -> Unit) {
    val context = LocalContext.current
    LaunchedEffect(uiState) {
        when (uiState) {
            CustomServiceUiState.Success -> {
                onCreated()
            }

            CustomServiceUiState.ServiceNameRequired -> {
                Toast.makeText(context, "Service name is required", Toast.LENGTH_SHORT).show()
            }

            CustomServiceUiState.CategoryRequired -> {
                Toast.makeText(context, "Category is required", Toast.LENGTH_SHORT).show()
            }

            CustomServiceUiState.ServiceNotFound -> {
                Toast.makeText(context, "Service not found", Toast.LENGTH_SHORT).show()
            }

            CustomServiceUiState.ImageProcessingError -> {
                Toast.makeText(context, "Failed to process image", Toast.LENGTH_SHORT).show()
            }

            is CustomServiceUiState.UnknownError -> {
                Toast.makeText(context, uiState.message, Toast.LENGTH_SHORT).show()
            }

            null -> {}
        }
    }
}

@Composable
fun CustomServiceForm(
    modifier: Modifier = Modifier,
    initialServiceData: CustomServiceData? = null,
    categories: List<Category>,
    onSave: (CustomServiceData) -> Unit,
    title: String,
    saveButtonText: String,
) {
    var serviceName by remember { mutableStateOf(initialServiceData?.name ?: "") }
    var imageUri by remember { mutableStateOf(initialServiceData?.imageUri) }
    val pickerState = rememberPickerState<Category>(initialServiceData?.category)
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top)
    ) {

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        TitleColumn(title = "What's your service?") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SubyTextField(
                    value = serviceName,
                    onValueChange = { serviceName = it },
                    modifier = Modifier
                        .weight(1f, true)
                        .focusRequester(focusRequester),
                    singleLine = true,
                    placeholder = {
                        Text(stringResource(R.string.service_name))
                    }
                )


                ImagePicker(
                    modifier = Modifier
                        .size(56.dp)
                        .aspectRatio(1f),
                    selectedImage = imageUri,
                    onImageSelected = { imageUri = it }
                )
            }
        }

        TitleColumn(title = "Pick a category") {
            AnimatedContent(categories) { categoriesList ->
                if (categoriesList.isNotEmpty()) {
                    VerticalPicker(
                        items = categoriesList,
                        state = pickerState,
                        visibleItemsCount = 5,
                        modifier = Modifier.padding(vertical = 16.dp),
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    val selectedCategory = pickerState.selectedItem
                    val customServiceData = CustomServiceData(
                        name = serviceName,
                        imageUri = imageUri,
                        category = selectedCategory
                    )
                    onSave(customServiceData)
                },
                modifier = Modifier.weight(1f),
                enabled = serviceName.isNotBlank()
            ) {
                Text(saveButtonText)
            }
        }
    }
}

@Composable
fun ImagePicker(
    modifier: Modifier,
    selectedImage: Uri?,
    onImageSelected: (Uri?) -> Unit,
    placeholderResId: Int = R.drawable.add_photo_placeholder
) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let(onImageSelected)
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (selectedImage != null) {
            Icon(
                imageVector = Icons.Outlined.Close,
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 3.dp, y = (-3).dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .clickable {
                        onImageSelected(null)
                    }
                    .background(
                        Color.Black.copy(alpha = 0.6f),
                        shape = CircleShape
                    )
                    .padding(3.dp)
                    .zIndex(1f)

            )
        }

        Box(
            modifier = Modifier
                .clip(SubyShape)
                .clickable { launcher.launch("image/*") }
        ) {
            if (selectedImage != null) {
                Image(
                    painter = rememberAsyncImagePainter(model = selectedImage),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = placeholderResId),
                    contentDescription = "Placeholder Image",
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                )
            }
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