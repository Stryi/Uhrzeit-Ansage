package de.stryi.uhrzeitansage

import de.stryi.uhrzeitansage.util.TimeFormatter
import org.junit.Assert.assertEquals
import org.junit.Test

class TimeFormatterTest {

    @Test
    fun testFormatTimeForSpeech_fullHour() {
        val result = TimeFormatter.formatTimeForSpeech(7, 0)
        assertEquals("Es ist 7 Uhr", result)
    }

    @Test
    fun testFormatTimeForSpeech_oneMinute() {
        val result = TimeFormatter.formatTimeForSpeech(7, 1)
        assertEquals("Es ist 7 Uhr und 1 Minute", result)
    }

    @Test
    fun testFormatTimeForSpeech_fifteenMinutes() {
        val result = TimeFormatter.formatTimeForSpeech(7, 15)
        assertEquals("Es ist 7 Uhr und 15 Minuten", result)
    }

    @Test
    fun testFormatTimeString() {
        assertEquals("07:05", TimeFormatter.formatTimeString(7, 5))
        assertEquals("14:30", TimeFormatter.formatTimeString(14, 30))
    }

    @Test
    fun testVolumeClamping() {
        assertEquals(0.0f, (-0.5f).coerceIn(0.0f, 1.0f), 0.001f)
        assertEquals(1.0f, 1.5f.coerceIn(0.0f, 1.0f), 0.001f)
        assertEquals(0.8f, 0.8f.coerceIn(0.0f, 1.0f), 0.001f)
        assertEquals(80, (0.8f * 100).toInt())
    }

    @Test
    fun testFormatSpeechWithPattern_customPattern() {
        val result = TimeFormatter.formatSpeechWithPattern("Achtung, es ist {zeit}.", 7, 15)
        assertEquals("Achtung, es ist 7 Uhr und 15 Minuten.", result)
    }

    @Test
    fun testFormatSpeechWithPattern_uhrzeitPlaceholder() {
        val result = TimeFormatter.formatSpeechWithPattern("Uhrzeit: {uhrzeit}", 12, 0)
        assertEquals("Uhrzeit: 12 Uhr", result)
    }

    @Test
    fun testFormatSpeechWithPattern_noPlaceholder() {
        val result = TimeFormatter.formatSpeechWithPattern("Pause um", 10, 30)
        assertEquals("Pause um 10 Uhr und 30 Minuten", result)
    }
}
