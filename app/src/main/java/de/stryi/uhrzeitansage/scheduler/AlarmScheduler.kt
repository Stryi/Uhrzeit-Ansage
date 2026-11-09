package de.stryi.uhrzeitansage.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import de.stryi.uhrzeitansage.data.RuleRepository
import de.stryi.uhrzeitansage.model.AnnouncementRule
import de.stryi.uhrzeitansage.model.DayOfWeekInfo
import java.util.Calendar

object AlarmScheduler {

    private const val REQUEST_CODE_ALARM = 1001

    fun scheduleNextAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val pendingIntent = createPendingIntent(context)

        val repository = RuleRepository(context)
        val activeRules = repository.getRules().filter { it.enabled && it.daysOfWeek.isNotEmpty() }

        if (activeRules.isEmpty()) {
            alarmManager.cancel(pendingIntent)
            return
        }

        val nextTriggerMillis = findNextTriggerTimeMillis(activeRules)
        if (nextTriggerMillis == null) {
            alarmManager.cancel(pendingIntent)
            return
        }

        scheduleExactAlarm(context, alarmManager, nextTriggerMillis, pendingIntent)
    }

    private fun scheduleExactAlarm(
        context: Context,
        alarmManager: AlarmManager,
        triggerAtMillis: Long,
        pendingIntent: PendingIntent
    ) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerAtMillis, pendingIntent)
                    alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                }
            } else {
                val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerAtMillis, pendingIntent)
                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
            try {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun findNextTriggerTimeMillis(activeRules: List<AnnouncementRule>): Long? {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.add(Calendar.MINUTE, 1)

        val maxMinutesToSearch = 7 * 24 * 60
        for (i in 0 until maxMinutesToSearch) {
            val dayOfWeek = DayOfWeekInfo.fromCalendarDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK))
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            if (isTimeMatchingAnyRule(activeRules, dayOfWeek, hour, minute)) {
                return calendar.timeInMillis
            }

            calendar.add(Calendar.MINUTE, 1)
        }

        return null
    }

    fun isTimeMatchingAnyRule(
        rules: List<AnnouncementRule>,
        dayOfWeek: Int,
        hour: Int,
        minute: Int
    ): Boolean {
        return rules.any { rule ->
            isRuleMatching(rule, dayOfWeek, hour, minute)
        }
    }

    fun isRuleMatching(
        rule: AnnouncementRule,
        dayOfWeek: Int,
        hour: Int,
        minute: Int
    ): Boolean {
        if (!rule.enabled) return false
        if (!rule.daysOfWeek.contains(dayOfWeek)) return false

        val currentMins = hour * 60 + minute
        val startMins = rule.startHour * 60 + rule.startMinute
        val endMins = rule.endHour * 60 + rule.endMinute

        val isOvernight = endMins < startMins

        val inTimeRange = if (!isOvernight) {
            currentMins in startMins..endMins
        } else {
            currentMins >= startMins || currentMins <= endMins
        }

        if (!inTimeRange) return false

        val offsetMins = if (!isOvernight) {
            currentMins - startMins
        } else {
            if (currentMins >= startMins) {
                currentMins - startMins
            } else {
                (currentMins + 24 * 60) - startMins
            }
        }

        return offsetMins % rule.intervalMinutes == 0
    }

    private fun createPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, AnnouncementReceiver::class.java)
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getBroadcast(context, REQUEST_CODE_ALARM, intent, flags)
    }
}
