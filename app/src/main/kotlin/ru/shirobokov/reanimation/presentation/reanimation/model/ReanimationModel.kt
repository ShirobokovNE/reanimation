package ru.shirobokov.reanimation.presentation.reanimation.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ru.shirobokov.reanimation.R

data class ReanimationModel(
    /**viewState*/
    val zmsCardTimer: String = START_TIMER_TITLE,
    @StringRes val zmsCardText: Int = R.string.reanimation_zms_text,
    @StringRes val previousZmsCardText: Int = R.string.reanimation_zms_text,
    val soundIconVisibility: Boolean,

    @DrawableRes val adrenalinCardColor: Int = R.drawable.bg_card_green,
    val adrenalinCardTimer: String = START_TIMER_TITLE,

    val defibrillationCardVisibility: Boolean,
    @DrawableRes val defibrillationCardColor: Int = R.drawable.bg_card_yellow,
    val defibrillationCardTimer: String = START_TIMER_TITLE,

    val additionalDataCardsVisibility: Boolean = true,
    @StringRes val additionalDataCardTitle: Int,
    @DrawableRes val additionalDataCard1Color: Int = R.drawable.bg_card_red,
    @StringRes val additionalDataCard1Text: Int,
    @DrawableRes val additionalDataCard2Color: Int = R.drawable.bg_card_red,
    @StringRes val additionalDataCard2Text: Int,

    @DrawableRes val adrenalinInjectCardColor: Int = R.drawable.bg_card_red,
    @StringRes val adrenalinInjectCardSubtitle: Int,

    @DrawableRes val doDefibrillationCardColor: Int = R.drawable.bg_card_green,
    @StringRes val doDefibrillationCardPower: Int = R.string.reanimation_do_defibrillation_360,
    val doDefibrillationCardCount: Int = 0,

    val doIntubationCardVisibility: Boolean = true,

    /**audio*/
    val firstAudioFile: String = NONE_AUDIO_FILE,
    val secondAudioFile: String = NONE_AUDIO_FILE,
    val thirdAudioFile: String = NONE_AUDIO_FILE,
    val fourthAudioFile: String = NONE_AUDIO_FILE,

    /**flags*/
    val isDoDefibrillation: Boolean = false,
    val isInjectAdrenalin: Boolean = false,
    val isAmiodaroneAudioFilePlayed: Boolean = false,

    /**timer*/
    var zmsTimer: Int = 0,
    var adrenalinTimer: Int = 0,
    var defibrillationTimer: Int = 0
) {

    companion object {
        private const val START_TIMER_TITLE = "00:00"

        const val NONE_AUDIO_FILE = ""
        const val CHOOSE_AGE_AUDIO_FILE = "choose_age.mp3"
        const val TWO_BREATH_AUDIO_FILE = "two_breath.mp3"
        const val BREATH_AUDIO_FILE = "breath.mp3"
        const val PREPARE_ADRENALINE_AUDIO_FILE = "prepare_adrenaline.mp3"
        const val INJECT_ADRENALINE_AUDIO_FILE = "inject_adrenaline.mp3"
        const val PREPARE_DEFIBRILLATION_AUDIO_FILE = "prepare_defibrillation.mp3"
        const val DO_DEFIBRILLATION_IF_NEED_AUDIO_FILE = "do_defibrillation_if_need.mp3"
        const val INJECT_AMIODARONE_300_AUDIO_FILE = "inject_amiodarone_300.mp3"
        const val INJECT_AMIODARONE_150_AUDIO_FILE = "inject_amiodarone_150.mp3"
        const val INJECT_AMIODARONE_5_AUDIO_FILE = "inject_amiodarone_5.mp3"
        const val EVALUATION_RHYTHM_AUDIO_FILE = "evaluation_rhythm.mp3"
        const val TEEN_MINUTE_AUDIO_FILE = "10_minute.mp3"
        const val THIRTY_MINUTE_AUDIO_FILE = "30_minute.mp3"

        fun getAdultPatient() = ReanimationModel(
            soundIconVisibility = true,
            defibrillationCardVisibility = true,
            additionalDataCardTitle = R.string.reanimation_death_cause_title,
            additionalDataCard1Text = R.string.reanimation_death_cause_description1,
            additionalDataCard2Text = R.string.reanimation_death_cause_description2,
            adrenalinInjectCardSubtitle = R.string.reanimation_adrenalin_inject_description_adult,
        )

        fun getChildPatient() = ReanimationModel(
            soundIconVisibility = false,
            defibrillationCardVisibility = true,
            additionalDataCardTitle = R.string.reanimation_death_cause_title,
            additionalDataCard1Text = R.string.reanimation_death_cause_description1,
            additionalDataCard2Text = R.string.reanimation_death_cause_description2,
            adrenalinInjectCardSubtitle = R.string.reanimation_adrenalin_inject_description_child,
            doDefibrillationCardPower = R.string.reanimation_do_defibrillation_4
        )

        fun getNewbornPatient() = ReanimationModel(
            soundIconVisibility = false,
            defibrillationCardVisibility = false,
            additionalDataCardTitle = R.string.reanimation_additional_manipulation_title,
            additionalDataCard1Text = R.string.reanimation_additional_manipulation_card1,
            additionalDataCard2Text = R.string.reanimation_additional_manipulation_card2,
            adrenalinInjectCardSubtitle = R.string.reanimation_adrenalin_inject_description_child,
            doDefibrillationCardPower = R.string.reanimation_do_defibrillation_4,
            doIntubationCardVisibility = false
        )
    }
}
