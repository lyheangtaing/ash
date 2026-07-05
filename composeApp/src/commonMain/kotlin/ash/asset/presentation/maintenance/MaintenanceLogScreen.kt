package ash.asset.presentation.maintenance

import ash.asset.domain.model.Asset
import ash.asset.presentation.MaintenanceFormState
import ash.asset.presentation.components.EmptyState
import ash.asset.presentation.components.SectionTitle
import ash.core.designsystem.AshPanel
import ash.core.util.formatMoney
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 116.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = asset?.name ?: "Maintenance",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                form.error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            SectionTitle("New record")
            AshPanel(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    MaintenanceTextField(
                        label = "Date",
                        value = form.date,
                        onValueChange = { onFormChanged(form.copy(date = it)) }
                    )
                    MaintenanceTextField(
                        label = "Cost",
                        value = form.cost,
                        onValueChange = { onFormChanged(form.copy(cost = it)) },
                        keyboardType = KeyboardType.Decimal
                    )
                    MaintenanceTextField(
                        label = "Type",
                        value = form.type,
                        onValueChange = { onFormChanged(form.copy(type = it)) }
                    )
                    OutlinedTextField(
                        value = form.notes,
                        onValueChange = { onFormChanged(form.copy(notes = it)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        shape = RoundedCornerShape(8.dp),
                        label = { Text("Notes") }
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            onClick = onDone
                        ) {
                            Text("Done")
                        }
                        Button(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            onClick = onAddRecord
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Text("Add")
                        }
                    }
                }
            }
        }

        item {
            SectionTitle("History")
        }

        val records = asset?.maintenanceRecords.orEmpty()
        if (records.isEmpty()) {
            item {
                EmptyState(
                    title = "No records",
                    body = "Add service, repair, cleaning, refill, or replacement notes."
                )
            }
        } else {
            items(records, key = { it.id }) { record ->
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
                            Text(record.notes)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MaintenanceTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        label = { Text(label) }
    )
}
