package com.merkost.suby.presentation


import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.merkost.suby.SubyShape
import com.merkost.suby.model.entity.Currency
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun <T> rememberPickerState() = remember { PickerState<T>() }

class PickerState<T> {
    var selectedItem by mutableStateOf<T?>(null)
}

@Composable
fun <T> Picker(
    items: List<T>,
    state: PickerState<T> = rememberPickerState(),
    modifier: Modifier = Modifier,
    startIndex: Int = 0,
    visibleItemsCount: Int = 3,
    dividerColor: Color = LocalContentColor.current,
    pickerItem: @Composable (item: T, modifier: Modifier) -> Unit
) {

    val visibleItemsMiddle = visibleItemsCount / 2
    val listScrollCount = items.size
    val listScrollMiddle = listScrollCount / 2
    val listStartIndex =
        listScrollMiddle - listScrollMiddle % items.size - visibleItemsMiddle + startIndex

    fun getItem(index: Int) = items[index % items.size]

    val coroutineScope = rememberCoroutineScope()

    val listState =
        rememberLazyListState(initialFirstVisibleItemIndex = listStartIndex.coerceAtLeast(0))
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    val itemWidthPixels = remember { mutableIntStateOf(0) }
    val itemHeightPixels = remember { mutableIntStateOf(0) }
    val itemWidthDp = pixelsToDp(itemWidthPixels.intValue)
    val itemHeightDp = pixelsToDp(itemHeightPixels.intValue)

    val fadingEdgeGradient = remember {
        Brush.horizontalGradient(
            0f to Transparent,
            0.5f to Color.Black,
            1f to Transparent
        )
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .map { index -> getItem(index) }
            .distinctUntilChanged()
            .collect { item -> state.selectedItem = item }
    }

    Box(modifier = modifier) {
        LazyRow(
            state = listState,
            flingBehavior = flingBehavior,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .width(itemWidthDp * visibleItemsCount)
                .fadingEdge(fadingEdgeGradient)
        ) {
            items(visibleItemsCount / 2) {
                Box(
                    modifier = Modifier
                        .width(itemWidthDp)
                        .height(itemHeightDp)
                )
            }
            items(listScrollCount) { index ->
                pickerItem(
                    getItem(index),
                    Modifier
                        .onSizeChanged { size ->
                            itemWidthPixels.intValue = size.width
                            itemHeightPixels.intValue = size.height
                        }
                        .clip(SubyShape)
                        .clickable {
                            coroutineScope.launch {
                                listState.animateScrollToItem(index)
                            }
                        })
            }
            items(visibleItemsCount / 2) {
                Box(
                    modifier = Modifier
                        .width(itemWidthDp)
                        .height(itemHeightDp)
                )
            }
        }

        Box(
            modifier = Modifier
                .offset(x = itemWidthDp * (visibleItemsMiddle + 1) - itemWidthDp)
                .height(itemHeightDp)
                .width(itemWidthDp)
                .border(
                    2.dp, dividerColor, RoundedCornerShape(16.dp)
                )
        )
    }
}

private fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }

@Composable
private fun pixelsToDp(pixels: Int) = with(LocalDensity.current) { pixels.toDp() }

@Composable
fun FadingEdgeCurrencyPicker() {

    val lazyListState = rememberLazyListState()
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState)

    val leftRightFade = Brush.horizontalGradient(
        0f to Transparent,
        0.5f to Color.Red,
        0.5f to Color.Red,
        1f to Transparent
    )

    LazyRow(
        state = lazyListState,
        flingBehavior = snapBehavior,
        modifier = Modifier
            .fadingEdge(brush = leftRightFade)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        itemsIndexed(Currency.entries.toList()) { index, item ->
            BorderedCurrency(lazyListState = lazyListState, currency = item, index = index)
        }
    }
}

@Composable
fun BorderedCurrency(lazyListState: LazyListState, currency: Currency, index: Int) {

    val borderColor by remember {
        derivedStateOf {
            val layoutInfo = lazyListState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            val itemInfo = visibleItemsInfo.firstOrNull { it.index == index }

            itemInfo?.let {

                val delta = it.size / 2 //use your custom logic
                val center = lazyListState.layoutInfo.viewportEndOffset / 2
                val childCenter = it.offset + it.size / 2
                val target = childCenter - center
                if (target in -delta..delta) return@derivedStateOf Gray
            }
            Transparent
        }
    }


    CurrencyLabel(
        modifier = Modifier
            .padding(8.dp)
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .wrapContentHeight(),
        currency = currency,
        showArrow = false
    )


}

@Composable
fun <T> LayoutPicker(
    items: List<T>,
    state: PickerState<T> = rememberPickerState(),
    modifier: Modifier = Modifier,
    startIndex: Int = 0,
    visibleItemsCount: Int = 3,
    dividerColor: Color = LocalContentColor.current,
    pickerItem: @Composable (item: T, modifier: Modifier) -> Unit
) {
    val visibleItemsMiddle = visibleItemsCount / 2
    val listScrollCount = items.size
    val listScrollMiddle = listScrollCount / 2
    val listStartIndex = listScrollMiddle - listScrollMiddle % items.size - visibleItemsMiddle + startIndex

    fun getItem(index: Int) = items[index % items.size]

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState(initial = listStartIndex.coerceAtLeast(0))
    val itemSizeDp = 100.dp
    val itemSizePx = with(LocalDensity.current) { itemSizeDp.toPx() }

    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.value / itemSizePx.toInt() }
            .map { index -> getItem(index) }
            .distinctUntilChanged()
            .collect { item -> state.selectedItem = item }
    }

    Box(modifier = modifier) {
        Layout(
            content = {
                // Padding items before the actual content
                repeat(visibleItemsMiddle) {
                    Box(modifier = Modifier.size(itemSizeDp))
                }
                items.forEachIndexed { index, item ->
                    Box(
                        modifier = Modifier
                            .size(itemSizeDp)
                            .clickable {
                                coroutineScope.launch {
                                    scrollState.animateScrollTo(scrollState.value + itemSizePx.toInt() * (index - visibleItemsMiddle))
                                }
                            }
                    ) {
                        pickerItem(item, Modifier)
                    }
                }
                // Padding items after the actual content
                repeat(visibleItemsMiddle) {
                    Box(modifier = Modifier.size(itemSizeDp))
                }
            },
            modifier = Modifier
                .padding(16.dp)
                .horizontalScroll(scrollState)
        ) { measurables, constraints ->
            val itemConstraints = constraints.copy(
                minWidth = 0,
                minHeight = 0,
                maxWidth = (itemSizeDp.toPx()).toInt(),
                maxHeight = (itemSizeDp.toPx()).toInt()
            )
            val placeables = measurables.map { it.measure(itemConstraints) }
            val totalWidth = placeables.sumOf { it.width }
            layout(totalWidth, constraints.maxHeight) {
                var xPosition = 0
                placeables.forEach { placeable ->
                    placeable.placeRelative(x = xPosition, y = (constraints.maxHeight - placeable.height) / 2)
                    xPosition += placeable.width
                }
            }
        }

        Box(
            modifier = Modifier
                .offset(x = itemSizeDp * (visibleItemsMiddle + 1) - itemSizeDp)
                .size(itemSizeDp)
                .border(2.dp, dividerColor, RoundedCornerShape(16.dp))
        )
    }
}

@Composable
fun <T> VerticalPicker(
    items: List<T>,
    state: PickerState<T> = rememberPickerState(),
    modifier: Modifier = Modifier,
    startIndex: Int = 0,
    visibleItemsCount: Int = 3,
    dividerColor: Color = LocalContentColor.current,
    pickerItem: @Composable (item: T, modifier: Modifier) -> Unit
) {
    val visibleItemsMiddle = visibleItemsCount / 2
    val listScrollCount = items.size
    val listScrollMiddle = listScrollCount / 2
    val listStartIndex =
        listScrollMiddle - listScrollMiddle % items.size - visibleItemsMiddle + startIndex

    fun getItem(index: Int) = items[index % items.size]

    val coroutineScope = rememberCoroutineScope()

    val listState =
        rememberLazyListState(initialFirstVisibleItemIndex = listStartIndex.coerceAtLeast(0))
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    val itemWidthPixels = remember { mutableIntStateOf(0) }
    val itemHeightPixels = remember { mutableIntStateOf(0) }
    val itemWidthDp = pixelsToDp(itemWidthPixels.intValue)
    val itemHeightDp = pixelsToDp(itemHeightPixels.intValue)

    val fadingEdgeGradient = remember {
        Brush.verticalGradient(
            0f to Transparent,
            0.5f to Color.Black,
            1f to Transparent
        )
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .map { index -> getItem(index) }
            .distinctUntilChanged()
            .collect { item -> state.selectedItem = item }
    }

    Box(modifier = modifier) {
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .height(itemHeightDp * visibleItemsCount)
                .fadingEdge(fadingEdgeGradient)
        ) {
            items(visibleItemsCount / 2) {
                Box(
                    modifier = Modifier
                        .width(itemWidthDp)
                        .height(itemHeightDp)
                )
            }
            items(listScrollCount) { index ->
                pickerItem(
                    getItem(index),
                    Modifier
                        .onSizeChanged { size ->
                            itemWidthPixels.intValue = size.width
                            itemHeightPixels.intValue = size.height
                        }
                        .clip(SubyShape)
                        .clickable {
                            coroutineScope.launch {
                                listState.animateScrollToItem(index)
                            }
                        })
            }
            items(visibleItemsCount / 2) {
                Box(
                    modifier = Modifier
                        .width(itemWidthDp)
                        .height(itemHeightDp)
                )
            }
        }

        Box(
            modifier = Modifier
                .offset(y = itemHeightDp * (visibleItemsMiddle + 1) - itemHeightDp)
                .height(itemHeightDp)
                .width(itemWidthDp)
                .border(
                    2.dp, dividerColor, SubyShape
                )
        )
    }
}




