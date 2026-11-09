package de.stryi.uhrzeitansage.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.stryi.uhrzeitansage.model.AnnouncementRule
import de.stryi.uhrzeitansage.model.DayOfWeekInfo
import de.stryi.uhrzeitansage.util.TimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRuleDialog(
    initialRule: AnnouncementRule?,
    onDismiss: () -> Unit,
    onSave: (AnnouncementRule) -> Unit,
    onTestSpeech: (String) -> Unit = {}
) {
    var title by remember { mutableStateOf(initialRule?.title ?: "") }
    var speechPattern by remember { mutableStateOf(initialRule?.speechPattern ?: AnnouncementRule.DEFAULT_SPEECH_PATTERN) }
    var isTimeRangeMode by remember { mutableStateOf(initialRule == null || (initialRule.startHour != initialRule.endHour || initialRule.startMinute != initialRule.endMinute)) }
    var selectedDays by remember { mutableStateOf(initialRule?.daysOfWeek ?: setOf(1, 2, 3, 4, 5, 6, 7)) }
    var startHour by remember { mutableIntStateOf(initialRule?.startHour ?: 8) }
    var startMinute by remember { mutableIntStateOf(initialRule?.startMinute ?: 0) }
    var endHour by remember { mutableIntStateOf(initialRule?.endHour ?: 20) }
    var endMinute by remember { mutableIntStateOf(initialRule?.endMinute ?: 0) }
    var intervalMinutes by remember { mutableIntStateOf(initialRule?.intervalMinutes ?: 15) }

    val isEditing = initialRule != null
    val dialogTitle = if (isEditing) "Ansage bearbeiten" else "Neue Zeitansage"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = dialogTitle,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Bezeichnung (z.B. Arbeitszeit)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Ansagetext (Sprachausgabe)
                Column {
                    OutlinedTextField(
                        value = speechPattern,
                        onValueChange = { speechPattern = it },
                        label = { Text("Ansagetext (Sprachausgabe)") },
                        supportingText = { Text("Platzhalter {zeit} wird durch die Uhrzeit ersetzt.") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AssistChip(
                            onClick = {
                                val textToSpeak = TimeFormatter.formatSpeechWithPattern(
                                    speechPattern.ifBlank { AnnouncementRule.DEFAULT_SPEECH_PATTERN },
                                    startHour,
                                    startMinute
                                )
                                onTestSpeech(textToSpeak)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = { Text("Anhören", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )
                        AssistChip(
                            onClick = {
                                if (!speechPattern.contains("{zeit}", ignoreCase = true)) {
                                    speechPattern = if (speechPattern.isBlank()) "{zeit}" else "$speechPattern {zeit}"
                                }
                            },
                            label = { Text("+ {zeit}", fontSize = 11.sp) }
                        )
                        AssistChip(
                            onClick = { speechPattern = AnnouncementRule.DEFAULT_SPEECH_PATTERN },
                            label = { Text("Standard", fontSize = 11.sp) }
                        )
                    }
                }

                Column {
                    Text(
                        text = "Wochentage",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DayOfWeekInfo.ALL_DAYS.forEach { dayInfo ->
                            val isSelected = selectedDays.contains(dayInfo.dayNumber)
                            val bgColor = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                            val textColor = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(bgColor)
                                    .clickable {
                                        selectedDays = if (isSelected) {
                                            if (selectedDays.size > 1) selectedDays - dayInfo.dayNumber else selectedDays
                                        } else {
                                            selectedDays + dayInfo.dayNumber
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayInfo.shortName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                            }
                        }
                    }
                }

                Column {
                    Text(
                        text = "Ansage-Typ",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { isTimeRangeMode = true }
                        ) {
                            RadioButton(
                                selected = isTimeRangeMode,
                                onClick = { isTimeRangeMode = true }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Uhrzeitbereich")
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { isTimeRangeMode = false }
                        ) {
                            RadioButton(
                                selected = !isTimeRangeMode,
                                onClick = { isTimeRangeMode = false }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Uhrzeit")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TimeDropdownPicker(
                            label = if (isTimeRangeMode) "Von" else "Uhrzeit",
                            hour = startHour,
                            minute = startMinute,
                            onTimeSelected = { h, m ->
                                startHour = h
                                startMinute = m
                            },
                            modifier = Modifier.weight(1f)
                        )

                        if (isTimeRangeMode) {
                            Spacer(modifier = Modifier.width(12.dp))

                            TimeDropdownPicker(
                                label = "Bis",
                                hour = endHour,
                                minute = endMinute,
                                onTimeSelected = { h, m ->
                                    endHour = h
                                    endMinute = m
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                if (isTimeRangeMode) {
                    Column {
                        Text(
                            text = "Zeitabstand",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Alle",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )

                            var intervalDropdownExpanded by remember { mutableStateOf(false) }
                            val intervalOptions = listOf(1, 2, 3, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 70, 80, 90, 100, 110, 120)

                            ExposedDropdownMenuBox(
                                expanded = intervalDropdownExpanded,
                                onExpandedChange = { intervalDropdownExpanded = !intervalDropdownExpanded },
                                modifier = Modifier.width(110.dp)
                            ) {
                                OutlinedTextField(
                                    value = intervalMinutes.toString(),
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = intervalDropdownExpanded) },
                                    modifier = Modifier
                                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                        .fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                ExposedDropdownMenu(
                                    expanded = intervalDropdownExpanded,
                                    onDismissRequest = { intervalDropdownExpanded = false },
                                    modifier = Modifier.height(240.dp)
                                ) {
                                    intervalOptions.forEach { opt ->
                                        DropdownMenuItem(
                                            text = { Text("$opt") },
                                            onClick = {
                                                intervalMinutes = opt
                                                intervalDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "Minuten",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val finalEndHour = if (isTimeRangeMode) endHour else startHour
                    val finalEndMinute = if (isTimeRangeMode) endMinute else startMinute

                    val rule = AnnouncementRule(
                        id = initialRule?.id ?: java.util.UUID.randomUUID().toString(),
                        title = title.trim(),
                        enabled = initialRule?.enabled ?: true,
                        daysOfWeek = selectedDays,
                        startHour = startHour,
                        startMinute = startMinute,
                        endHour = finalEndHour,
                        endMinute = finalEndMinute,
                        intervalMinutes = intervalMinutes,
                        speechPattern = speechPattern.trim().ifBlank { AnnouncementRule.DEFAULT_SPEECH_PATTERN }
                    )
                    onSave(rule)
                }
            ) {
                Text("Speichern", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeDropdownPicker(
    label: String,
    hour: Int,
    minute: Int,
    onTimeSelected: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = TimeFormatter.formatTimeString(hour, minute),
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.height(240.dp)
            ) {
                for (h in 0..23) {
                    for (m in listOf(0, 15, 30, 45)) {
                        DropdownMenuItem(
                            text = { Text(TimeFormatter.formatTimeString(h, m)) },
                            onClick = {
                                onTimeSelected(h, m)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
