package ash.asset.presentation.activity

import ash.asset.domain.model.Asset
import ash.asset.domain.model.MaintenanceRecord
import ash.asset.presentation.image.AssetImagePreview
import ash.core.designsystem.AshPanel
import ash.core.designsystem.AshRadius
import ash.core.designsystem.AshSpacing
import ash.core.util.formatMoney
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

private data class ActivityItem(val asset: Asset, val record: MaintenanceRecord)

@Composable
fun ActivityScreen(
    assets: List<Asset>,
    onAssetSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val activity = assets.flatMap { asset -> asset.maintenanceRecords.map { ActivityItem(asset, it) } }
        .sortedByDescending { it.record.date }
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(AshSpacing.screen, AshSpacing.md, AshSpacing.screen, AshSpacing.bottomNavPadding),
        verticalArrangement = Arrangement.spacedBy(AshSpacing.md)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Activity", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                Text("Repairs, modifications, and service history", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        if (activity.isEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AshSpacing.md)
                ) {
                    Icon(Icons.Default.Build, null, Modifier.size(32.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("No activity yet", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Text("Repair and modification events will form a timeline here.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(activity, key = { "${it.asset.id}-${it.record.id}" }) { item ->
                Row(horizontalArrangement = Arrangement.spacedBy(AshSpacing.md)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(Modifier.size(10.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
                        Box(Modifier.size(width = 1.dp, height = 116.dp).background(MaterialTheme.colorScheme.outline))
                    }
                    AshPanel(
                        modifier = Modifier.weight(1f).clickable { onAssetSelected(item.asset.id) }
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                item.record.imageUris.firstOrNull()?.let {
                                    AssetImagePreview(it, "${item.record.eventType.label} photo", Modifier.size(44.dp))
                                }
                                Column(Modifier.weight(1f)) {
                                    Text(item.record.type, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(item.asset.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Text(
                                "${item.record.eventType.label} · ${item.record.date}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (item.record.cost > 0.0) Text("Cost ${formatMoney(item.record.cost)}", fontWeight = FontWeight.Medium)
                            if (item.record.notes.isNotBlank()) Text(item.record.notes, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
