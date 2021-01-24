package ru.shirobokov.reanimation.presentation.abot

import androidx.lifecycle.ViewModel
import ru.shirobokov.reanimation.domain.ServicesDataInteractor

class AboutViewModel(servicesDataInteractor: ServicesDataInteractor): ViewModel() {

    val videoInstructionUrl = servicesDataInteractor.getVideoInstructionUrl()
}
