package ash.core.navigation

import androidx.compose.runtime.Composable

@Composable
actual fun AshBackHandler(
    enabled: Boolean,
    onBack: () -> Unit
) {
    // iOS navigation gestures are host-driven; Android handles system back here.
}
