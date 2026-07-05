package ash.core.navigation

import androidx.compose.runtime.Composable

@Composable
expect fun AshBackHandler(
    enabled: Boolean,
    onBack: () -> Unit
)
