package de.stryi.uhrzeitansage.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlin.math.roundToInt

object SpeechVolumeHelper {

    fun applyAlarmAudioAttributes(tts: TextToSpeech) {
        try {
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
            tts.setAudioAttributes(attributes)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun speakWithBoost(
        context: Context,
        tts: TextToSpeech,
        text: String,
        appVolume: Float,
        utteranceId: String,
        onUtteranceDone: (() -> Unit)? = null
    ) {
        val audioManager = context.applicationContext.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        val streamType = AudioManager.STREAM_ALARM
        var previousVolume: Int? = null

        if (audioManager != null) {
            try {
                val maxVolume = audioManager.getStreamMaxVolume(streamType)
                val currentVolume = audioManager.getStreamVolume(streamType)
                previousVolume = currentVolume

                val clampedAppVol = appVolume.coerceIn(0.0f, 1.0f)
                val targetVolume = (maxVolume * clampedAppVol).roundToInt().coerceIn(0, maxVolume)

                if (targetVolume != currentVolume) {
                    audioManager.setStreamVolume(streamType, targetVolume, 0)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        var isRestored = false
        val restoreAction = {
            if (!isRestored) {
                isRestored = true
                previousVolume?.let { oldVol ->
                    try {
                        audioManager?.setStreamVolume(streamType, oldVol, 0)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                onUtteranceDone?.invoke()
            }
        }

        applyAlarmAudioAttributes(tts)

        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(uttId: String?) {}

            override fun onDone(uttId: String?) {
                restoreAction()
            }

            @Deprecated("Deprecated in Java")
            override fun onError(uttId: String?) {
                restoreAction()
            }
        })

        val params = Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
        }
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }
}
