package com.nexuspaths.game.game

import android.content.Context
import android.media.AudioAttributes
import android.media.ToneGenerator
import android.media.AudioManager
import android.os.VibrationEffect
import android.os.Vibrator
import kotlinx.coroutines.*

/**
 * Manages sound generation using ToneGenerator and vibration feedback
 */
class SoundManager(private val context: Context) {

    private var toneGenerator: ToneGenerator? = null
    private val vibrator: Vibrator? = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    private val soundScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private var soundEnabled = true
    private var vibrationEnabled = true

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 50)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Play sound when a node is selected
     */
    fun playSelectSound() {
        if (!soundEnabled) return
        soundScope.launch {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 50)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Play sound when a match is made
     */
    fun playMatchSound(nodeCount: Int, pattern: MatchPattern) {
        if (!soundEnabled) return

        soundScope.launch {
            try {
                // Different tones based on match size and pattern
                when {
                    pattern == MatchPattern.STAR -> {
                        playMelody(listOf(
                            ToneGenerator.TONE_PROP_ACK to 100,
                            ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_NORMAL to 100,
                            ToneGenerator.TONE_PROP_ACK to 150
                        ))
                    }
                    pattern == MatchPattern.LINE -> {
                        playMelody(listOf(
                            ToneGenerator.TONE_PROP_BEEP to 80,
                            ToneGenerator.TONE_PROP_BEEP2 to 120
                        ))
                    }
                    nodeCount >= 5 -> {
                        toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 150)
                    }
                    else -> {
                        toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 100)
                    }
                }

                // Vibration feedback
                if (vibrationEnabled && vibrator?.hasVibrator() == true) {
                    val intensity = when {
                        nodeCount >= 7 -> 100L
                        nodeCount >= 5 -> 50L
                        else -> 30L
                    }
                    vibrator.vibrate(VibrationEffect.createOneShot(intensity, VibrationEffect.DEFAULT_AMPLITUDE))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Play sound when ability is used
     */
    fun playAbilitySound() {
        if (!soundEnabled) return

        soundScope.launch {
            try {
                playMelody(listOf(
                    ToneGenerator.TONE_DTMF_1 to 80,
                    ToneGenerator.TONE_DTMF_4 to 80,
                    ToneGenerator.TONE_DTMF_8 to 150
                ))

                if (vibrationEnabled && vibrator?.hasVibrator() == true) {
                    val pattern = longArrayOf(0, 50, 50, 100)
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Play sound when game is over
     */
    fun playGameOverSound() {
        if (!soundEnabled) return

        soundScope.launch {
            try {
                playMelody(listOf(
                    ToneGenerator.TONE_DTMF_8 to 150,
                    ToneGenerator.TONE_DTMF_4 to 150,
                    ToneGenerator.TONE_DTMF_1 to 300
                ))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Play a melody sequence
     */
    private suspend fun playMelody(notes: List<Pair<Int, Int>>) {
        notes.forEach { (tone, duration) ->
            toneGenerator?.startTone(tone, duration)
            delay(duration.toLong() + 50)
        }
    }

    /**
     * Play sound for UI actions
     */
    fun playUIClick() {
        if (!soundEnabled) return
        soundScope.launch {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 30)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Play achievement unlock sound
     */
    fun playAchievementSound() {
        if (!soundEnabled) return

        soundScope.launch {
            try {
                playMelody(listOf(
                    ToneGenerator.TONE_DTMF_1 to 100,
                    ToneGenerator.TONE_DTMF_5 to 100,
                    ToneGenerator.TONE_DTMF_9 to 200
                ))

                if (vibrationEnabled && vibrator?.hasVibrator() == true) {
                    val pattern = longArrayOf(0, 100, 100, 100, 100, 200)
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Toggle sound on/off
     */
    fun toggleSound(): Boolean {
        soundEnabled = !soundEnabled
        return soundEnabled
    }

    /**
     * Toggle vibration on/off
     */
    fun toggleVibration(): Boolean {
        vibrationEnabled = !vibrationEnabled
        return vibrationEnabled
    }

    /**
     * Check if sound is enabled
     */
    fun isSoundEnabled(): Boolean = soundEnabled

    /**
     * Check if vibration is enabled
     */
    fun isVibrationEnabled(): Boolean = vibrationEnabled

    /**
     * Release resources
     */
    fun release() {
        soundScope.cancel()
        toneGenerator?.release()
        toneGenerator = null
    }
}
