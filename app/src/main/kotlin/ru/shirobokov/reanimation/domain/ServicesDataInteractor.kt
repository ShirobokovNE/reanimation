package ru.shirobokov.reanimation.domain

import ru.shirobokov.reanimation.data.SharedPreferencesRepository

class ServicesDataInteractor(
    private val sharedPreferencesRepository: SharedPreferencesRepository
) {

    var isWarningAgree: Boolean
        get() = sharedPreferencesRepository.isWarningAgree
        set(value) {
            sharedPreferencesRepository.isWarningAgree = value
        }
}
