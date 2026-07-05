package ash.app

import ash.asset.data.datasource.SettingsAssetLocalDataSource
import ash.asset.data.repository.SettingsAssetRepository
import ash.asset.domain.usecase.AddAssetUseCase
import ash.asset.domain.usecase.AddMaintenanceRecordUseCase
import ash.asset.domain.usecase.AssetUseCases
import ash.asset.domain.usecase.CalculateAssetDecisionUseCase
import ash.asset.domain.usecase.CalculateAssetSummaryUseCase
import ash.asset.domain.usecase.DeleteAssetUseCase
import ash.asset.domain.usecase.FilterAssetsUseCase
import ash.asset.domain.usecase.GetAssetByIdUseCase
import ash.asset.domain.usecase.GetAssetsUseCase
import ash.asset.domain.usecase.SearchAssetsUseCase
import ash.asset.domain.usecase.UpdateAssetUseCase
import ash.asset.presentation.AssetEffect
import ash.asset.presentation.AssetIntent
import ash.asset.presentation.AssetViewModel
import ash.asset.presentation.dashboard.DashboardScreen
import ash.asset.presentation.detail.AssetDetailScreen
import ash.asset.presentation.edit.AssetEditScreen
import ash.asset.presentation.list.AssetListScreen
import ash.asset.presentation.maintenance.MaintenanceLogScreen
import ash.core.designsystem.AshTheme
import ash.core.navigation.AshBackHandler
import ash.core.navigation.AshRoute
import com.russhwolf.settings.Settings
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AshApp(settings: Settings) {
    AshTheme {
        val viewModel = remember(settings) { AssetViewModel(createAssetUseCases(settings)) }
        val state = viewModel.state
        val snackbarHostState = remember { SnackbarHostState() }
        var route by remember { mutableStateOf<AshRoute>(AshRoute.Dashboard) }
        val backStack = remember { mutableStateListOf<AshRoute>() }

        fun navigateTo(nextRoute: AshRoute) {
            if (nextRoute == route) return
            backStack.add(route)
            route = nextRoute
        }

        fun replaceWith(nextRoute: AshRoute) {
            if (backStack.lastOrNull() == nextRoute) {
                backStack.removeAt(backStack.lastIndex)
            }
            route = nextRoute
        }

        fun navigateHome() {
            backStack.clear()
            route = AshRoute.Dashboard
        }

        fun navigateAssetsRoot() {
            if (route == AshRoute.AssetList) return
            if (route == AshRoute.Dashboard) {
                navigateTo(AshRoute.AssetList)
            } else {
                backStack.clear()
                route = AshRoute.AssetList
            }
        }

        fun navigateBack() {
            route = if (backStack.isNotEmpty()) {
                backStack.removeAt(backStack.lastIndex)
            } else {
                route.fallbackBackDestination() ?: route
            }
        }

        fun openDetail(assetId: String) {
            viewModel.dispatch(AssetIntent.SelectAsset(assetId))
            navigateTo(AshRoute.AssetDetail(assetId))
        }

        fun openAddAsset() {
            viewModel.dispatch(AssetIntent.StartAddAsset)
            navigateTo(AshRoute.EditAsset(assetId = null))
        }

        fun openEditAsset(assetId: String) {
            viewModel.dispatch(AssetIntent.StartEditAsset(assetId))
            navigateTo(AshRoute.EditAsset(assetId = assetId))
        }

        fun openMaintenance(assetId: String) {
            viewModel.dispatch(AssetIntent.StartMaintenance(assetId))
            navigateTo(AshRoute.MaintenanceLog(assetId))
        }

        AshBackHandler(
            enabled = backStack.isNotEmpty() || route.fallbackBackDestination() != null,
            onBack = ::navigateBack
        )

        LaunchedEffect(state.effect) {
            val effect = state.effect
            if (effect is AssetEffect.Message) {
                snackbarHostState.showSnackbar(effect.text)
                viewModel.dispatch(AssetIntent.ClearEffect)
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                if (route.canGoBack()) {
                    TopAppBar(
                    title = { Text(route.title()) },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            scrolledContainerColor = MaterialTheme.colorScheme.background
                        ),
                        navigationIcon = {
                            IconButton(onClick = ::navigateBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        }
                    )
                }
            },
            bottomBar = {
                LiquidGlassBottomNavigation(
                    route = route,
                    onDashboardClick = ::navigateHome,
                    onAddClick = ::openAddAsset,
                    onAssetsClick = ::navigateAssetsRoot
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            val screenModifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)

            AnimatedContent(
                targetState = route,
                modifier = screenModifier,
                transitionSpec = {
                    val movingForward = targetState.depth() >= initialState.depth()
                    val enterOffset = { width: Int -> if (movingForward) width / 3 else -width / 3 }
                    val exitOffset = { width: Int -> if (movingForward) -width / 5 else width / 5 }

                    ((
                        slideInHorizontally(
                            animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing),
                            initialOffsetX = enterOffset
                        ) + fadeIn(animationSpec = tween(durationMillis = 180))
                        ) togetherWith (
                        slideOutHorizontally(
                            animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
                            targetOffsetX = exitOffset
                        ) + fadeOut(animationSpec = tween(durationMillis = 160))
                        )).using(SizeTransform(clip = false))
                },
                label = "AshRouteTransition"
            ) { currentRoute ->
                when (currentRoute) {
                    AshRoute.Dashboard -> DashboardScreen(
                        state = state,
                        decisionFor = viewModel::decisionFor,
                        onSearchChanged = { viewModel.dispatch(AssetIntent.SearchChanged(it)) },
                        onAssetSelected = ::openDetail,
                        onAddAsset = ::openAddAsset,
                        onOpenAssets = { navigateTo(AshRoute.AssetList) },
                        modifier = Modifier.fillMaxSize()
                    )
                    AshRoute.AssetList -> AssetListScreen(
                        state = state,
                        decisionFor = viewModel::decisionFor,
                        onSearchChanged = { viewModel.dispatch(AssetIntent.SearchChanged(it)) },
                        onCategoryChanged = { viewModel.dispatch(AssetIntent.CategoryFilterChanged(it)) },
                        onConditionChanged = { viewModel.dispatch(AssetIntent.ConditionFilterChanged(it)) },
                        onOwnershipChanged = { viewModel.dispatch(AssetIntent.OwnershipFilterChanged(it)) },
                        onClearFilters = { viewModel.dispatch(AssetIntent.ClearFilters) },
                        onAssetSelected = ::openDetail,
                        modifier = Modifier.fillMaxSize()
                    )
                    is AshRoute.AssetDetail -> {
                        val asset = state.assets.firstOrNull { it.id == currentRoute.assetId }
                        AssetDetailScreen(
                            asset = asset,
                            decision = asset?.let(viewModel::decisionFor),
                            onEdit = ::openEditAsset,
                            onMaintenance = ::openMaintenance,
                            onDelete = {
                                viewModel.dispatch(AssetIntent.DeleteAsset(it))
                                backStack.clear()
                                route = AshRoute.AssetList
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    is AshRoute.EditAsset -> AssetEditScreen(
                        form = state.editForm,
                        isEditing = currentRoute.assetId != null,
                        onFormChanged = { viewModel.dispatch(AssetIntent.AssetFormChanged(it)) },
                        onSave = {
                            viewModel.dispatch(AssetIntent.SaveAsset)
                            val savedId = viewModel.state.selectedAssetId
                            if (viewModel.state.editForm.error == null && savedId != null) {
                                replaceWith(AshRoute.AssetDetail(savedId))
                            }
                        },
                        onCancel = {
                            navigateBack()
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                    is AshRoute.MaintenanceLog -> MaintenanceLogScreen(
                        asset = state.assets.firstOrNull { it.id == currentRoute.assetId },
                        form = state.maintenanceForm,
                        onFormChanged = { viewModel.dispatch(AssetIntent.MaintenanceFormChanged(it)) },
                        onAddRecord = { viewModel.dispatch(AssetIntent.AddMaintenanceRecord) },
                        onDone = { replaceWith(AshRoute.AssetDetail(currentRoute.assetId)) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

private fun createAssetUseCases(settings: Settings): AssetUseCases {
    val repository = SettingsAssetRepository(SettingsAssetLocalDataSource(settings))
    val calculateAssetDecision = CalculateAssetDecisionUseCase()
    return AssetUseCases(
        addAsset = AddAssetUseCase(repository),
        updateAsset = UpdateAssetUseCase(repository),
        deleteAsset = DeleteAssetUseCase(repository),
        getAssets = GetAssetsUseCase(repository),
        getAssetById = GetAssetByIdUseCase(repository),
        searchAssets = SearchAssetsUseCase(),
        filterAssets = FilterAssetsUseCase(),
        calculateAssetSummary = CalculateAssetSummaryUseCase(calculateAssetDecision),
        addMaintenanceRecord = AddMaintenanceRecordUseCase(repository),
        calculateAssetDecision = calculateAssetDecision
    )
}

private fun AshRoute.title(): String {
    return when (this) {
        AshRoute.Dashboard -> "Lookup"
        AshRoute.AssetList -> "Assets"
        is AshRoute.AssetDetail -> "Asset"
        is AshRoute.EditAsset -> if (assetId == null) "Add asset" else "Edit asset"
        is AshRoute.MaintenanceLog -> "Maintenance"
    }
}

private fun AshRoute.canGoBack(): Boolean {
    return this is AshRoute.AssetDetail || this is AshRoute.EditAsset || this is AshRoute.MaintenanceLog
}

private fun AshRoute.fallbackBackDestination(): AshRoute? {
    return when (this) {
        AshRoute.Dashboard -> null
        AshRoute.AssetList -> AshRoute.Dashboard
        is AshRoute.AssetDetail -> AshRoute.AssetList
        is AshRoute.EditAsset -> assetId?.let { AshRoute.AssetDetail(it) } ?: AshRoute.AssetList
        is AshRoute.MaintenanceLog -> AshRoute.AssetDetail(assetId)
    }
}

private fun AshRoute.depth(): Int {
    return when (this) {
        AshRoute.Dashboard -> 0
        AshRoute.AssetList -> 1
        is AshRoute.AssetDetail -> 2
        is AshRoute.EditAsset -> 3
        is AshRoute.MaintenanceLog -> 3
    }
}

@Composable
private fun LiquidGlassBottomNavigation(
    route: AshRoute,
    onDashboardClick: () -> Unit,
    onAddClick: () -> Unit,
    onAssetsClick: () -> Unit
) {
    val glassShape = RoundedCornerShape(36.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 22.dp, end = 22.dp, top = 6.dp, bottom = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp),
            shape = glassShape,
            color = Color.White.copy(alpha = 0.82f),
            border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.10f)),
            shadowElevation = 12.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.94f),
                                Color.White.copy(alpha = 0.72f),
                                Color(0xFFF5F5F2).copy(alpha = 0.78f)
                            )
                        ),
                        shape = glassShape
                    )
                    .border(
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.72f)),
                        shape = glassShape
                    )
                    .padding(horizontal = 9.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LiquidGlassNavItem(
                        selected = route is AshRoute.Dashboard,
                        icon = Icons.Default.Home,
                        label = "Lookup",
                        onClick = onDashboardClick,
                        modifier = Modifier.weight(1f)
                    )
                    LiquidGlassAddButton(
                        onClick = onAddClick,
                        modifier = Modifier.size(50.dp)
                    )
                    LiquidGlassNavItem(
                        selected = route.isAssetArea(),
                        icon = Icons.AutoMirrored.Filled.List,
                        label = "Assets",
                        onClick = onAssetsClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun LiquidGlassNavItem(
    selected: Boolean,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val itemShape = RoundedCornerShape(28.dp)
    val selectedBackground by animateFloatAsState(
        targetValue = if (selected) 0.90f else 0f,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "NavSelectedBackground"
    )
    val itemScale by animateFloatAsState(
        targetValue = if (selected) 1f else 0.96f,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "NavItemScale"
    )
    val contentColor = if (selected) Color.White else Color.Black.copy(alpha = 0.58f)
    Column(
        modifier = modifier
            .height(50.dp)
            .graphicsLayer {
                scaleX = itemScale
                scaleY = itemScale
            }
            .clip(itemShape)
            .background(Color.Black.copy(alpha = selectedBackground), itemShape)
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(20.dp),
            tint = contentColor
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun LiquidGlassAddButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
        label = "AddButtonScale"
    )
    val buttonElevation by animateDpAsState(
        targetValue = 12.dp,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "AddButtonElevation"
    )
    Surface(
        modifier = modifier.graphicsLayer {
            scaleX = buttonScale
            scaleY = buttonScale
        },
        onClick = onClick,
        shape = CircleShape,
        color = Color.Black,
        contentColor = Color.White,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.72f)),
        shadowElevation = buttonElevation
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add asset",
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

private fun AshRoute.isAssetArea(): Boolean {
    return when (this) {
        AshRoute.Dashboard -> false
        AshRoute.AssetList -> true
        is AshRoute.AssetDetail -> true
        is AshRoute.EditAsset -> assetId != null
        is AshRoute.MaintenanceLog -> true
    }
}
