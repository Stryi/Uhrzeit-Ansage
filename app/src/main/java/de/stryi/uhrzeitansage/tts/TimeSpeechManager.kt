package de.stryi.uhrzeitansage.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import de.stryi.uhrzeitansage.util.SpeechVolumeHelper
import de.stryi.uhrzeitansage.util.TimeFormatter
import java.util.Locale

class TimeSpeechManager(context: Context) : TextToSpeech.OnInitListener {

    private val applicationContext = context.applicationContext
    private var tts: TextToSpeech? = TextToSpeech(applicationContext, this)
    private var isInitialized = false
    private var pendingSpeechText: String? = null
    private var pendingVolume: Float = 1.0f

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.GERMAN)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback zu Default-Sprache falls Deutsch nicht installiert ist
                tts?.language = Locale.getDefault()
            }
            isInitialized = true
            tts?.let { SpeechVolumeHelper.applyAlarmAudioAttributes(it) }
            pendingSpeechText?.let {
                speak(it, pendingVolume)
                pendingSpeechText = null
            }
        }
    }

    fun speak(text: String, volume: Float = 1.0f) {
        val clampedVolume = volume.coerceIn(0.0f, 1.0f)
        val activeTts = tts
        if (isInitialized && activeTts != null) {
            SpeechVolumeHelper.speakWithBoost(
                context = applicationContext,
                tts = activeTts,
                text = text,
                appVolume = clampedVolume,
                utteranceId = "uhrzeit_speech_${System.currentTimeMillis()}"
            )
        } else {
            pendingSpeechText = text
            pendingVolume = clampedVolume
        }
    }

    fun speakCurrentTime(volume: Float = 1.0f) {
        val speechText = TimeFormatter.formatCurrentTimeForSpeech()
        speak(speechText, volume)
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            tts = null
            isInitialized = false
        }
    }
}
