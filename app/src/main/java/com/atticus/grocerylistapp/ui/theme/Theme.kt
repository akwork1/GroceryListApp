package com.atticus.grocerylistapp.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.unit.sp


private val DarkColors = darkColorScheme(
    primary = Color(0xFF7EE787),
    onPrimary = Color(0xFF003314),
    surface = Color(0xFF0B0F10),
    onSurface = Color(0xFFE6EAE9),
    background = Color(0xFF0B0F10),
    onBackground = Color(0xFFE6EAE9),
    secondary = Color(0xFF7DD3FC),
    outline = Color(0xFF273033)
)

private val AppShapes = Shapes(
    extraSmall = ShapeDefaults.ExtraSmall.copy(all = CornerSize(10.dp)),
    small = ShapeDefaults.Small.copy(all = CornerSize(14.dp)),
    medium = ShapeDefaults.Medium.copy(all = CornerSize(18.dp)),
    large = ShapeDefaults.Large.copy(all = CornerSize(24.dp)),
    extraLarge = ShapeDefaults.ExtraLarge.copy(all = CornerSize(28.dp)),
)

@Composable
fun GroceryTheme(content: @Composable () -> Unit) {
    val colors = DarkColors
    MaterialTheme(
        colorScheme = colors,
        shapes = AppShapes,
        typography = Typography(
            titleLarge = Typography().titleLarge.copy(letterSpacing = 0.2.sp),
            bodyLarge = Typography().bodyLarge,
            bodyMedium = Typography().bodyMedium
        ),
        content = content
    )
}
