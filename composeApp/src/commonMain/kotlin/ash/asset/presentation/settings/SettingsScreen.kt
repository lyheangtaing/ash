package ash.asset.presentation.settings

import ash.core.designsystem.AshPanel
import ash.core.designsystem.AshSpacing
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier,
        contentPadding = PaddingValues(AshSpacing.screen),
        verticalArrangement = Arrangement.spacedBy(AshSpacing.lg)
    ) {
        item { Text("Collection settings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }
        item {
            AshPanel(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(AshSpacing.sm)) {
                    Icon(Icons.Default.Lock, contentDescription = null)
                    Text("Private by design", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        "Your collection records stay on this device. Ash does not use analytics, ads, accounts, or automatic suggestions.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        item {
            AshPanel(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(AshSpacing.sm)) {
                    Text("Currency", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("USD · US Dollar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
