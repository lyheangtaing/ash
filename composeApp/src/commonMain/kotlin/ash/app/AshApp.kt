package ash.app

import ash.asset.data.datasource.SettingsAssetLocalDataSource
import ash.asset.data.repository.SettingsAssetRepository
import ash.asset.domain.usecase.AddAssetUseCase
import ash.asset.domain.usecase.AddMaintenanceRecordUseCase
import ash.asset.domain.usecase.AddReminderUseCase
import ash.asset.domain.usecase.AssetUseCases
import ash.asset.domain.usecase.CalculateAssetDecisionUseCase
import ash.asset.domain.usecase.CalculateAssetSummaryUseCase
import ash.asset.domain.usecase.DeleteAssetUseCase
import ash.asset.domain.usecase.FilterAssetsUseCase
import ash.asset.domain.usecase.GetAssetByIdUseCase
import ash.asset.domain.usecase.GetAssetsUseCase
import ash.asset.domain.usecase.SearchAssetsUseCase
import ash.asset.domain.usecase.SetReminderCompletedUseCase
import ash.asset.domain.usecase.UpdateAssetUseCase
import ash.asset.presentation.AssetEffect
import ash.asset.presentation.AssetIntent
import ash.asset.presentation.AssetViewModel
import ash.asset.presentation.activity.ActivityScreen
import ash.asset.presentation.detail.AssetDetailScreen
import ash.asset.presentation.edit.AssetEditScreen
import ash.asset.presentation.list.AssetListScreen
import ash.asset.presentation.maintenance.MaintenanceLogScreen
import ash.asset.presentation.reminder.ReminderEditScreen
import ash.asset.presentation.reminder.RemindersScreen
import ash.asset.presentation.settings.SettingsScreen
import ash.core.designsystem.AshTheme
import ash.core.navigation.AshBackHandler
import ash.core.navigation.AshRoute
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NotificationsNone
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.russhwolf.settings.Settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AshApp(settings: Settings) {
    AshTheme {
        val viewModel = remember(settings) { AssetViewModel(createAssetUseCases(settings)) }
        val state = viewModel.state
        val snackbarHostState = remember { SnackbarHostState() }
        var route by remember { mutableStateOf<AshRoute>(AshRoute.Collection) }
        val backStack = remember { mutableStateListOf<AshRoute>() }

        fun navigateTo(next: AshRoute) {
            if (next == route) return
            backStack += route
            route = next
        }
        fun navigateRoot(next: AshRoute) {
            backStack.clear()
            route = next
        }
        fun navigateBack() {
            route = if (backStack.isNotEmpty()) backStack.removeAt(backStack.lastIndex)
            else route.fallback() ?: route
        }
        fun replaceWith(next: AshRoute) {
            route = next
        }
        fun openAsset(id: String) {
            viewModel.dispatch(AssetIntent.SelectAsset(id))
            navigateTo(AshRoute.AssetDetail(id))
        }
        fun addAsset() {
            viewModel.dispatch(AssetIntent.StartAddAsset)
            navigateTo(AshRoute.EditAsset(null))
        }
        fun editAsset(id: String) {
            viewModel.dispatch(AssetIntent.StartEditAsset(id))
            navigateTo(AshRoute.EditAsset(id))
        }
        fun addHistory(id: String) {
            viewModel.dispatch(AssetIntent.StartMaintenance(id))
            navigateTo(AshRoute.MaintenanceLog(id))
        }
        fun addReminder(assetId: String?) {
            viewModel.dispatch(AssetIntent.StartReminder(assetId))
            navigateTo(AshRoute.EditReminder(assetId))
        }

        AshBackHandler(
            enabled = backStack.isNotEmpty() || route.fallback() != null,
            onBack = ::navigateBack
        )
        LaunchedEffect(state.effect) {
            (state.effect as? AssetEffect.Message)?.let {
                snackbarHostState.showSnackbar(it.text)
                viewModel.dispatch(AssetIntent.ClearEffect)
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                if (route.showsTopBar()) {
                    TopAppBar(
                        title = { Text(route.title()) },
                        navigationIcon = {
                            IconButton(onClick = ::navigateBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            scrolledContainerColor = MaterialTheme.colorScheme.background
                        )
                    )
                }
            },
            bottomBar = {
                if (route.showsBottomBar()) {
                    AshBottomNavigation(
                        route,
                        { navigateRoot(AshRoute.Collection) },
                        { navigateRoot(AshRoute.Activity) },
                        { navigateRoot(AshRoute.Reminders) },
                        onAddAsset = ::addAsset
                    )
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            AnimatedContent(
                targetState = route,
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                transitionSpec = {
                    val forward = targetState.depth() >= initialState.depth()
                    ((slideInHorizontally(tween(280, easing = FastOutSlowInEasing)) { if (forward) it / 4 else -it / 4 } + fadeIn()) togetherWith
                        (slideOutHorizontally(tween(220, easing = FastOutSlowInEasing)) { if (forward) -it / 6 else it / 6 } + fadeOut()))
                        .using(SizeTransform(clip = false))
                },
                label = "AshNavigation"
            ) { current ->
                when (current) {
                    AshRoute.Collection -> AssetListScreen(
                        state = state,
                        onSearchChanged = { viewModel.dispatch(AssetIntent.SearchChanged(it)) },
                        onCategoryChanged = { viewModel.dispatch(AssetIntent.CategoryFilterChanged(it)) },
                        onTagChanged = { viewModel.dispatch(AssetIntent.TagFilterChanged(it)) },
                        onSortChanged = { viewModel.dispatch(AssetIntent.SortChanged(it)) },
                        onClearFilters = { viewModel.dispatch(AssetIntent.ClearFilters) },
                        onAssetSelected = ::openAsset,
                        onAddAsset = ::addAsset,
                        onOpenSettings = { navigateTo(AshRoute.Settings) },
                        modifier = Modifier.fillMaxSize()
                    )
                    AshRoute.Activity -> ActivityScreen(state.assets, ::openAsset, Modifier.fillMaxSize())
                    AshRoute.Reminders -> RemindersScreen(
                        assets = state.assets,
                        onAddReminder = { addReminder(null) },
                        onReminderCompleted = { assetId, reminderId, complete ->
                            viewModel.dispatch(AssetIntent.ReminderCompleted(assetId, reminderId, complete))
                        },
                        onAssetSelected = ::openAsset,
                        modifier = Modifier.fillMaxSize()
                    )
                    AshRoute.Settings -> SettingsScreen(Modifier.fillMaxSize())
                    is AshRoute.AssetDetail -> {
                        val asset = state.assets.firstOrNull { it.id == current.assetId }
                        AssetDetailScreen(
                            asset = asset,
                            suggestion = state.suggestion.takeIf { state.suggestionAssetId == current.assetId },
                            onEdit = ::editAsset,
                            onMaintenance = ::addHistory,
                            onCreateReminder = { addReminder(it) },
                            onRequestSuggestion = { viewModel.dispatch(AssetIntent.RequestSuggestion(it)) },
                            onDelete = {
                                viewModel.dispatch(AssetIntent.DeleteAsset(it))
                                navigateRoot(AshRoute.Collection)
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    is AshRoute.EditAsset -> AssetEditScreen(
                        form = state.editForm,
                        isEditing = current.assetId != null,
                        onFormChanged = { viewModel.dispatch(AssetIntent.AssetFormChanged(it)) },
                        onSave = {
                            viewModel.dispatch(AssetIntent.SaveAsset)
                            val id = viewModel.state.selectedAssetId
                            if (viewModel.state.editForm.error == null && id != null) replaceWith(AshRoute.AssetDetail(id))
                        },
                        onCancel = ::navigateBack,
                        modifier = Modifier.fillMaxSize()
                    )
                    is AshRoute.MaintenanceLog -> MaintenanceLogScreen(
                        asset = state.assets.firstOrNull { it.id == current.assetId },
                        form = state.maintenanceForm,
                        onFormChanged = { viewModel.dispatch(AssetIntent.MaintenanceFormChanged(it)) },
                        onAddRecord = { viewModel.dispatch(AssetIntent.AddMaintenanceRecord) },
                        onDone = { replaceWith(AshRoute.AssetDetail(current.assetId)) },
                        modifier = Modifier.fillMaxSize()
                    )
                    is AshRoute.EditReminder -> ReminderEditScreen(
                        assets = state.assets,
                        form = state.reminderForm,
                        onFormChanged = { viewModel.dispatch(AssetIntent.ReminderFormChanged(it)) },
                        onSave = {
                            viewModel.dispatch(AssetIntent.SaveReminder)
                            if (viewModel.state.reminderForm.error == null) {
                                replaceWith(current.assetId?.let(AshRoute::AssetDetail) ?: AshRoute.Reminders)
                            }
                        },
                        onCancel = ::navigateBack,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

private fun createAssetUseCases(settings: Settings): AssetUseCases {
    val repository = SettingsAssetRepository(SettingsAssetLocalDataSource(settings))
    return AssetUseCases(
        addAsset = AddAssetUseCase(repository),
        updateAsset = UpdateAssetUseCase(repository),
        deleteAsset = DeleteAssetUseCase(repository),
        getAssets = GetAssetsUseCase(repository),
        getAssetById = GetAssetByIdUseCase(repository),
        searchAssets = SearchAssetsUseCase(),
        filterAssets = FilterAssetsUseCase(),
        calculateAssetSummary = CalculateAssetSummaryUseCase(),
        addMaintenanceRecord = AddMaintenanceRecordUseCase(repository),
        addReminder = AddReminderUseCase(repository),
        setReminderCompleted = SetReminderCompletedUseCase(repository),
        calculateAssetDecision = CalculateAssetDecisionUseCase()
    )
}

@Composable
private fun AshBottomNavigation(
    route: AshRoute,
    onCollection: () -> Unit,
    onActivity: () -> Unit,
    onReminders: () -> Unit,
    onAddAsset: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val showAdd = route is AshRoute.Collection || route is AshRoute.Activity || route is AshRoute.Reminders
    val selectedIndex = when {
        route.collectionSelected() -> 0
        route is AshRoute.Activity -> 1
        else -> 2
    }
    val items = listOf(
        LiquidNavItem(Icons.Default.CollectionsBookmark, "Collection", onCollection),
        LiquidNavItem(Icons.Default.History, "Activity", onActivity),
        LiquidNavItem(Icons.Default.NotificationsNone, "Reminders", onReminders)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LiquidGlassTabs(
            items = items,
            selectedIndex = selectedIndex,
            isDark = isDark,
            modifier = Modifier.width(224.dp)
        )
        if (showAdd) {
            Spacer(Modifier.width(8.dp))
            LiquidAddButton(onClick = onAddAsset, isDark = isDark)
        }
    }
}

private data class LiquidNavItem(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit
)

@Composable
private fun LiquidGlassTabs(
    items: List<LiquidNavItem>,
    selectedIndex: Int,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val glassShape = CircleShape
    val selectionShape = RoundedCornerShape(28.dp)
    val glassBrush = Brush.verticalGradient(
        colors = if (isDark) {
            listOf(Color.White.copy(alpha = 0.16f), Color(0xFF252525).copy(alpha = 0.90f))
        } else {
            listOf(Color.White.copy(alpha = 0.94f), Color(0xFFF4F4F1).copy(alpha = 0.82f))
        }
    )
    Surface(
        modifier = modifier.height(68.dp),
        shape = glassShape,
        color = Color.Transparent,
        border = BorderStroke(
            1.dp,
            if (isDark) Color.White.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.92f)
        ),
        shadowElevation = 16.dp
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize().background(glassBrush, glassShape).padding(6.dp)
        ) {
            val itemWidth = maxWidth / items.size
            val selectionWidth = 68.dp
            val selectionHeight = 56.dp
            val selectionOffset by animateDpAsState(
                targetValue = itemWidth * selectedIndex + (itemWidth - selectionWidth) / 2,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "LiquidGlassSelection"
            )
            Surface(
                modifier = Modifier
                    .offset(x = selectionOffset)
                    .size(width = selectionWidth, height = selectionHeight),
                shape = selectionShape,
                color = if (isDark) Color.White.copy(alpha = 0.14f) else Color.White.copy(alpha = 0.78f),
                border = BorderStroke(
                    1.dp,
                    if (isDark) Color.White.copy(alpha = 0.16f) else Color.White.copy(alpha = 0.90f)
                ),
                shadowElevation = if (isDark) 0.dp else 2.dp
            ) {}
            Row(Modifier.fillMaxSize()) {
                items.forEachIndexed { index, item ->
                    Box(
                        modifier = Modifier.width(itemWidth).fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        BottomNavItem(
                            selected = index == selectedIndex,
                            icon = item.icon,
                            label = item.label,
                            onClick = item.onClick,
                            modifier = Modifier.size(width = selectionWidth, height = selectionHeight)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    selected: Boolean,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(if (selected) 1f else 0.94f, tween(180), label = "NavItemScale")
    val color by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
        tween(180),
        label = "NavItemColor"
    )
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .selectable(selected = selected, onClick = onClick, role = Role.Tab)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, label, Modifier.size(20.dp), tint = color)
        Text(
            label,
            modifier = Modifier.padding(horizontal = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                lineHeight = 12.sp
            ),
            color = color,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            maxLines = 1
        )
    }
}

@Composable
private fun LiquidAddButton(onClick: () -> Unit, isDark: Boolean) {
    val shape = CircleShape
    Surface(
        onClick = onClick,
        modifier = Modifier.size(62.dp),
        shape = shape,
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        border = BorderStroke(
            1.dp,
            if (isDark) Color.White.copy(alpha = 0.20f) else Color.White.copy(alpha = 0.72f)
        ),
        shadowElevation = 16.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.18f), Color.Transparent)
                ),
                shape
            ),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Add, "Add asset", Modifier.size(24.dp))
        }
    }
}

private fun AshRoute.title() = when (this) {
    AshRoute.Collection -> "Collection"
    AshRoute.Activity -> "Activity"
    AshRoute.Reminders -> "Reminders"
    AshRoute.Settings -> "Settings"
    is AshRoute.AssetDetail -> "Asset"
    is AshRoute.EditAsset -> if (assetId == null) "Add asset" else "Edit asset"
    is AshRoute.MaintenanceLog -> "History"
    is AshRoute.EditReminder -> "Reminder"
}

private fun AshRoute.fallback(): AshRoute? = when (this) {
    AshRoute.Collection, AshRoute.Activity, AshRoute.Reminders -> null
    AshRoute.Settings -> AshRoute.Collection
    is AshRoute.AssetDetail -> AshRoute.Collection
    is AshRoute.EditAsset -> assetId?.let(AshRoute::AssetDetail) ?: AshRoute.Collection
    is AshRoute.MaintenanceLog -> AshRoute.AssetDetail(assetId)
    is AshRoute.EditReminder -> assetId?.let(AshRoute::AssetDetail) ?: AshRoute.Reminders
}

private fun AshRoute.depth() = when (this) {
    AshRoute.Collection, AshRoute.Activity, AshRoute.Reminders -> 0
    AshRoute.Settings, is AshRoute.AssetDetail -> 1
    is AshRoute.EditAsset, is AshRoute.MaintenanceLog, is AshRoute.EditReminder -> 2
}

private fun AshRoute.showsTopBar() = this !in listOf(AshRoute.Collection, AshRoute.Activity, AshRoute.Reminders)
private fun AshRoute.showsBottomBar() = this is AshRoute.Collection || this is AshRoute.Activity ||
    this is AshRoute.Reminders || this is AshRoute.AssetDetail
private fun AshRoute.collectionSelected() = this is AshRoute.Collection || this is AshRoute.AssetDetail
private fun AshRoute.remindersSelected() = this is AshRoute.Reminders || this is AshRoute.EditReminder
