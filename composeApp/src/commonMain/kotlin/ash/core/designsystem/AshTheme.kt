package ash.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AshLightColors = lightColorScheme(
    primary = Color.Black,
    onPrimary = Color.White,
    secondary = Color(0xFF333333),
    onSecondary = Color.White,
    background = Color(0xFFFAFAF8),
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFF5F5F2),
    onSurfaceVariant = Color(0xFF5E5E58),
    outline = Color(0xFFE4E2DC),
    outlineVariant = Color(0xFFF0EEE8),
    error = Color(0xFF9E1B1B),
    onError = Color.White
)

private val AshDarkColors = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    secondary = Color(0xFFD7D4CB),
    onSecondary = Color.Black,
    background = Color(0xFF11110F),
    onBackground = Color(0xFFF6F4EF),
    surface = Color(0xFF191916),
    onSurface = Color(0xFFF6F4EF),
    surfaceVariant = Color(0xFF24231F),
    onSurfaceVariant = Color(0xFFC8C3B7),
    outline = Color(0xFF3A3832),
    outlineVariant = Color(0xFF2D2B26),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

@Composable
fun AshTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) AshDarkColors else AshLightColors,
        content = content
    )
}
