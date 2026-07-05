package ash.asset.data.datasource

interface AssetLocalDataSource {
    fun getAssets(): List<AssetEntity>
    fun saveAssets(assets: List<AssetEntity>)
}
