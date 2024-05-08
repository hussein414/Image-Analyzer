package com.example.myapplication.ui.view.component

import androidx.annotation.FloatRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun Ring(
    modifier: Modifier = Modifier,
    diameter: Dp,
    @FloatRange(from = 0.0, 1.0)
    ringSize: Float = .1f,
    color: Color
) {
    Canvas(
        modifier = modifier.size(diameter),
        onDraw = {
            drawRing(
                color = color,
                diameter = diameter,
                ringFraction = ringSize
            )
        }
    )
}

fun DrawScope.drawRing(
    color: Color,
    diameter: Dp = 100.dp,
    @FloatRange(from = 0.0, 1.0)
    ringFraction: Float = .1f,
    offset: Offset = Offset.Zero
) {
    val path = Path().apply {
        val size = diameter.toPx()
        addOval(Rect(0f, 0f, size, size))
        op(
            path1 = this,
            path2 = Path().apply {
                addOval(
                    Rect(0f, 0f, size * (1 - ringFraction), size * (1 - ringFraction))
                )
                translate(Offset(size * ringFraction / 2, size * ringFraction / 2))
            },
            operation = PathOperation.Difference
        )
        if (offset != Offset.Zero)
            translate(offset.copy(offset.x - size / 2, offset.y - size / 2))
    }
    drawPath(path, color)
}
