package de.stryi.uhrzeitansage.util

import de.stryi.uhrzeitansage.model.AnnouncementRule
import java.util.Calendar
import java.util.Locale

object TimeFormatter {

    fun formatTimeOnlyForSpeech(hour: Int, minute: Int): String {
        val minuteSpeech = when (minute) {
            0 -> ""
            1 -> " und 1 Minute"
            else -> " und $minute Minuten"
        }
        return "$hour Uhr$minuteSpeech"
    }

    fun formatSpeechWithPattern(pattern: String, hour: Int, minute: Int): String {
        val timeSpeech = formatTimeOnlyForSpeech(hour, minute)
        val safePattern = pattern.ifBlank { AnnouncementRule.DEFAULT_SPEECH_PATTERN }

        return when {
            safePattern.contains("{zeit}", ignoreCase = true) -> {
                safePattern.replace("{zeit}", timeSpeech, ignoreCase = true)
            }
            safePattern.contains("{uhrzeit}", ignoreCase = true) -> {
                safePattern.replace("{uhrzeit}", timeSpeech, ignoreCase = true)
            }
            else -> {
                "$safePattern $timeSpeech"
            }
        }
    }

    fun formatTimeForSpeech(hour: Int, minute: Int): String {
        return formatSpeechWithPattern(AnnouncementRule.DEFAULT_SPEECH_PATTERN, hour, minute)
    }

    fun formatCurrentTimeForSpeech(pattern: String = AnnouncementRule.DEFAULT_SPEECH_PATTERN): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return formatSpeechWithPattern(pattern, hour, minute)
    }

    fun formatTimeString(hour: Int, minute: Int): String {
        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
    }
}
