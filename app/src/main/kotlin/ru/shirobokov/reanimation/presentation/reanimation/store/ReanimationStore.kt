package ru.shirobokov.reanimation.presentation.reanimation.store

import ru.shirobokov.reanimation.R
import ru.shirobokov.reanimation.domain.ReanimationInteractor
import ru.shirobokov.reanimation.presentation.NewbornHelp
import ru.shirobokov.reanimation.presentation.reanimation.model.ReanimationModel

abstract class ReanimationStore(private val reanimationInteractor: ReanimationInteractor) {

    protected val startReanimationTime = System.currentTimeMillis()
    protected val helpList: MutableList<Pair<Int, Long>> = mutableListOf()

    abstract val defibrillationHelpRes: Int
    abstract val adrenalinInjectHelpRes: Int
    abstract val amiodaroneFirstInjectHelpInt: Int
    abstract val amiodaroneSecondInjectHelpInt: Int

    abstract val amiodaroneFirstAudioFile: String
    abstract val amiodaroneSecondAudioFile: String

    suspend fun onNewEvent(event: EventType, reanimationModel: ReanimationModel) =
        when (event) {
            EventType.NewSecond -> newSecondProcess(reanimationModel)
            EventType.ZmsCardClick -> zmsCardClick(reanimationModel)
            EventType.AdditionalData1Click -> additionalDataCard1Click(reanimationModel)
            EventType.AdditionalData2Click -> additionalDataCard2Click(reanimationModel)
            EventType.DoDefibrillationClick -> doDefibrillationClick(reanimationModel)
            EventType.InjectAdrenalinClick -> injectAdrenalinClick(reanimationModel)
            EventType.DoIntubationClick -> doIntubationClick(reanimationModel)
            EventType.ReanimationFinishClick -> reanimationFinishClick(reanimationModel)
            is EventType.SaveNewBornHelp -> saveNewBornHelp(event.helpType, reanimationModel)
        }

    abstract fun newSecondProcess(reanimationModel: ReanimationModel): ReanimationModel
    abstract fun zmsCardClick(reanimationModel: ReanimationModel): ReanimationModel
    abstract fun doDefibrillationClick(reanimationModel: ReanimationModel): ReanimationModel
    abstract fun saveNewBornHelp(helpType: NewbornHelp, reanimationModel: ReanimationModel): ReanimationModel
    abstract fun doIntubationClick(reanimationModel: ReanimationModel): ReanimationModel

    private fun additionalDataCard1Click(reanimationModel: ReanimationModel) = with(reanimationModel) {
        if (additionalDataCard2Color == R.drawable.bg_card_green) {
            copy(additionalDataCardsVisibility = false)
        } else {
            copy(additionalDataCard1Color = R.drawable.bg_card_green)
        }
    }

    private fun additionalDataCard2Click(reanimationModel: ReanimationModel) = with(reanimationModel) {
        if (additionalDataCard1Color == R.drawable.bg_card_green) {
            copy(additionalDataCardsVisibility = false)
        } else {
            copy(additionalDataCard2Color = R.drawable.bg_card_green)
        }
    }

    private fun injectAdrenalinClick(reanimationModel: ReanimationModel) = with(reanimationModel) {
        helpList.add(adrenalinInjectHelpRes to System.currentTimeMillis())
        adrenalinTimer = 0
        copy(
            adrenalinCardTimer = mapToViewTimer(adrenalinTimer),
            adrenalinCardColor = R.drawable.bg_card_green,
            adrenalinInjectCardColor = R.drawable.bg_card_green,
            isInjectAdrenalin = true
        )
    }

    private suspend fun reanimationFinishClick(reanimationModel: ReanimationModel): ReanimationModel {
        helpList.add(R.string.reanimation_help_finish to System.currentTimeMillis())
        reanimationInteractor.saveReanimationHelp(startReanimationTime, System.currentTimeMillis(), helpList)
        return reanimationModel
    }

    protected fun mapToViewTimer(value: Int): String {
        val minute = value / SECOND_IN_MINUTE
        val second = value % SECOND_IN_MINUTE
        val minuteString = if (minute < 10) "0$minute" else minute.toString()
        val secondString = if (second < 10) "0$second" else second.toString()
        return "$minuteString:$secondString"
    }

    companion object {
        private const val SECOND_IN_MINUTE = 60
    }
}

sealed class EventType {
    object NewSecond : EventType()
    object ZmsCardClick : EventType()
    object AdditionalData1Click : EventType()
    object AdditionalData2Click : EventType()
    object InjectAdrenalinClick : EventType()
    object DoDefibrillationClick : EventType()
    object DoIntubationClick : EventType()
    object ReanimationFinishClick : EventType()
    data class SaveNewBornHelp(val helpType: NewbornHelp) : EventType()
}

