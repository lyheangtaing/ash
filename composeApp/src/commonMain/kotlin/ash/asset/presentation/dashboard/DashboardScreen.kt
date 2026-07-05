package ash.asset.presentation.dashboard

import ash.asset.domain.model.Asset
import ash.asset.domain.model.AssetDecisionResult
import ash.asset.presentation.AssetUiState
import ash.asset.presentation.components.AssetCompactRow
import ash.asset.presentation.components.AssetOperationalCard
import ash.asset.presentation.components.EmptyState
import ash.asset.presentation.components.PremiumSearchBar
import ash.asset.presentation.components.SectionTitle
import ash.core.designsystem.AshPanel
import ash.core.designsystem.AshSpacing
import ash.core.designsystem.EqualWeightPanel
import ash.core.util.formatMoney
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(
    state: AssetUiState,
    decisionFor: (Asset) -> AssetDecisionResult,
    onSearchChanged: (String) -> Unit,
    onAssetSelected: (String) -> Unit,
    onAddAsset: () -> Unit,
    onOpenAssets: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSearching = state.searchQuery.isNotBlank()
    val lookupResults = state.visibleAssets.take(6)

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = AshSpacing.screen,
            top = 14.dp,
            end = AshSpacing.screen,
            bottom = AshSpacing.bottomNavPadding
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Lookup",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Find, verify, and act on assets fast.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                PremiumSearchBar(
                    value = state.searchQuery,
                    onValueChange = onSearchChanged,
                    placeholder = "Search name, brand, model, serial"
                )
            }
        }

        if (isSearching) {
            item {
                SectionTitle("${lookupResults.size} matched")
            }
            if (lookupResults.isEmpty()) {
                item {
                    EmptyState(
                        title = "No asset found",
                        body = "Try a name, category, brand, model, or serial number."
                    )
                }
            } else {
                items(lookupResults.size, key = { lookupResults[it].id }) { index ->
                    val asset = lookupResults[index]
                    AssetOperationalCard(
                        asset = asset,
                        decision = decisionFor(asset),
                        onClick = { onAssetSelected(asset.id) }
                    )
                }
            }
            item {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onOpenAssets
                ) {
                    Text("Open full results")
                }
            }
        } else {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = onAddAsset
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Add asset")
                    }
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = onOpenAssets
                    ) {
                        Text("Browse")
                    }
                }
            }

            item {
                SectionTitle("Today")
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        EqualWeightPanel { StatBlock("Tracked", state.summary.totalAssetCount.toString()) }
                        EqualWeightPanel { StatBlock("Needs care", state.summary.assetsNeedingMaintenance.toString()) }
                    }
                    AshPanel(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(18.dp), elevated = true) {
                        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Text(
                                text = "Current asset value",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            AnimatedValueText(
                                value = formatMoney(state.summary.totalCurrentValue),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            item {
                AssetSection(
                    title = "Recent",
                    assets = state.summary.recentAssets,
                    emptyTitle = "No assets yet",
                    emptyBody = "Add your first asset to make lookup useful.",
                    onAssetSelected = onAssetSelected
                )
            }

            item {
                AssetSection(
                    title = "Highest value",
                    assets = state.summary.topValuableAssets,
                    emptyTitle = "No value data",
                    emptyBody = "Assets with current value appear here.",
                    onAssetSelected = onAssetSelected
                )
            }
        }
    }
}

@Composable
private fun AssetSection(
    title: String,
    assets: List<Asset>,
    emptyTitle: String,
    emptyBody: String,
    onAssetSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionTitle(title)
        if (assets.isEmpty()) {
            EmptyState(title = emptyTitle, body = emptyBody)
        } else {
            AshPanel(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    assets.forEach { asset ->
                        AssetCompactRow(
                            asset = asset,
                            onClick = { onAssetSelected(asset.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBlock(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(6.dp))
        AnimatedValueText(
            value = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AnimatedValueText(
    value: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight? = null
) {
    AnimatedContent(
        targetState = value,
        transitionSpec = {
            ((
                slideInVertically(
                    animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
                    initialOffsetY = { it / 3 }
                ) + fadeIn(animationSpec = tween(durationMillis = 160))
                ) togetherWith (
                slideOutVertically(
                    animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
                    targetOffsetY = { -it / 3 }
                ) + fadeOut(animationSpec = tween(durationMillis = 120))
                )).using(SizeTransform(clip = false))
        },
        label = "AnimatedValueText"
    ) { animatedValue ->
        Text(
            text = animatedValue,
            modifier = modifier,
            style = style,
            color = color,
            fontWeight = fontWeight
        )
    }
}
