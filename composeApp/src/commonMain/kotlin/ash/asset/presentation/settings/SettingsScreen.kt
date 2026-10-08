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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    var showPrivacyPolicy by rememberSaveable { mutableStateOf(false) }
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
                        "Ash does not send your collection to the developer or third parties. There are no analytics, ads, or accounts. Read the privacy policy for photo storage, backups, and deletion details.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(onClick = { showPrivacyPolicy = !showPrivacyPolicy }) {
                        Text(if (showPrivacyPolicy) "Hide privacy policy" else "Read privacy policy")
                    }
                }
            }
        }
        if (showPrivacyPolicy) {
            items(PrivacyPolicy.sections.size) { index ->
                val section = PrivacyPolicy.sections[index]
                AshPanel(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(AshSpacing.sm)) {
                        Text(section.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(section.text, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
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
