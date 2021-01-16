package ru.shirobokov.reanimation.presentation.reanimation.store

import ru.shirobokov.reanimation.R
import ru.shirobokov.reanimation.domain.ReanimationInteractor
import ru.shirobokov.reanimation.presentation.reanimation.model.ReanimationModel
import ru.shirobokov.reanimation.presentation.reanimation.model.ReanimationModel.Companion.INJECT_AMIODARONE_5_AUDIO_FILE

class ChildPatientStore(
    reanimationInteractor: ReanimationInteractor,
) : AdultPatientStore(reanimationInteractor, R.string.reanimation_help_start_child) {

    override val defibrillationHelpRes = R.string.reanimation_help_do_defibrillation_child
    override val adrenalinInjectHelpRes = R.string.reanimation_help_inject_adrenalin_child
    override val amiodaroneFirstInjectHelpInt = R.string.reanimation_help_inject_amiodarone_child
    override val amiodaroneSecondInjectHelpInt = R.string.reanimation_help_inject_amiodarone_child

    override val amiodaroneFirstAudioFile = INJECT_AMIODARONE_5_AUDIO_FILE
    override val amiodaroneSecondAudioFile = INJECT_AMIODARONE_5_AUDIO_FILE

    override fun zmsCardClick(reanimationModel: ReanimationModel) = with(reanimationModel) {
        copy(zmsCardText = previousZmsCardText)
    }
}
