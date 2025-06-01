package com.merkost.suby.presentation.base.components.service

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.merkost.suby.SubyShape
import com.merkost.suby.model.entity.full.Category
import com.merkost.suby.model.entity.full.Service
import com.merkost.suby.presentation.base.PlaceholderHighlight
import com.merkost.suby.presentation.base.fade
import com.merkost.suby.presentation.base.placeholder3
import com.merkost.suby.utils.now
import com.merkost.suby.utils.toEpochMillis
import kotlinx.datetime.LocalDateTime
import timber.log.Timber
import java.text.DateFormat
import java.util.Date


@Composable
fun ServiceSvg(
    modifier: Modifier = Modifier,
    link: String,
    contentScale: ContentScale = ContentScale.Fit
) {
    SubcomposeAsyncImage(
        modifier = modifier,
        model = link,
        loading = {
            Box(
                modifier = modifier.placeholder3(
                    true,
                    shape = SubyShape,
                    highlight = PlaceholderHighlight.fade()
                ),
            )
        },
        onError = {
            //todo: Check if it is java.io.FileNotFoundException
            Timber.tag("ServiceLogo").e(it.result.throwable)
        },
        contentScale = contentScale,
        contentDescription = ""
    )
}

@Composable
fun ServiceLogo(
    modifier: Modifier = Modifier,
    service: Service,
    shape: Shape = SubyShape
) {
    if (service.logoUrl != null) {
        ServiceSvg(
            modifier = modifier
                .clip(shape)
                .then(
                    if (service.isCustomService) Modifier.aspectRatio(1f) else Modifier
                ),
            link = service.logoUrl,
            contentScale = if (service.isCustomService) ContentScale.Crop else ContentScale.Fit
        )
    } else {
        ServiceNameImage(
            modifier = modifier,
            shape = shape,
            name = service.name
        )
    }
}

@Composable
fun ServiceNameImage(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    name: String
) {
    Box(
        modifier = modifier
            .size(64.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.take(1).uppercase(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun BaseService(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = SubyShape,
        onClick = onClick,
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
    ) {
        content()
    }
}

@Composable
fun ServiceRowItem(
    modifier: Modifier = Modifier,
    service: Service,
    showCreatedAt: Boolean = false,
    showCategory: Boolean = false,
    onClick: () -> Unit
) {
    BaseService(
        modifier = modifier
            .fillMaxWidth(),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f, false),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ServiceLogo(
                    modifier = Modifier
                        .size(56.dp),
                    service = service
                )
                Column(verticalArrangement = Arrangement.Center) {
                    Text(
                        text = service.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (showCategory) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = service.category.beautifulName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            if (showCreatedAt) {
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = DateFormat.getDateInstance()
                        .format(Date(service.createdAt.toEpochMillis())),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Preview
@Composable
private fun ServiceRowItemLongPreview() {
    ServiceRowItem(
        service = Service(
            id = 1,
            name = "Netflix but with a very and very long name for two ",
            category = Category(
                id = 1,
                name = "Streaming",
                emoji = "📺",
                createdAt = LocalDateTime.now()
            ),
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/0/08/Netflix_2015_logo.svg",
            createdAt = LocalDateTime.now(),
            isCustomService = true,
            lastUpdated = LocalDateTime.now()
        ),
        showCreatedAt = true,
        showCategory = true,
        onClick = {}
    )
}

@Preview
@Composable
private fun ServiceRowItemShortPreview() {
    ServiceRowItem(
        service = Service(
            id = 1,
            name = "Netflix",
            category = Category(
                id = 1,
                name = "Streaming",
                emoji = "📺",
                createdAt = LocalDateTime.now()
            ),
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/0/08/Netflix_2015_logo.svg",
            createdAt = LocalDateTime.now(),
            isCustomService = true,
            lastUpdated = LocalDateTime.now()
        ),
        showCreatedAt = true,
        showCategory = true,
        onClick = {}
    )
}