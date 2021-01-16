package ru.shirobokov.reanimation.presentation.reanimation.store

import ru.shirobokov.reanimation.R
import ru.shirobokov.reanimation.domain.ReanimationInteractor
import ru.shirobokov.reanimation.presentation.NewbornHelp
import ru.shirobokov.reanimation.presentation.reanimation.model.ReanimationModel
import ru.shirobokov.reanimation.presentation.reanimation.model.ReanimationModel.Companion.NONE_AUDIO_FILE
import ru.shirobokov.reanimation.presentation.reanimation.model.ReanimationModel.Companion.THIRTY_MINUTE_AUDIO_FILE

open class AdultPatientStore(
    reanimationInteractor: ReanimationInteractor,
    startHelpRes: Int = R.string.reanimation_help_start_adult
) : ReanimationStore(reanimationInteractor) {

    private val doIntubationHelp = R.string.reanimation_help_do_intubation

    override val defibrillationHelpRes = R.string.reanimation_help_do_defibrillation_adult
    override val adrenalinInjectHelpRes = R.string.reanimation_help_inject_adrenalin_adult
    override val amiodaroneFirstInjectHelpInt = R.string.reanimation_help_inject_300_amiodarone_adult
    override val amiodaroneSecondInjectHelpInt = R.string.reanimation_help_inject_150_amiodarone_adult

    override val amiodaroneFirstAudioFile = ReanimationModel.INJECT_AMIODARONE_300_AUDIO_FILE
    override val amiodaroneSecondAudioFile = ReanimationModel.INJECT_AMIODARONE_150_AUDIO_FILE

    init {
        helpList.add(startHelpRes to startReanimationTime)
    }

    override fun saveNewBornHelp(helpType: NewbornHelp, reanimationModel: ReanimationModel) = reanimationModel

    override fun newSecondProcess(reanimationModel: ReanimationModel) = with(reanimationModel) {
        copy(
            zmsCardTimer = mapToViewTimer(zmsTimer++),
            adrenalinCardTimer = mapToViewTimer(adrenalinTimer++),
            adrenalinCardColor = when (adrenalinTimer) {
                in 0..180 -> R.drawable.bg_card_green
                in 180..240 -> R.drawable.bg_card_orange
                else -> R.drawable.bg_card_red
            },
            adrenalinInjectCardColor = when {
                isInjectAdrenalin && adrenalinTimer in 0..150 -> R.drawable.bg_card_green
                isInjectAdrenalin && adrenalinTimer in 150..180 -> R.drawable.bg_card_orange
                else -> R.drawable.bg_card_red
            },
            defibrillationCardTimer = mapToViewTimer(if (isDoDefibrillation) defibrillationTimer++ else 0),
            defibrillationCardColor = when {
                !isDoDefibrillation -> R.drawable.bg_card_yellow
                isDoDefibrillation && defibrillationTimer in 0..90 -> R.drawable.bg_card_green
                else -> R.drawable.bg_card_red
            },
            doDefibrillationCardColor = when {
                !isDoDefibrillation -> R.drawable.bg_card_green
                isDoDefibrillation && defibrillationTimer in 0..90 -> R.drawable.bg_card_green
                isDoDefibrillation && defibrillationTimer in 90..120 -> R.drawable.bg_card_orange
                isDoDefibrillation && defibrillationTimer in 120..180 -> R.drawable.bg_card_orange
                else -> R.drawable.bg_card_green
            },
            firstAudioFile = when {
                defibrillationTimer == 90 -> ReanimationModel.PREPARE_DEFIBRILLATION_AUDIO_FILE
                defibrillationTimer == 120 -> ReanimationModel.DO_DEFIBRILLATION_IF_NEED_AUDIO_FILE
                (!isDoDefibrillation && zmsTimer % 120 == 0) || (isDoDefibrillation && defibrillationTimer % 120 == 0) -> {
                    ReanimationModel.EVALUATION_RHYTHM_AUDIO_FILE
                }
                else -> NONE_AUDIO_FILE
            },
            secondAudioFile = when (adrenalinTimer) {
                120 -> ReanimationModel.PREPARE_ADRENALINE_AUDIO_FILE
                180, 240, 300 -> ReanimationModel.INJECT_ADRENALINE_AUDIO_FILE
                else -> NONE_AUDIO_FILE
            },
            thirdAudioFile = when {
                doDefibrillationCardCount == 3 && !isAmiodaroneAudioFilePlayed -> {
                    helpList.add(amiodaroneFirstInjectHelpInt to System.currentTimeMillis())
                    amiodaroneFirstAudioFile
                }
                doDefibrillationCardCount == 5 && !isAmiodaroneAudioFilePlayed -> {
                    helpList.add(amiodaroneSecondInjectHelpInt to System.currentTimeMillis())
                    amiodaroneSecondAudioFile
                }
                else -> NONE_AUDIO_FILE
            },
            fourthAudioFile = if (zmsTimer == 1800) THIRTY_MINUTE_AUDIO_FILE else NONE_AUDIO_FILE,
            isAmiodaroneAudioFilePlayed = doDefibrillationCardCount == 3 || doDefibrillationCardCount == 5
        )
    }

    override fun zmsCardClick(reanimationModel: ReanimationModel) = with(reanimationModel) {
        copy(
            zmsCardText = when (zmsCardText) {
                R.string.reanimation_zms_text, R.string.reanimation_ivl_text -> R.string.reanimation_lukas_text
                else -> previousZmsCardText
            }
        )
    }

    override fun doDefibrillationClick(reanimationModel: ReanimationModel) = with(reanimationModel) {
        helpList.add(defibrillationHelpRes to System.currentTimeMillis())
        defibrillationTimer = 0
        copy(
            defibrillationCardTimer = mapToViewTimer(defibrillationTimer),
            doDefibrillationCardCount = doDefibrillationCardCount + 1,
            defibrillationCardColor = R.drawable.bg_card_green,
            doDefibrillationCardColor = R.drawable.bg_card_green,
            isDoDefibrillation = true
        )
    }

    override fun doIntubationClick(reanimationModel: ReanimationModel) = with(reanimationModel) {
        helpList.add(doIntubationHelp to System.currentTimeMillis())
        copy(
            zmsCardText = R.string.reanimation_ivl_text,
            previousZmsCardText = R.string.reanimation_ivl_text,
            doIntubationCardVisibility = false
        )
    }
}
