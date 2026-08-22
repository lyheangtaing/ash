package ash.asset.presentation.components

import ash.asset.domain.model.Asset
import ash.asset.domain.model.AssetCondition
import ash.asset.domain.model.AssetDecision
import ash.asset.domain.model.AssetDecisionResult
import ash.asset.domain.model.OwnershipStatus
import ash.asset.presentation.image.AssetImagePreview
import ash.core.designsystem.AshPanel
import ash.core.designsystem.AshRadius
import ash.core.designsystem.AshStatusDot
import ash.core.designsystem.AshStatusPalette
import ash.core.util.displayText
import ash.core.util.formatMoney
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon

@Composable
fun PremiumSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search assets, tags, brands",
    onClear: () -> Unit = { onValueChange("") }
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 58.dp)
            .semantics { contentDescription = "Asset search" },
        singleLine = true,
        shape = RoundedCornerShape(AshRadius.lg),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            if (value.isNotBlank()) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Close, contentDescription = "Clear search")
                }
            }
        },
        placeholder = { Text(placeholder) }
    )
}

@Composable
fun AshFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(AshRadius.pill)
    val background by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        label = "FilterChipBackground"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        label = "FilterChipContent"
    )
    Surface(
        modifier = modifier
            .heightIn(min = 44.dp)
            .clip(shape)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.Tab
            ),
        shape = shape,
        color = background,
        border = if (selected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}

@Composable
fun AssetStatusBadge(
    asset: Asset,
    decision: AssetDecisionResult,
    modifier: Modifier = Modifier,
    onDark: Boolean = false
) {
    val status = asset.operationalStatus(decision)
    val badgeColor = if (onDark) Color.White else status.color
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(AshRadius.pill),
        color = badgeColor.copy(alpha = if (status.isCritical) 0.16f else 0.11f),
        border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.24f))
    ) {
        Text(
            text = status.label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = badgeColor,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
fun AssetOperationalCard(
    asset: Asset,
    decision: AssetDecisionResult,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val metadata = buildString {
        append("#${asset.lookupCode()} · ${asset.category.label}")
        if (asset.imageUris.isNotEmpty()) {
            append(" · ${asset.imageUris.size} photos")
        }
    }

    AshPanel(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        contentPadding = PaddingValues(16.dp),
        elevated = false
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                asset.imageUris.firstOrNull()?.let { uri ->
                    AssetThumbnail(
                        uri = uri,
                        contentDescription = "${asset.name} image",
                        modifier = Modifier.size(58.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = asset.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = metadata,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                AssetStatusBadge(asset = asset, decision = decision)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                CompactMeta(label = "Condition", value = asset.condition.label)
                CompactMeta(label = "Updated", value = asset.updatedAt.toString())
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Value",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatMoney(asset.currentEstimatedValue),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun SectionTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        modifier = modifier,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
fun LabeledValue(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun EmptyState(
    title: String,
    body: String,
    modifier: Modifier = Modifier
) {
    AshPanel(modifier = modifier.fillMaxWidth(), contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AssetListItem(
    asset: Asset,
    decision: AssetDecisionResult,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AshPanel(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        contentPadding = PaddingValues(14.dp),
        elevated = false
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            asset.imageUris.firstOrNull()?.let { uri ->
                AssetThumbnail(
                    uri = uri,
                    contentDescription = "${asset.name} image",
                    modifier = Modifier.size(50.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = asset.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = "${asset.category.label} · ${asset.condition.label} · ${asset.ownershipStatus.label}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                AnimatedVisibility(
                    visible = decision.maintenanceOverdue || decision.warrantyNearExpiry,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                        Spacer(Modifier.height(8.dp))
                        StatusRow(decision)
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatMoney(asset.currentEstimatedValue),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                DecisionBadge(decision.decision)
            }
        }
    }
}

@Composable
fun AssetCompactRow(
    asset: Asset,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        asset.imageUris.firstOrNull()?.let { uri ->
            AssetThumbnail(
                uri = uri,
                contentDescription = "${asset.name} image",
                modifier = Modifier.size(44.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = asset.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${asset.category.label} · ${asset.purchaseDate.displayText()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = formatMoney(asset.currentEstimatedValue),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun StatusRow(
    decision: AssetDecisionResult,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatusDotLabel("Maintenance", decision.maintenanceOverdue)
        StatusDotLabel("Warranty", decision.warrantyNearExpiry)
    }
}

@Composable
fun DecisionBadge(
    decision: AssetDecision,
    modifier: Modifier = Modifier
) {
    val isPrimary = decision == AssetDecision.KEEP
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = if (isPrimary) Color.Black else MaterialTheme.colorScheme.surfaceVariant,
        border = if (isPrimary) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Box(
            modifier = Modifier
                .height(28.dp)
                .width(74.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = decision.label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = if (isPrimary) Color.White else Color.Black.copy(alpha = 0.72f),
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

fun Asset.lookupCode(): String {
    return serialNumber
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?.takeLast(10)
        ?: id.substringAfterLast("-").takeLast(8).uppercase()
}

private data class OperationalStatus(
    val label: String,
    val color: Color,
    val isCritical: Boolean = false
)

private fun Asset.operationalStatus(decision: AssetDecisionResult): OperationalStatus {
    return when {
        ownershipStatus == OwnershipStatus.LOST -> OperationalStatus("Lost", AshStatusPalette.lost, true)
        ownershipStatus == OwnershipStatus.SOLD || ownershipStatus == OwnershipStatus.REPLACED ->
            OperationalStatus("Retired", AshStatusPalette.retired)
        condition == AssetCondition.BROKEN || condition == AssetCondition.POOR ->
            OperationalStatus("Damaged", AshStatusPalette.damaged, true)
        decision.maintenanceOverdue -> OperationalStatus("Maintenance", AshStatusPalette.maintenance, true)
        isInUse -> OperationalStatus("Assigned", AshStatusPalette.assigned)
        else -> OperationalStatus("Available", AshStatusPalette.available)
    }
}

@Composable
private fun AssetThumbnail(
    uri: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        AssetImagePreview(
            uri = uri,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun CompactMeta(
    label: String,
    value: String
) {
    Column(modifier = Modifier.widthIn(max = 104.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun <T> AshDropdown(
    label: String,
    selected: T,
    options: List<T>,
    optionLabel: (T) -> String,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownButton(
        label = label,
        selectedLabel = optionLabel(selected),
        optionContent = { closeMenu ->
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabel(option)) },
                    onClick = {
                        onSelected(option)
                        closeMenu()
                    }
                )
            }
        },
        modifier = modifier
    )
}

@Composable
fun <T> AshNullableDropdown(
    label: String,
    selected: T?,
    options: List<T>,
    optionLabel: (T?) -> String,
    onSelected: (T?) -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownButton(
        label = label,
        selectedLabel = optionLabel(selected),
        optionContent = { closeMenu ->
            DropdownMenuItem(
                text = { Text(optionLabel(null)) },
                onClick = {
                    onSelected(null)
                    closeMenu()
                }
            )
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabel(option)) },
                    onClick = {
                        onSelected(option)
                        closeMenu()
                    }
                )
            }
        },
        modifier = modifier
    )
}

@Composable
private fun DropdownButton(
    label: String,
    selectedLabel: String,
    optionContent: @Composable (() -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            onClick = { expanded = true }
        ) {
            Text(
                text = "$label: $selectedLabel",
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            optionContent { expanded = false }
        }
    }
}

@Composable
private fun StatusDotLabel(
    label: String,
    isActive: Boolean
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AshStatusDot(
            isActive = isActive,
            modifier = Modifier.size(7.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
