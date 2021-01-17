package ru.shirobokov.reanimation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.shirobokov.reanimation.domain.ServicesDataInteractor
import ru.shirobokov.reanimation.presentation.reanimation.model.ReanimationModel.Companion.BREATH_AUDIO_FILE
import ru.shirobokov.reanimation.presentation.reanimation.model.ReanimationModel.Companion.CHOOSE_AGE_AUDIO_FILE
import ru.shirobokov.reanimation.presentation.reanimation.model.ReanimationModel.Companion.DO_DEFIBRILLATION_IF_NEED_AUDIO_FILE
import ru.shirobokov.reanimation.presentation.reanimation.model.ReanimationModel.Companion.EVALUATION_RHYTHM_AUDIO_FILE
import ru.shirobokov.reanimation.presentation.reanimation.model.ReanimationModel.Companion.NONE_AUDIO_FILE
import ru.shirobokov.reanimation.presentation.reanimation.model.ReanimationModel.Companion.TWO_BREATH_AUDIO_FILE

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class HostViewModel(
    private val servicesDataInteractor: ServicesDataInteractor
) : ViewModel() {

    private var metronomeJob: Job? = null
    private val metronomeDelayTime = (MILLISECOND_IN_MINUTE / METRONOME_RATE).toLong()

    val metronome = MutableStateFlow(Metronome.NONE)
    val audioPlayer = MutableStateFlow(NONE_AUDIO_FILE)
    val audioPlayerCompletion = MutableStateFlow(AudioPlayerCompletion.NONE)
    val newbornHelp = MutableStateFlow(NewbornHelp.NONE)

    val audioQueue = linkedSetOf(CHOOSE_AGE_AUDIO_FILE)
    var isLongDelayTime = false
    var metronomeType = MetronomeType.ZMS_ADULT
        set(value) {
            field = value
            when (value) {
                MetronomeType.ZMS_ADULT -> startDefaultMetronome()
                MetronomeType.ZMS_CHILD -> startChildMetronome()
                MetronomeType.ZMS_NEWBORN -> startNewbornMetronome()
                MetronomeType.IVL -> startIvlMetronome()
            }
        }

    var isWarningAgree
        get() = servicesDataInteractor.isWarningAgree
        set(value) {
            servicesDataInteractor.isWarningAgree = value
        }

    init {
        initAudioQueue()
    }

    private fun initAudioQueue() {
        viewModelScope.launch {
            ticker(delayMillis = 1000, initialDelayMillis = 0).consumeEach {
                if (audioQueue.isNotEmpty() && audioPlayer.value == NONE_AUDIO_FILE && metronome.value != Metronome.HIGH) {
                    audioQueue.first().let { audioFile ->
                        audioQueue.remove(audioFile)
                        audioPlayer.value = audioFile
                        when {
                            audioFile == CHOOSE_AGE_AUDIO_FILE -> {
                                audioPlayerCompletion.value = AudioPlayerCompletion.PLAY_METRONOME
                            }
                            audioFile == DO_DEFIBRILLATION_IF_NEED_AUDIO_FILE && metronomeJob?.isActive == true -> {
                                audioPlayerCompletion.value = AudioPlayerCompletion.LONG_DELAY_TIME
                            }
                            audioFile == EVALUATION_RHYTHM_AUDIO_FILE && metronomeJob?.isActive == true -> {
                                audioPlayerCompletion.value = AudioPlayerCompletion.LONG_DELAY_TIME
                            }
                        }
                    }
                }
            }
        }
    }

    fun startDefaultMetronome() {
        if (metronomeJob?.isActive == true) return
        metronomeJob = viewModelScope.launch {
            var metronomeIndex = 0
            while (isActive) {
                metronomeIndex++
                val index = metronomeIndex % 30
                when {
                    isLongDelayTime -> {
                        isLongDelayTime = false
                        audioPlayerCompletion.value = AudioPlayerCompletion.NONE
                        metronomeIndex -= index
                        delay(LONG_DELAY_TIME)
                    }
                    index in 1..25 -> {
                        metronome.updateState(Metronome.DEFAULT)
                        delay(metronomeDelayTime)
                    }
                    index in 25..29 -> {
                        metronome.updateState(Metronome.HIGH)
                        delay(metronomeDelayTime)
                    }
                    else -> {
                        metronome.updateState(Metronome.HIGH)
                        if (metronomeIndex < 120) audioPlayer.value = TWO_BREATH_AUDIO_FILE
                        delay(MIDDLE_DELAY_TIME)
                    }
                }
            }
        }
    }

    private fun startChildMetronome() {
        stopMetronome()
        metronomeJob = viewModelScope.launch {
            var metronomeIndex = 0
            while (isActive) {
                metronomeIndex++
                val index = metronomeIndex % 15
                when {
                    isLongDelayTime -> {
                        isLongDelayTime = false
                        audioPlayerCompletion.value = AudioPlayerCompletion.NONE
                        metronomeIndex -= index
                        delay(LONG_DELAY_TIME)
                    }
                    index in 1..10 -> {
                        metronome.updateState(Metronome.DEFAULT)
                        delay(metronomeDelayTime)
                    }
                    index in 10..14 -> {
                        metronome.updateState(Metronome.HIGH)
                        delay(metronomeDelayTime)
                    }
                    else -> {
                        metronome.updateState(Metronome.HIGH)
                        if (metronomeIndex < 120) audioPlayer.value = TWO_BREATH_AUDIO_FILE
                        delay(MIDDLE_DELAY_TIME)
                    }
                }
            }
        }
    }

    private fun startNewbornMetronome() {
        stopMetronome()
        newbornHelp.value = NewbornHelp.EVALUATE_RHYTHM
        metronomeJob = viewModelScope.launch {
            var breathCount = 0
            while (isActive && breathCount < 10) {
                audioPlayer.value = BREATH_AUDIO_FILE
                delay(metronomeDelayTime * 2)
                breathCount++
                if (breathCount % 5 == 0) {
                    audioPlayer.value = EVALUATION_RHYTHM_AUDIO_FILE
                    newbornHelp.value = NewbornHelp.FIVE_BREATH
                    newbornHelp.value = NewbornHelp.NONE
                    delay(LONG_DELAY_TIME)
                }
            }

            var metronomeIndex = 0
            while (isActive) {
                metronomeIndex++
                val index = metronomeIndex % 3
                when {
                    isLongDelayTime -> {
                        isLongDelayTime = false
                        audioPlayerCompletion.value = AudioPlayerCompletion.NONE
                        metronomeIndex -= index
                        delay(LONG_DELAY_TIME)
                    }
                    index in 1..2 -> {
                        metronome.updateState(Metronome.DEFAULT)
                        delay(metronomeDelayTime)
                    }
                    else -> {
                        metronome.updateState(Metronome.DEFAULT)
                        if (metronomeIndex < 45) audioPlayer.value = BREATH_AUDIO_FILE
                        delay(metronomeDelayTime * 2)
                    }
                }
            }
        }
    }

    private fun startIvlMetronome() {
        stopMetronome()
        metronomeJob = viewModelScope.launch {
            while (isActive) {
                when {
                    isLongDelayTime -> {
                        isLongDelayTime = false
                        audioPlayerCompletion.value = AudioPlayerCompletion.NONE
                        delay(LONG_DELAY_TIME)
                    }
                    else -> {
                        metronome.updateState(Metronome.DEFAULT)
                        delay(metronomeDelayTime)
                    }
                }
            }
        }
    }

    fun stopMetronome() {
        audioPlayerCompletion.value = AudioPlayerCompletion.NONE
        metronomeJob?.cancel()
    }

    private fun MutableStateFlow<Metronome>.updateState(newState: Metronome) {
        value = Metronome.NONE
        value = newState
    }

    companion object {
        private const val MIDDLE_DELAY_TIME = 5000L
        private const val LONG_DELAY_TIME = 8000L
        private const val MILLISECOND_IN_MINUTE = 60000.0
        private const val METRONOME_RATE = 100
    }
}

enum class Metronome {
    DEFAULT, HIGH, NONE
}

enum class MetronomeType {
    ZMS_ADULT, ZMS_NEWBORN, ZMS_CHILD, IVL
}

enum class AudioPlayerCompletion {
    LONG_DELAY_TIME, PLAY_METRONOME, NONE
}

enum class NewbornHelp {
    EVALUATE_RHYTHM, FIVE_BREATH, NONE
}
