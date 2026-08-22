package ash.asset.presentation.reminder

import ash.asset.domain.model.Asset
import ash.asset.domain.model.ReminderRecurrence
import ash.asset.presentation.ReminderFormState
import ash.asset.presentation.components.AshDropdown
import ash.core.designsystem.AshPanel
import ash.core.designsystem.AshRadius
import ash.core.designsystem.AshSpacing
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ReminderEditScreen(
    assets: List<Asset>,
    form: ReminderFormState,
    onFormChanged: (ReminderFormState) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier,
        contentPadding = PaddingValues(AshSpacing.screen, AshSpacing.md, AshSpacing.screen, AshSpacing.bottomNavPadding),
        verticalArrangement = Arrangement.spacedBy(AshSpacing.lg)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Create reminder", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("Ash only reminds you when you ask.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item {
            AshPanel(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(AshSpacing.md)) {
                    if (assets.isNotEmpty()) {
                        AshDropdown(
                            label = "Asset",
                            selected = assets.firstOrNull { it.id == form.assetId } ?: assets.first(),
                            options = assets,
                            optionLabel = Asset::name,
                            onSelected = { onFormChanged(form.copy(assetId = it.id)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    ReminderField("Action", form.action) { onFormChanged(form.copy(action = it)) }
                    ReminderField("Date (YYYY-MM-DD)", form.dueDate) { onFormChanged(form.copy(dueDate = it)) }
                    ReminderField("Time (HH:MM, optional)", form.time) { onFormChanged(form.copy(time = it)) }
                    AshDropdown(
                        "Repeat",
                        form.recurrence,
                        ReminderRecurrence.entries,
                        ReminderRecurrence::label,
                        { onFormChanged(form.copy(recurrence = it)) },
                        Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        form.notes,
                        { onFormChanged(form.copy(notes = it)) },
                        Modifier.fillMaxWidth(),
                        label = { Text("Note (optional)") },
                        minLines = 2,
                        shape = RoundedCornerShape(AshRadius.md)
                    )
                    form.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    Button(onClick = onSave, modifier = Modifier.fillMaxWidth().height(52.dp), enabled = assets.isNotEmpty()) {
                        Text("Create reminder")
                    }
                    OutlinedButton(onClick = onCancel, modifier = Modifier.fillMaxWidth()) { Text("Cancel") }
                }
            }
        }
    }
}

@Composable
private fun ReminderField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value,
        onValueChange,
        Modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = true,
        shape = RoundedCornerShape(AshRadius.md)
    )
}
