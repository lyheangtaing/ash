package ash.core.designsystem

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object AshSpacing {
    val xs: Dp = 4.dp
    val sm: Dp = 8.dp
    val md: Dp = 12.dp
    val lg: Dp = 16.dp
    val xl: Dp = 20.dp
    val screen: Dp = 16.dp
    val bottomNavPadding: Dp = 116.dp
}

object AshRadius {
    val sm: Dp = 10.dp
    val md: Dp = 14.dp
    val lg: Dp = 18.dp
    val pill: Dp = 999.dp
}

object AshStatusPalette {
    val available = Color(0xFF0E6B3E)
    val assigned = Color(0xFF254E9B)
    val maintenance = Color(0xFF8A5A00)
    val lost = Color(0xFF9E1B1B)
    val retired = Color(0xFF5E5E58)
    val damaged = Color(0xFF8B2E18)
}

@Composable
fun AshPanel(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    elevated: Boolean = false,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(AshRadius.md),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shadowElevation = if (elevated) 10.dp else 0.dp
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .animateContentSize()
                .padding(contentPadding)
        ) {
            content()
        }
    }
}

@Composable
fun AshHeroPanel(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(AshRadius.lg)
    Surface(
        modifier = modifier,
        shape = shape,
        color = Color.Black,
        contentColor = Color.White,
        shadowElevation = 14.dp
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF202020), Color.Black)
                    ),
                    shape = shape
                )
                .padding(contentPadding)
        ) {
            content()
        }
    }
}

@Composable
fun AshStatusDot(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(99.dp),
        color = if (isActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        content = {}
    )
}

@Composable
fun RowScope.EqualWeightPanel(
    contentPadding: PaddingValues = PaddingValues(14.dp),
    content: @Composable () -> Unit
) {
    AshPanel(
        modifier = Modifier.weight(1f),
        contentPadding = contentPadding,
        content = content
    )
}
