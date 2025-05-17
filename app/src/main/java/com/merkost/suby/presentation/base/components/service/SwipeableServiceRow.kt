package com.merkost.suby.presentation.base.components.service

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.merkost.suby.SubyShape
import com.merkost.suby.model.entity.full.Service
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun CustomServiceRowItem(
    modifier: Modifier = Modifier,
    service: Service,
    onCustomServiceSelected: (Service) -> Unit,
    onEditService: () -> Unit,
    onDeleteService: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = SubyShape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ServiceRowItem(
                modifier = Modifier.weight(1f),
                service = service,
                showCreatedAt = true,
                showCategory = true,
                onClick = { onCustomServiceSelected(service) }
            )

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    shape = SubyShape,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        onClick = {
                            onEditService()
                            showMenu = false
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Delete") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        },
                        onClick = {
                            onDeleteService()
                            showMenu = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SwipeableServiceRow(
    modifier: Modifier,
    service: Service,
    onCustomServiceSelected: (Service) -> Unit,
    onEditService: (Service) -> Unit,
    onDeleteService: (Service) -> Unit
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val maxSwipeDistance = with(density) { 64.dp.toPx() }

    val offsetX = remember { Animatable(0f) }
    val draggableState = rememberDraggableState { delta ->
        scope.launch {
            val newOffset =
                (offsetX.value + delta).coerceIn(-maxSwipeDistance * 1.5f, maxSwipeDistance * 1.5f)
            offsetX.snapTo(newOffset)
        }
    }

    val draggableModifier = Modifier
        .fillMaxWidth()
        .draggable(
            state = draggableState,
            orientation = Orientation.Horizontal,
            enabled = true,
            onDragStopped = { velocity ->
                scope.launch {
                    when {
                        offsetX.value <= -maxSwipeDistance -> {
                            onEditService(service)
                        }

                        offsetX.value >= maxSwipeDistance -> {
                            onDeleteService(service)
                        }
                    }
                    offsetX.animateTo(
                        targetValue = 0f,
                        initialVelocity = velocity,
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                    )
                }
            }
        )

    Box(
        modifier = draggableModifier.then(modifier),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clip(SubyShape),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val deleteSwipeProgress by animateFloatAsState(
                (offsetX.value / maxSwipeDistance).coerceIn(0f, 1f)
            )
            val editSwipeProgress by animateFloatAsState(
                (-offsetX.value / maxSwipeDistance).coerceIn(0f, 1f)
            )

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = deleteSwipeProgress)),
                contentAlignment = Alignment.CenterStart
            ) {
                IconButton(
                    onClick = {},
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = Icons.Default.Delete.name,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = editSwipeProgress)),
                contentAlignment = Alignment.CenterEnd
            ) {
                IconButton(
                    onClick = {},
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = Icons.Default.Edit.name,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        ServiceRowItem(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) },
            showCreatedAt = true,
            showCategory = true,
            service = service,
            onClick = {
                onCustomServiceSelected(service)
            }
        )
    }
}