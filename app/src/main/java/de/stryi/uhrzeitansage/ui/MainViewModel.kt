package de.stryi.uhrzeitansage.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import de.stryi.uhrzeitansage.data.RuleRepository
import de.stryi.uhrzeitansage.model.AnnouncementRule
import de.stryi.uhrzeitansage.model.DayOfWeekInfo
import de.stryi.uhrzeitansage.scheduler.AlarmScheduler
import de.stryi.uhrzeitansage.tts.TimeSpeechManager
import de.stryi.uhrzeitansage.util.TimeFormatter
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Calendar

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RuleRepository(application)
    private val ttsManager = TimeSpeechManager(application)

    private val _rules = MutableStateFlow<List<AnnouncementRule>>(emptyList())
    val rules: StateFlow<List<AnnouncementRule>> = _rules.asStateFlow()

    private val _nextAnnouncementInfo = MutableStateFlow<String>("")
    val nextAnnouncementInfo: StateFlow<String> = _nextAnnouncementInfo.asStateFlow()

    private val _volume = MutableStateFlow<Float>(0.5f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    init {
        _volume.value = repository.getVolume()
        loadRules()
        startAutoRefresh()
    }

    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (isActive) {
                refreshNextAnnouncement()
                delay(5000) // Alle 5 Sekunden die nächste Ansage aktualisieren
            }
        }
    }

    fun setVolume(newVolume: Float) {
        val clamped = newVolume.coerceIn(0.0f, 1.0f)
        _volume.value = clamped
        repository.setVolume(clamped)
    }

    fun loadRules() {
        val loadedRules = repository.getRules()
        _rules.value = loadedRules
        updateNextAnnouncementInfo(loadedRules)
        AlarmScheduler.scheduleNextAlarm(getApplication())
    }

    fun saveRule(rule: AnnouncementRule) {
        repository.saveRule(rule)
        loadRules()
    }

    fun deleteRule(id: String) {
        repository.deleteRule(id)
        loadRules()
    }

    fun toggleRule(id: String) {
        repository.toggleRule(id)
        loadRules()
    }

    fun speakNextAnnouncementTest() {
        val textToSpeak = getNextAnnouncementTextToSpeak()
        ttsManager.speak(textToSpeak, _volume.value)
        refreshNextAnnouncement()
    }

    fun speakCurrentTime() {
        ttsManager.speakCurrentTime(_volume.value)
        refreshNextAnnouncement()
    }

    fun speakCustom(text: String) {
        ttsManager.speak(text, _volume.value)
    }

    fun getNextAnnouncementTextToSpeak(): String {
        val activeRules = _rules.value.filter { it.enabled && it.daysOfWeek.isNotEmpty() }
        if (activeRules.isEmpty()) {
            return TimeFormatter.formatCurrentTimeForSpeech()
        }

        val nextMillis = AlarmScheduler.findNextTriggerTimeMillis(activeRules)
            ?: return TimeFormatter.formatCurrentTimeForSpeech()

        val cal = Calendar.getInstance()
        cal.timeInMillis = nextMillis
        val dayOfWeek = DayOfWeekInfo.fromCalendarDayOfWeek(cal.get(Calendar.DAY_OF_WEEK))
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)

        val matchingRules = activeRules.filter { rule ->
            AlarmScheduler.isRuleMatching(rule, dayOfWeek, hour, minute)
        }

        if (matchingRules.isEmpty()) {
            return TimeFormatter.formatCurrentTimeForSpeech()
        }

        val speechTexts = matchingRules.map { rule ->
            TimeFormatter.formatSpeechWithPattern(rule.speechPattern, hour, minute)
        }.distinct()

        return speechTexts.joinToString(separator = ". ")
    }

    fun refreshNextAnnouncement() {
        updateNextAnnouncementInfo(_rules.value)
    }

    private fun updateNextAnnouncementInfo(rulesList: List<AnnouncementRule>) {
        val activeRules = rulesList.filter { it.enabled && it.daysOfWeek.isNotEmpty() }
        if (activeRules.isEmpty()) {
            _nextAnnouncementInfo.value = "Keine aktiven Ansagen"
            return
        }

        val nextMillis = AlarmScheduler.findNextTriggerTimeMillis(activeRules)
        if (nextMillis != null) {
            val cal = Calendar.getInstance()
            cal.timeInMillis = nextMillis
            val dayName = DayOfWeekInfo.getShortName(DayOfWeekInfo.fromCalendarDayOfWeek(cal.get(Calendar.DAY_OF_WEEK)))
            val timeStr = TimeFormatter.formatTimeString(
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE)
            )
            _nextAnnouncementInfo.value = "Nächste Ansage: $dayName, $timeStr Uhr"
        } else {
            _nextAnnouncementInfo.value = "Keine bevorstehende Ansage"
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}
