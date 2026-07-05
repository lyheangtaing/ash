package ash.asset.presentation.list

import ash.asset.domain.model.Asset
import ash.asset.domain.model.AssetCategory
import ash.asset.domain.model.AssetCondition
import ash.asset.domain.model.AssetDecisionResult
import ash.asset.domain.model.OwnershipStatus
import ash.asset.presentation.AssetUiState
import ash.asset.presentation.components.AshFilterChip
import ash.asset.presentation.components.AssetOperationalCard
import ash.asset.presentation.components.EmptyState
import ash.asset.presentation.components.PremiumSearchBar
import ash.core.designsystem.AshSpacing
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AssetListScreen(
    state: AssetUiState,
    decisionFor: (Asset) -> AssetDecisionResult,
    onSearchChanged: (String) -> Unit,
    onCategoryChanged: (AssetCategory?) -> Unit,
    onConditionChanged: (AssetCondition?) -> Unit,
    onOwnershipChanged: (OwnershipStatus?) -> Unit,
    onClearFilters: () -> Unit,
    onAssetSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val hasActiveFilters = state.categoryFilter != null ||
        state.conditionFilter != null ||
        state.ownershipFilter != OwnershipStatus.OWNED
    var filtersExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(hasActiveFilters) {
        if (hasActiveFilters) filtersExpanded = true
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = AshSpacing.screen,
            top = 14.dp,
            end = AshSpacing.screen,
            bottom = AshSpacing.bottomNavPadding
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Assets",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${state.visibleAssets.size} assets ready to verify",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                PremiumSearchBar(
                    value = state.searchQuery,
                    onValueChange = onSearchChanged,
                    placeholder = "Search inventory"
                )
            }
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(end = 4.dp)
            ) {
                item {
                    AshFilterChip(
                        label = "All",
                        selected = state.categoryFilter == null,
                        onClick = { onCategoryChanged(null) }
                    )
                }
                items(AssetCategory.entries) { category ->
                    AshFilterChip(
                        label = category.label,
                        selected = state.categoryFilter == category,
                        onClick = { onCategoryChanged(category) }
                    )
                }
            }
        }

        item {
            Column(
                modifier = Modifier.animateContentSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        onClick = { filtersExpanded = !filtersExpanded }
                    ) {
                        Text(if (hasActiveFilters) "More filters active" else "More filters")
                    }
                    if (hasActiveFilters) {
                        TextButton(
                            modifier = Modifier.weight(1f),
                            onClick = onClearFilters
                        ) {
                            Text("Reset")
                        }
                    }
                }
                AnimatedVisibility(
                    visible = filtersExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        ChipRow(
                            title = "Status",
                            selected = state.ownershipFilter,
                            values = OwnershipStatus.entries,
                            label = { it.label },
                            onSelected = onOwnershipChanged
                        )
                        ChipRow(
                            title = "Condition",
                            selected = state.conditionFilter,
                            values = AssetCondition.entries,
                            label = { it.label },
                            onSelected = onConditionChanged
                        )
                    }
                }
            }
        }

        if (state.visibleAssets.isEmpty()) {
            item {
                EmptyState(
                    title = "No matching assets",
                    body = "Try a different search, category, status, or condition."
                )
            }
        } else {
            items(state.visibleAssets, key = { it.id }) { asset ->
                AssetOperationalCard(
                    asset = asset,
                    decision = decisionFor(asset),
                    onClick = { onAssetSelected(asset.id) }
                )
            }
        }
    }
}

@Composable
private fun <T> ChipRow(
    title: String,
    selected: T?,
    values: List<T>,
    label: (T) -> String,
    onSelected: (T?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                AshFilterChip(
                    label = "All",
                    selected = selected == null,
                    onClick = { onSelected(null) }
                )
            }
            items(values) { value ->
                AshFilterChip(
                    label = label(value),
                    selected = selected == value,
                    onClick = { onSelected(value) }
                )
            }
        }
    }
}
