package de.stryi.uhrzeitansage.data

import android.content.Context
import android.content.SharedPreferences
import de.stryi.uhrzeitansage.model.AnnouncementRule
import org.json.JSONArray
import org.json.JSONObject

class RuleRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("uhrzeit_ansage_rules", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_RULES = "key_rules_list"
        private const val KEY_VOLUME = "key_speech_volume"
    }

    fun getVolume(): Float {
        return prefs.getFloat(KEY_VOLUME, 0.5f)
    }

    fun setVolume(volume: Float) {
        val clamped = volume.coerceIn(0.0f, 1.0f)
        prefs.edit().putFloat(KEY_VOLUME, clamped).apply()
    }

    fun getRules(): List<AnnouncementRule> {
        val jsonString = prefs.getString(KEY_RULES, null) ?: return defaultRules()
        if (jsonString.isBlank()) return defaultRules()

        return try {
            val jsonArray = JSONArray(jsonString)
            val rules = mutableListOf<AnnouncementRule>()
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                rules.add(jsonObjectToRule(jsonObject))
            }
            rules
        } catch (e: Exception) {
            e.printStackTrace()
            defaultRules()
        }
    }

    fun saveRules(rules: List<AnnouncementRule>) {
        val jsonArray = JSONArray()
        for (rule in rules) {
            jsonArray.put(ruleToJsonObject(rule))
        }
        prefs.edit().putString(KEY_RULES, jsonArray.toString()).apply()
    }

    fun saveRule(rule: AnnouncementRule) {
        val currentRules = getRules().toMutableList()
        val index = currentRules.indexOfFirst { it.id == rule.id }
        if (index >= 0) {
            currentRules[index] = rule
        } else {
            currentRules.add(rule)
        }
        saveRules(currentRules)
    }

    fun deleteRule(id: String) {
        val currentRules = getRules().filter { it.id != id }
        saveRules(currentRules)
    }

    fun toggleRule(id: String) {
        val currentRules = getRules().map {
            if (it.id == id) it.copy(enabled = !it.enabled) else it
        }
        saveRules(currentRules)
    }

    private fun defaultRules(): List<AnnouncementRule> {
        return listOf(
            AnnouncementRule(
                title = "Standard Ansage",
                enabled = true,
                daysOfWeek = setOf(1, 2, 3, 4, 5, 6, 7), // Mo - So
                startHour = 7,
                startMinute = 0,
                endHour = 22,
                endMinute = 0,
                intervalMinutes = 15
            )
        )
    }

    private fun ruleToJsonObject(rule: AnnouncementRule): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("id", rule.id)
        jsonObject.put("title", rule.title)
        jsonObject.put("enabled", rule.enabled)

        val daysArray = JSONArray()
        rule.daysOfWeek.forEach { daysArray.put(it) }
        jsonObject.put("daysOfWeek", daysArray)

        jsonObject.put("startHour", rule.startHour)
        jsonObject.put("startMinute", rule.startMinute)
        jsonObject.put("endHour", rule.endHour)
        jsonObject.put("endMinute", rule.endMinute)
        jsonObject.put("intervalMinutes", rule.intervalMinutes)
        jsonObject.put("speechPattern", rule.speechPattern)

        return jsonObject
    }

    private fun jsonObjectToRule(jsonObject: JSONObject): JSONObjectToRuleResult {
        val id = jsonObject.optString("id")
        val title = jsonObject.optString("title", "")
        val enabled = jsonObject.optBoolean("enabled", true)

        val daysSet = mutableSetOf<Int>()
        val daysArray = jsonObject.optJSONArray("daysOfWeek")
        if (daysArray != null) {
            for (i in 0 until daysArray.length()) {
                daysSet.add(daysArray.getInt(i))
            }
        } else {
            daysSet.addAll(listOf(1, 2, 3, 4, 5, 6, 7))
        }

        val startHour = jsonObject.optInt("startHour", 8)
        val startMinute = jsonObject.optInt("startMinute", 0)
        val endHour = jsonObject.optInt("endHour", 20)
        val endMinute = jsonObject.optInt("endMinute", 0)
        val intervalMinutes = jsonObject.optInt("intervalMinutes", 15)
        val speechPattern = jsonObject.optString("speechPattern", AnnouncementRule.DEFAULT_SPEECH_PATTERN)

        return AnnouncementRule(
            id = id,
            title = title,
            enabled = enabled,
            daysOfWeek = daysSet,
            startHour = startHour,
            startMinute = startMinute,
            endHour = endHour,
            endMinute = endMinute,
            intervalMinutes = intervalMinutes,
            speechPattern = speechPattern
        )
    }
}
typealias JSONObjectToRuleResult = AnnouncementRule
