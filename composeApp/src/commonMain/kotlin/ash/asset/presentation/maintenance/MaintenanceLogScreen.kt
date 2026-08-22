package ash.asset.presentation.maintenance

import ash.asset.domain.model.Asset
import ash.asset.domain.model.AssetEventType
import ash.asset.presentation.MaintenanceFormState
import ash.asset.presentation.components.AshDropdown
import ash.asset.presentation.image.AssetImagePreview
import ash.asset.presentation.image.rememberAssetImagePicker
import ash.core.designsystem.AshPanel
import ash.core.designsystem.AshRadius
import ash.core.designsystem.AshSpacing
import ash.core.util.formatMoney
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun MaintenanceLogScreen(
    asset: Asset?,
    form: MaintenanceFormState,
    onFormChanged: (MaintenanceFormState) -> Unit,
    onAddRecord: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imagePicker = rememberAssetImagePicker { selected ->
        onFormChanged(form.copy(imageUris = (form.imageUris + selected).distinct()))
    }
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(AshSpacing.screen, AshSpacing.md, AshSpacing.screen, AshSpacing.bottomNavPadding),
        verticalArrangement = Arrangement.spacedBy(AshSpacing.lg)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Add history event", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(
                    asset?.name ?: "Asset history",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        item {
            AshPanel(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(AshSpacing.md)) {
                    AshDropdown(
                        "Event",
                        form.eventType,
                        AssetEventType.entries,
                        AssetEventType::label,
                        { onFormChanged(form.copy(eventType = it)) },
                        Modifier.fillMaxWidth()
                    )
                    HistoryField("Description", form.type, { onFormChanged(form.copy(type = it)) })
                    HistoryField("Date (YYYY-MM-DD)", form.date, { onFormChanged(form.copy(date = it)) })
                    HistoryField(
                        "Cost (USD, optional)", form.cost,
                        { onFormChanged(form.copy(cost = it.filter { char -> char.isDigit() || char == '.' })) },
                        KeyboardType.Decimal
                    )
                    OutlinedTextField(
                        value = form.notes,
                        onValueChange = { onFormChanged(form.copy(notes = it)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        shape = RoundedCornerShape(AshRadius.md),
                        label = { Text("Note (optional)") }
                    )
                    if (form.imageUris.isNotEmpty()) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(AshSpacing.sm)) {
                            items(form.imageUris) { uri ->
                                AssetImagePreview(uri, "Event photo", Modifier.size(82.dp))
                            }
                        }
                    }
                    OutlinedButton(
                        onClick = imagePicker::launch,
                        enabled = imagePicker.isAvailable,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, null)
                        Text("  Add event photos")
                    }
                    form.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    Button(onClick = onAddRecord, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text("  Save event")
                    }
                    OutlinedButton(onClick = onDone, modifier = Modifier.fillMaxWidth().height(48.dp)) {
                        Text("Done")
                    }
                }
            }
        }
        if (!asset?.maintenanceRecords.isNullOrEmpty()) {
            item { Text("Recent history", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            items(asset?.maintenanceRecords.orEmpty().take(3), key = { it.id }) { record ->
                AshPanel(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(record.type, fontWeight = FontWeight.SemiBold)
                        Text(
                            "${record.eventType.label} · ${record.date} · ${formatMoney(record.cost)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value,
        onValueChange,
        Modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = true,
        shape = RoundedCornerShape(AshRadius.md),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}
