package de.stryi.uhrzeitansage.scheduler

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.speech.tts.TextToSpeech
import de.stryi.uhrzeitansage.data.RuleRepository
import de.stryi.uhrzeitansage.model.DayOfWeekInfo
import de.stryi.uhrzeitansage.util.SpeechVolumeHelper
import de.stryi.uhrzeitansage.util.TimeFormatter
import java.util.Calendar
import java.util.Locale

class AnnouncementReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        val wakeLock = powerManager?.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "UhrzeitAnsage:AnnouncementWakeLock"
        )
        wakeLock?.acquire(15000)

        val calendar = Calendar.getInstance()
        val dayOfWeek = DayOfWeekInfo.fromCalendarDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK))
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val repository = RuleRepository(context)
        val activeRules = repository.getRules().filter { it.enabled }

        val shouldSpeak = AlarmScheduler.isTimeMatchingAnyRule(activeRules, dayOfWeek, hour, minute)

        if (shouldSpeak) {
            speakTimeAndFinish(context, hour, minute, dayOfWeek, wakeLock, pendingResult)
        } else {
            releaseWakeLockAndFinish(wakeLock, pendingResult)
        }

        AlarmScheduler.scheduleNextAlarm(context)
    }

    private fun speakTimeAndFinish(
        context: Context,
        hour: Int,
        minute: Int,
        dayOfWeek: Int,
        wakeLock: PowerManager.WakeLock?,
        pendingResult: PendingResult
    ) {
        val repository = RuleRepository(context)
        val activeRules = repository.getRules().filter { it.enabled }
        val matchingRules = activeRules.filter { rule ->
            AlarmScheduler.isRuleMatching(rule, dayOfWeek, hour, minute)
        }

        if (matchingRules.isEmpty()) {
            releaseWakeLockAndFinish(wakeLock, pendingResult)
            return
        }

        val speechTexts = matchingRules.map { rule ->
            TimeFormatter.formatSpeechWithPattern(rule.speechPattern, hour, minute)
        }.distinct()

        val textToSpeak = speechTexts.joinToString(separator = ". ")

        val volume = repository.getVolume()
        var tts: TextToSpeech? = null

        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.setLanguage(Locale.GERMAN)
                val activeTts = tts
                if (activeTts != null) {
                    SpeechVolumeHelper.speakWithBoost(
                        context = context,
                        tts = activeTts,
                        text = textToSpeak,
                        appVolume = volume,
                        utteranceId = "announcement_utt_id",
                        onUtteranceDone = {
                            cleanup(activeTts, wakeLock, pendingResult)
                        }
                    )
                } else {
                    cleanup(tts, wakeLock, pendingResult)
                }
            } else {
                cleanup(tts, wakeLock, pendingResult)
            }
        }
    }

    private fun releaseWakeLockAndFinish(
        wakeLock: PowerManager.WakeLock?,
        pendingResult: PendingResult
    ) {
        if (wakeLock != null && wakeLock.isHeld) {
            try {
                wakeLock.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        pendingResult.finish()
    }

    private fun cleanup(
        tts: TextToSpeech?,
        wakeLock: PowerManager.WakeLock?,
        pendingResult: PendingResult
    ) {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        releaseWakeLockAndFinish(wakeLock, pendingResult)
    }
}
