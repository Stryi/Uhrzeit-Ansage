package de.stryi.uhrzeitansage

import de.stryi.uhrzeitansage.model.AnnouncementRule
import de.stryi.uhrzeitansage.scheduler.AlarmScheduler
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AlarmSchedulerTest {

    @Test
    fun testIsRuleMatching_standardInterval() {
        val rule = AnnouncementRule(
            enabled = true,
            daysOfWeek = setOf(1, 3, 5),
            startHour = 7,
            startMinute = 0,
            endHour = 10,
            endMinute = 0,
            intervalMinutes = 15
        )

        assertTrue(AlarmScheduler.isRuleMatching(rule, dayOfWeek = 1, hour = 7, minute = 0))
        assertTrue(AlarmScheduler.isRuleMatching(rule, dayOfWeek = 1, hour = 7, minute = 15))
        assertFalse(AlarmScheduler.isRuleMatching(rule, dayOfWeek = 1, hour = 7, minute = 10))
        assertFalse(AlarmScheduler.isRuleMatching(rule, dayOfWeek = 2, hour = 7, minute = 15))
        assertTrue(AlarmScheduler.isRuleMatching(rule, dayOfWeek = 3, hour = 10, minute = 0))
        assertFalse(AlarmScheduler.isRuleMatching(rule, dayOfWeek = 3, hour = 10, minute = 1))
    }

    @Test
    fun testIsRuleMatching_overnightRange() {
        val rule = AnnouncementRule(
            enabled = true,
            daysOfWeek = setOf(1),
            startHour = 23,
            startMinute = 0,
            endHour = 1,
            endMinute = 0,
            intervalMinutes = 30
        )

        assertTrue(AlarmScheduler.isRuleMatching(rule, dayOfWeek = 1, hour = 23, minute = 0))
        assertTrue(AlarmScheduler.isRuleMatching(rule, dayOfWeek = 1, hour = 23, minute = 30))
        assertTrue(AlarmScheduler.isRuleMatching(rule, dayOfWeek = 1, hour = 0, minute = 0))
        assertTrue(AlarmScheduler.isRuleMatching(rule, dayOfWeek = 1, hour = 0, minute = 30))
        assertTrue(AlarmScheduler.isRuleMatching(rule, dayOfWeek = 1, hour = 1, minute = 0))
        assertFalse(AlarmScheduler.isRuleMatching(rule, dayOfWeek = 1, hour = 1, minute = 1))
    }

    @Test
    fun testIsRuleMatching_disabledRule() {
        val rule = AnnouncementRule(
            enabled = false,
            daysOfWeek = setOf(1),
            startHour = 7,
            startMinute = 0,
            endHour = 10,
            endMinute = 0,
            intervalMinutes = 15
        )

        assertFalse(AlarmScheduler.isRuleMatching(rule, dayOfWeek = 1, hour = 7, minute = 15))
    }

    @Test
    fun testMultipleMatchingRulesAtSameTime() {
        val rule1 = AnnouncementRule(
            enabled = true,
            daysOfWeek = setOf(1),
            startHour = 8,
            startMinute = 0,
            endHour = 8,
            endMinute = 0,
            speechPattern = "Termin 1! Es ist {zeit}"
        )
        val rule2 = AnnouncementRule(
            enabled = true,
            daysOfWeek = setOf(1),
            startHour = 8,
            startMinute = 0,
            endHour = 8,
            endMinute = 0,
            speechPattern = "Termin 2! Es ist {zeit}"
        )

        val activeRules = listOf(rule1, rule2)
        val matchingRules = activeRules.filter { rule ->
            AlarmScheduler.isRuleMatching(rule, dayOfWeek = 1, hour = 8, minute = 0)
        }

        org.junit.Assert.assertEquals(2, matchingRules.size)

        val speechTexts = matchingRules.map { rule ->
            de.stryi.uhrzeitansage.util.TimeFormatter.formatSpeechWithPattern(rule.speechPattern, 8, 0)
        }.distinct()

        val joinedText = speechTexts.joinToString(separator = ". ")
        org.junit.Assert.assertEquals("Termin 1! Es ist 8 Uhr. Termin 2! Es ist 8 Uhr", joinedText)
    }
}
