package ash.asset.domain.usecase

data class AssetUseCases(
    val addAsset: AddAssetUseCase,
    val updateAsset: UpdateAssetUseCase,
    val deleteAsset: DeleteAssetUseCase,
    val getAssets: GetAssetsUseCase,
    val getAssetById: GetAssetByIdUseCase,
    val searchAssets: SearchAssetsUseCase,
    val filterAssets: FilterAssetsUseCase,
    val calculateAssetSummary: CalculateAssetSummaryUseCase,
    val addMaintenanceRecord: AddMaintenanceRecordUseCase,
    val calculateAssetDecision: CalculateAssetDecisionUseCase
)
