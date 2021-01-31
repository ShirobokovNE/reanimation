package ru.shirobokov.reanimation.presentation.reanimation.store

import ru.shirobokov.reanimation.R
import ru.shirobokov.reanimation.domain.ReanimationInteractor
import ru.shirobokov.reanimation.presentation.NewbornHelp
import ru.shirobokov.reanimation.presentation.reanimation.model.ReanimationModel
import ru.shirobokov.reanimation.presentation.reanimation.model.ReanimationModel.Companion.EVALUATION_RHYTHM_AUDIO_FILE
import ru.shirobokov.reanimation.presentation.reanimation.model.ReanimationModel.Companion.NONE_AUDIO_FILE

class NewbornPatientStore(interactor: ReanimationInteractor) : ReanimationStore(interactor) {

    override val defibrillationHelpRes = R.string.reanimation_help_do_defibrillation_child
    override val adrenalinInjectHelpRes = R.string.reanimation_help_inject_adrenalin_child
    override val amiodaroneFirstInjectHelpInt = 0
    override val amiodaroneSecondInjectHelpInt = 0

    override val amiodaroneFirstAudioFile = ""
    override val amiodaroneSecondAudioFile = ""

    override fun saveNewBornHelp(helpType: NewbornHelp, reanimationModel: ReanimationModel): ReanimationModel {
        when (helpType) {
            NewbornHelp.EVALUATE_RHYTHM -> {
                helpList.add(R.string.reanimation_help_evaluate_rhythm to System.currentTimeMillis())
            }
            NewbornHelp.FIVE_BREATH -> {
                helpList.add(R.string.reanimation_help_five_breath to System.currentTimeMillis())
            }
            NewbornHelp.FIVE_BREATH_AND_START_ZMS -> {
                helpList.add(R.string.reanimation_help_five_breath_and_start_zms to System.currentTimeMillis())
            }
            else -> Unit
        }
        return reanimationModel
    }

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
            firstAudioFile = when (zmsTimer % 60) {
                0 -> EVALUATION_RHYTHM_AUDIO_FILE
                else -> NONE_AUDIO_FILE
            },
            secondAudioFile = when (adrenalinTimer) {
                120 -> ReanimationModel.PREPARE_ADRENALINE_AUDIO_FILE
                180, 240, 300 -> ReanimationModel.INJECT_ADRENALINE_AUDIO_FILE
                else -> NONE_AUDIO_FILE
            },
            thirdAudioFile = if (zmsTimer == 600) ReanimationModel.TEEN_MINUTE_AUDIO_FILE else NONE_AUDIO_FILE
        )
    }

    override fun zmsCardClick(reanimationModel: ReanimationModel): ReanimationModel = reanimationModel
    override fun doDefibrillationClick(reanimationModel: ReanimationModel): ReanimationModel = reanimationModel
    override fun doIntubationClick(reanimationModel: ReanimationModel): ReanimationModel = reanimationModel
}
