package ash.asset.presentation.list

import ash.asset.domain.model.Asset
import ash.asset.domain.model.AssetCategory
import ash.asset.domain.model.AssetCondition
import ash.asset.domain.model.AssetSummary
import ash.asset.domain.model.OwnershipStatus
import ash.asset.presentation.AssetSortOrder
import ash.asset.presentation.AssetUiState
import ash.asset.presentation.components.AshDropdown
import ash.asset.presentation.components.AshFilterChip
import ash.asset.presentation.components.PremiumSearchBar
import ash.asset.presentation.image.AssetImagePreview
import ash.core.designsystem.AshPanel
import ash.core.designsystem.AshRadius
import ash.core.designsystem.AshSpacing
import ash.core.util.DateProvider
import ash.core.util.formatMoney
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AssetListScreen(
    state: AssetUiState,
    onSearchChanged: (String) -> Unit,
    onCategoryChanged: (AssetCategory?) -> Unit,
    onTagChanged: (String?) -> Unit,
    onSortChanged: (AssetSortOrder) -> Unit,
    onClearFilters: () -> Unit,
    onAssetSelected: (String) -> Unit,
    onAddAsset: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tags = state.assets.flatMap(Asset::tags).distinct().sorted()
    val dueReminders = state.assets.flatMap { asset ->
        asset.reminders
            .filter { !it.isCompleted && it.dueDate <= DateProvider.today() }
            .map { asset to it }
    }.sortedBy { it.second.dueDate }
    val isFiltered = state.searchQuery.isNotBlank() || state.categoryFilter != null || state.tagFilter != null

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 164.dp),
        modifier = modifier,
        contentPadding = PaddingValues(
            start = AshSpacing.screen,
            top = AshSpacing.md,
            end = AshSpacing.screen,
            bottom = AshSpacing.bottomNavPadding
        ),
        horizontalArrangement = Arrangement.spacedBy(AshSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AshSpacing.md)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Collection", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                    Text(
                        "Your private archive",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onOpenSettings) {
                    Icon(Icons.Default.Settings, contentDescription = "Collection settings")
                }
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(horizontalArrangement = Arrangement.spacedBy(AshSpacing.sm)) {
                CollectionStat("Assets", state.summary.totalAssetCount.toString(), Modifier.weight(0.72f))
                CollectionStat(
                    "Recorded purchase value",
                    formatMoney(state.summary.totalPurchaseValue),
                    Modifier.weight(1.28f)
                )
            }
        }

        if (dueReminders.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                AshPanel(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(AshSpacing.md),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(AshRadius.sm),
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.10f)
                        ) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(10.dp).size(20.dp)
                            )
                        }
                        Column(Modifier.weight(1f)) {
                            Text(
                                if (dueReminders.size == 1) "1 reminder due" else "${dueReminders.size} reminders due",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                dueReminders.first().first.name + ": " + dueReminders.first().second.action,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            PremiumSearchBar(
                value = state.searchQuery,
                onValueChange = onSearchChanged,
                placeholder = "Search names, tags, details"
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(verticalArrangement = Arrangement.spacedBy(AshSpacing.sm)) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(AshSpacing.sm)) {
                    item {
                        AshFilterChip("All", state.categoryFilter == null, onClick = { onCategoryChanged(null) })
                    }
                    items(AssetCategory.entries) { category ->
                        AshFilterChip(category.label, state.categoryFilter == category, onClick = { onCategoryChanged(category) })
                    }
                }
                if (tags.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(AshSpacing.sm)) {
                        item { AshFilterChip("All tags", state.tagFilter == null, onClick = { onTagChanged(null) }) }
                        items(tags) { tag ->
                            AshFilterChip(tag, state.tagFilter == tag, onClick = { onTagChanged(tag) })
                        }
                    }
                }
                AshDropdown(
                    label = "Sort",
                    selected = state.sortOrder,
                    options = AssetSortOrder.entries,
                    optionLabel = AssetSortOrder::label,
                    onSelected = onSortChanged,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (state.assets.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                CollectionEmptyState(onAddAsset)
            }
        } else if (state.visibleAssets.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                AshPanel(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(AshSpacing.sm)) {
                        Text("No matches", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(
                            "Try another search or clear the active filters.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (isFiltered) Button(onClick = onClearFilters) { Text("Clear filters") }
                    }
                }
            }
        } else {
            items(state.visibleAssets, key = Asset::id) { asset ->
                CollectionAssetCard(asset = asset, onClick = { onAssetSelected(asset.id) })
            }
        }
    }
}

@Composable
private fun CollectionStat(label: String, value: String, modifier: Modifier = Modifier) {
    AshPanel(modifier = modifier.heightIn(min = 84.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            AnimatedContent(value, transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "CollectionStat") {
                Text(it, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, maxLines = 1)
            }
        }
    }
}

@Composable
private fun CollectionAssetCard(asset: Asset, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(AshRadius.md)).clickable(onClick = onClick),
        shape = RoundedCornerShape(AshRadius.md),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shadowElevation = 2.dp
    ) {
        Column {
            Box(
                modifier = Modifier.fillMaxWidth().aspectRatio(1.12f)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                val image = asset.imageUris.firstOrNull()
                if (image == null) {
                    Icon(
                        Icons.Default.AddPhotoAlternate,
                        contentDescription = "No photo for ${asset.name}",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(30.dp)
                    )
                } else {
                    AssetImagePreview(image, "Photo of ${asset.name}", Modifier.fillMaxSize())
                }
            }
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    asset.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "Purchase value",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(formatMoney(asset.purchasePrice), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                val status = when {
                    asset.ownershipStatus != ash.asset.domain.model.OwnershipStatus.OWNED -> asset.ownershipStatus.label
                    asset.willingToSell -> "Open to offers"
                    asset.tags.isNotEmpty() -> asset.tags.take(2).joinToString(" · ")
                    else -> asset.category.label
                }
                Text(
                    status,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun CollectionEmptyState(onAddAsset: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AshSpacing.md)
    ) {
        Surface(shape = RoundedCornerShape(AshRadius.md), color = MaterialTheme.colorScheme.surfaceVariant) {
            Icon(
                Icons.Default.AddPhotoAlternate,
                contentDescription = null,
                modifier = Modifier.padding(18.dp).size(30.dp)
            )
        }
        Text("Start your collection", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        Text(
            "Add a photo, name, and purchase value. You can fill in the rest later.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Button(onClick = onAddAsset) { Text("Add first asset") }
    }
}

@Preview
@Composable
private fun PopulatedCollectionPreview() {
    val asset = Asset(
        id = "preview-camera",
        name = "Limited camera",
        category = AssetCategory.ELECTRONICS,
        brand = "Leica",
        model = "Q3",
        serialNumber = null,
        purchaseDate = LocalDate(2025, 4, 12),
        purchasePrice = 6200.0,
        currentEstimatedValue = 5900.0,
        condition = AssetCondition.EXCELLENT,
        ownershipStatus = OwnershipStatus.OWNED,
        warrantyEndDate = null,
        notes = "",
        maintenanceRecords = emptyList(),
        isInUse = true,
        createdAt = LocalDate(2025, 4, 12),
        updatedAt = LocalDate(2025, 4, 12),
        tags = listOf("limited", "camera")
    )
    AssetListScreen(
        state = AssetUiState(
            assets = listOf(asset),
            visibleAssets = listOf(asset),
            summary = AssetSummary.Empty.copy(totalAssetCount = 1, totalPurchaseValue = 6200.0)
        ),
        onSearchChanged = {},
        onCategoryChanged = {},
        onTagChanged = {},
        onSortChanged = {},
        onClearFilters = {},
        onAssetSelected = {},
        onAddAsset = {},
        onOpenSettings = {}
    )
}

@Preview
@Composable
private fun EmptyCollectionPreview() {
    AssetListScreen(
        state = AssetUiState(),
        onSearchChanged = {},
        onCategoryChanged = {},
        onTagChanged = {},
        onSortChanged = {},
        onClearFilters = {},
        onAssetSelected = {},
        onAddAsset = {},
        onOpenSettings = {}
    )
}
