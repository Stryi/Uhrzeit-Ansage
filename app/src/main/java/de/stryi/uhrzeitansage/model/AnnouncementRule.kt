package de.stryi.uhrzeitansage.model

import java.util.UUID

data class AnnouncementRule(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val enabled: Boolean = true,
    val daysOfWeek: Set<Int> = setOf(1, 2, 3, 4, 5, 6, 7),
    val startHour: Int = 8,
    val startMinute: Int = 0,
    val endHour: Int = 20,
    val endMinute: Int = 0,
    val intervalMinutes: Int = 15,
    val speechPattern: String = DEFAULT_SPEECH_PATTERN
) {
    companion object {
        const val DEFAULT_SPEECH_PATTERN = "Es ist {zeit}"
    }
}
