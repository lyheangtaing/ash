package ash.asset.presentation.detail

import ash.asset.domain.model.Asset
import ash.asset.domain.model.AssetDecisionResult
import ash.asset.presentation.components.AssetStatusBadge
import ash.asset.presentation.components.EmptyState
import ash.asset.presentation.components.LabeledValue
import ash.asset.presentation.components.SectionTitle
import ash.asset.presentation.components.StatusRow
import ash.asset.presentation.components.lookupCode
import ash.asset.presentation.image.AssetImagePreview
import ash.core.designsystem.AshHeroPanel
import ash.core.designsystem.AshPanel
import ash.core.designsystem.AshRadius
import ash.core.designsystem.AshSpacing
import ash.core.util.displayText
import ash.core.util.formatMoney
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AssetDetailScreen(
    asset: Asset?,
    decision: AssetDecisionResult?,
    onEdit: (String) -> Unit,
    onMaintenance: (String) -> Unit,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (asset == null || decision == null) {
        Column(modifier = modifier.padding(16.dp)) {
            EmptyState(
                title = "Asset not found",
                body = "Return to the list and choose another asset."
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = AshSpacing.screen,
            top = 14.dp,
            end = AshSpacing.screen,
            bottom = AshSpacing.bottomNavPadding
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            AshHeroPanel(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = asset.name,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "#${asset.lookupCode()} · ${asset.category.label}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.68f)
                            )
                        }
                        AssetStatusBadge(asset = asset, decision = decision, onDark = true)
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Current value",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.64f)
                        )
                        Text(
                            text = formatMoney(asset.currentEstimatedValue),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (asset.imageUris.isNotEmpty()) {
            item {
                SectionTitle("Images")
                AssetDetailImageGallery(imageUris = asset.imageUris)
            }
        }

        item {
            SectionTitle("Profile")
            AshPanel(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    LabeledValue("Tag", "#${asset.lookupCode()}")
                    LabeledValue("Images", asset.imageUris.size.toString())
                    LabeledValue("Condition", asset.condition.label)
                    LabeledValue("Assignment", if (asset.isInUse) "In regular use" else "Unassigned")
                    LabeledValue("Location", "Not recorded")
                    LabeledValue("Last updated", asset.updatedAt.toString())
                }
            }
        }

        item {
            AshPanel(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(14.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatusRow(decision)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            onClick = { onMaintenance(asset.id) }
                        ) {
                            Icon(Icons.Default.Build, contentDescription = null)
                        }
                        OutlinedButton(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            onClick = { onEdit(asset.id) }
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                        }
                        OutlinedButton(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            onClick = { onDelete(asset.id) }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete asset")
                        }
                    }
                }
            }
        }

        item {
            SectionTitle("Value")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AshPanel(modifier = Modifier.weight(1f)) {
                    LabeledValue("Purchase", formatMoney(asset.purchasePrice))
                }
                AshPanel(modifier = Modifier.weight(1f)) {
                    LabeledValue("Maint.", formatMoney(asset.totalMaintenanceCost))
                }
            }
        }

        item {
            SectionTitle("Details")
            AshPanel(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    LabeledValue("Brand", asset.brand.ifBlank { "Not set" })
                    LabeledValue("Model", asset.model.ifBlank { "Not set" })
                    LabeledValue("Serial number", asset.serialNumber ?: "Not set")
                    LabeledValue("Purchase date", asset.purchaseDate.displayText())
                    LabeledValue("Warranty end", asset.warrantyEndDate.displayText())
                    LabeledValue("Ownership", asset.ownershipStatus.label)
                }
            }
        }

        item {
            SectionTitle("Recommendation")
            AshPanel(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    decision.reasons.forEach { reason ->
                        Text(
                            text = reason,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        item {
            SectionTitle("Notes")
            AshPanel(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = asset.notes.ifBlank { "No notes" },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        item {
            SectionTitle("Maintenance")
        }

        if (asset.maintenanceRecords.isEmpty()) {
            item {
                EmptyState(
                    title = "No maintenance records",
                    body = "Log work, cleaning, replacement, or service history for this asset."
                )
            }
        } else {
            items(asset.maintenanceRecords, key = { it.id }) { record ->
                AshPanel(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = record.type,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${record.date} · ${formatMoney(record.cost)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (record.notes.isNotBlank()) {
                            Text(record.notes, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AssetDetailImageGallery(
    imageUris: List<String>
) {
    AshPanel(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(0.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                AssetImagePreview(
                    uri = imageUris.first(),
                    contentDescription = "Primary asset image",
                    modifier = Modifier.fillMaxSize()
                )
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    shape = RoundedCornerShape(AshRadius.pill),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.90f)
                ) {
                    Text(
                        text = "${imageUris.size} photos",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (imageUris.size > 1) {
                LazyRow(
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(imageUris, key = { index, uri -> "$index-$uri" }) { index, uri ->
                        Surface(
                            modifier = Modifier.size(72.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                AssetImagePreview(
                                    uri = uri,
                                    contentDescription = "Asset image ${index + 1}",
                                    modifier = Modifier.fillMaxSize()
                                )
                                Surface(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(6.dp),
                                    shape = RoundedCornerShape(AshRadius.pill),
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.86f)
                                ) {
                                    Text(
                                        text = (index + 1).toString(),
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
