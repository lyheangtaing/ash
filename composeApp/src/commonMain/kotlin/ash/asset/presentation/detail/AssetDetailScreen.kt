package ash.asset.presentation.detail

import ash.asset.domain.model.Asset
import ash.asset.domain.model.AssetDecisionResult
import ash.asset.presentation.components.EmptyState
import ash.asset.presentation.components.LabeledValue
import ash.asset.presentation.image.AssetImagePreview
import ash.core.designsystem.AshPanel
import ash.core.designsystem.AshRadius
import ash.core.designsystem.AshSpacing
import ash.core.util.displayText
import ash.core.util.formatMoney
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun AssetDetailScreen(
    asset: Asset?,
    suggestion: AssetDecisionResult?,
    onEdit: (String) -> Unit,
    onMaintenance: (String) -> Unit,
    onCreateReminder: (String) -> Unit,
    onRequestSuggestion: (String) -> Unit,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (asset == null) {
        Column(modifier.padding(AshSpacing.screen)) {
            EmptyState("Asset not found", "Return to Collection and choose another asset.")
        }
        return
    }
    var showDeleteConfirmation by remember(asset.id) { mutableStateOf(false) }
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete asset?") },
            text = { Text("This permanently removes ${asset.name}, its photos, history, and reminders.") },
            confirmButton = {
                TextButton(onClick = { showDeleteConfirmation = false; onDelete(asset.id) }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { showDeleteConfirmation = false }) { Text("Cancel") } }
        )
    }

    LazyColumn(
        modifier,
        contentPadding = PaddingValues(bottom = AshSpacing.bottomNavPadding),
        verticalArrangement = Arrangement.spacedBy(AshSpacing.lg)
    ) {
        item { AssetPhotoHeader(asset) }
        item {
            Column(
                modifier = Modifier.padding(horizontal = AshSpacing.screen),
                verticalArrangement = Arrangement.spacedBy(AshSpacing.lg)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(asset.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text(
                        listOf(asset.category.label, asset.brand, asset.model).filter(String::isNotBlank).joinToString(" · "),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(AshSpacing.sm)) {
                    ValuePanel("Purchase value", formatMoney(asset.purchasePrice), Modifier.weight(1f))
                    ValuePanel("Estimated current", formatMoney(asset.currentEstimatedValue), Modifier.weight(1f))
                }
                if (asset.willingToSell) {
                    AshPanel(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AshSpacing.md)) {
                            Icon(Icons.Default.Sell, null)
                            Column(Modifier.weight(1f)) {
                                Text("Open to selling", fontWeight = FontWeight.SemiBold)
                                Text(
                                    asset.desiredSellingPrice?.let { "Desired selling price ${formatMoney(it)}" }
                                        ?: "No desired selling price recorded",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                AssetActions(asset.id, onEdit, onMaintenance, onCreateReminder)
            }
        }

        if (asset.specialDetails.isNotBlank() || asset.serialNumber != null || asset.tags.isNotEmpty()) {
            item {
                DetailSection("Identity and edition") {
                    asset.serialNumber?.let { LabeledValue("Serial or identifier", it) }
                    if (asset.specialDetails.isNotBlank()) LabeledValue("Special details", asset.specialDetails)
                    if (asset.tags.isNotEmpty()) LabeledValue("Tags", asset.tags.joinToString(" · "))
                }
            }
        }

        item {
            DetailSection("Repair and modification history") {
                if (asset.maintenanceRecords.isEmpty()) {
                    Text("No history recorded yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedButton(onClick = { onMaintenance(asset.id) }) { Text("Add first event") }
                } else {
                    asset.maintenanceRecords.sortedByDescending { it.date }.forEachIndexed { index, record ->
                        Row(horizontalArrangement = Arrangement.spacedBy(AshSpacing.md)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(Modifier.size(9.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
                                if (index != asset.maintenanceRecords.lastIndex) {
                                    Box(Modifier.size(width = 1.dp, height = 82.dp).background(MaterialTheme.colorScheme.outline))
                                }
                            }
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(record.type, fontWeight = FontWeight.SemiBold)
                                Text(
                                    "${record.eventType.label} · ${record.date}" +
                                        if (record.cost > 0) " · ${formatMoney(record.cost)}" else "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (record.notes.isNotBlank()) Text(record.notes, style = MaterialTheme.typography.bodyMedium)
                                record.imageUris.firstOrNull()?.let {
                                    AssetImagePreview(it, "${record.eventType.label} photo", Modifier.size(72.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        if (asset.reminders.isNotEmpty()) {
            item {
                DetailSection("Reminders") {
                    asset.reminders.filterNot { it.isCompleted }.sortedBy { it.dueDate }.take(3).forEach { reminder ->
                        Row(horizontalArrangement = Arrangement.spacedBy(AshSpacing.sm), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, null, Modifier.size(18.dp))
                            Column {
                                Text(reminder.action, fontWeight = FontWeight.Medium)
                                Text(
                                    reminder.dueDate.toString() + if (reminder.time.isNotBlank()) " at ${reminder.time}" else "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    OutlinedButton(onClick = { onCreateReminder(asset.id) }) { Text("Create reminder") }
                }
            }
        }

        item {
            DetailSection("Notes and record") {
                if (asset.notes.isNotBlank()) LabeledValue("Notes", asset.notes)
                LabeledValue("Purchase date", asset.purchaseDate.displayText())
                LabeledValue("Condition", asset.condition.label)
                LabeledValue("Ownership", asset.ownershipStatus.label)
                LabeledValue("Warranty end", asset.warrantyEndDate.displayText())
                LabeledValue("Last updated", asset.updatedAt.toString())
            }
        }

        item {
            Column(
                modifier = Modifier.padding(horizontal = AshSpacing.screen),
                verticalArrangement = Arrangement.spacedBy(AshSpacing.md)
            ) {
                Text("Optional guidance", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    "Ask Ash when you want a repair, replace, or sell perspective. Nothing is generated automatically.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedButton(onClick = { onRequestSuggestion(asset.id) }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Lightbulb, null)
                    Text(if (suggestion == null) "  Request suggestion" else "  Refresh suggestion")
                }
                AnimatedVisibility(suggestion != null, enter = fadeIn() + expandVertically()) {
                    suggestion?.let { SuggestionPanel(asset, it) }
                }
                TextButton(onClick = { showDeleteConfirmation = true }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                    Text("  Delete asset", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun AssetPhotoHeader(asset: Asset) {
    Column(verticalArrangement = Arrangement.spacedBy(AshSpacing.sm)) {
        Box(
            Modifier.fillMaxWidth().aspectRatio(1.25f).background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            asset.imageUris.firstOrNull()?.let {
                AssetImagePreview(it, "Primary photo of ${asset.name}", Modifier.fillMaxSize())
            } ?: Icon(Icons.Default.AddPhotoAlternate, "No photo for ${asset.name}", Modifier.size(40.dp))
        }
        if (asset.imageUris.size > 1) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = AshSpacing.screen),
                horizontalArrangement = Arrangement.spacedBy(AshSpacing.sm)
            ) {
                items(asset.imageUris.drop(1)) { uri ->
                    Surface(shape = RoundedCornerShape(AshRadius.md)) {
                        AssetImagePreview(uri, "Additional photo of ${asset.name}", Modifier.size(72.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ValuePanel(label: String, value: String, modifier: Modifier = Modifier) {
    AshPanel(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AssetActions(
    assetId: String,
    onEdit: (String) -> Unit,
    onMaintenance: (String) -> Unit,
    onCreateReminder: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(AshSpacing.sm)) {
        Row(horizontalArrangement = Arrangement.spacedBy(AshSpacing.sm)) {
            Button(onClick = { onEdit(assetId) }, modifier = Modifier.weight(1f).height(50.dp)) {
                Icon(Icons.Default.Edit, "Edit asset")
                Text("  Edit")
            }
            OutlinedButton(onClick = { onMaintenance(assetId) }, modifier = Modifier.weight(1f).height(50.dp)) {
                Icon(Icons.Default.Build, "Add repair or modification")
                Text("  History")
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(AshSpacing.sm)) {
            OutlinedButton(onClick = { onCreateReminder(assetId) }, modifier = Modifier.weight(1f).height(48.dp)) {
                Icon(Icons.Default.Notifications, "Create reminder")
                Text("  Reminder")
            }
            OutlinedButton(onClick = { onEdit(assetId) }, modifier = Modifier.weight(1f).height(48.dp)) {
                Icon(Icons.Default.Sell, "Update sale preference")
                Text("  Sale")
            }
        }
    }
}

@Composable
private fun DetailSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.padding(horizontal = AshSpacing.screen),
        verticalArrangement = Arrangement.spacedBy(AshSpacing.sm)
    ) {
        Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        AshPanel(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(AshSpacing.md)) { content() }
        }
    }
}

@Composable
private fun SuggestionPanel(asset: Asset, suggestion: AssetDecisionResult) {
    AshPanel(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(AshSpacing.sm)) {
            Text(suggestion.decision.label, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            suggestion.reasons.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
            Text("Information used", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                "Condition ${asset.condition.label}; purchase value ${formatMoney(asset.purchasePrice)}; " +
                    "estimated current value ${formatMoney(asset.currentEstimatedValue)}; " +
                    "recorded repair and service cost ${formatMoney(asset.totalMaintenanceCost)}.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "Guidance only. Market demand, repair quotes, and sentimental value are not known to Ash.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
