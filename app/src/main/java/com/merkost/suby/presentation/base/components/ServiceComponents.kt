package com.merkost.suby.presentation.base.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.merkost.suby.SubyShape
import com.merkost.suby.model.entity.full.Service
import com.merkost.suby.presentation.base.BaseItem
import com.merkost.suby.presentation.base.PlaceholderHighlight
import com.merkost.suby.presentation.base.fade
import com.merkost.suby.presentation.base.placeholder3
import com.merkost.suby.utils.toEpochMillis
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
        model = ImageRequest.Builder(LocalContext.current.applicationContext)
            .data(link)
            .decoderFactory(SvgDecoder.Factory())
            .build(),
        loading = {
            Box(
                modifier = modifier.placeholder3(
                    true,
                    shape = SubyShape,
                    highlight = PlaceholderHighlight.fade()
                ),
            )
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
    service.logoUrl?.let {
        ServiceSvg(
            modifier = modifier
                .clip(shape),
            link = it
        )
    } ?: Box(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = service.name.take(1),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.headlineSmall
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
fun ServiceItem(
    service: Service,
    modifier: Modifier = Modifier
) {
    BaseItem {

        Row(
            modifier = modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ServiceLogo(
                modifier = Modifier
                    .size(64.dp),
                service = service
            )
//
//            Text(
//                text = service.name,
//                style = MaterialTheme.typography.titleLarge,
//                color = MaterialTheme.colorScheme.onSurface,
//                maxLines = 2,
//                overflow = TextOverflow.Ellipsis
//            )
        }
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
                    text = DateFormat.getDateInstance()
                        .format(Date(service.createdAt.toEpochMillis())),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}