package com.zhulin.guideshop.core.presentation.components

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

@Composable
fun CustomIconButton(
    onClick: () -> Unit,
    painter: Painter,
    contentDescription: String
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .requiredHeightIn(48.dp)
            .fillMaxHeight(1f)
            .aspectRatio(1f)
    ) {
        Icon(
            painter = painter,
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxHeight(0.5f)
                .aspectRatio(1f)
        )
    }
}