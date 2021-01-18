package ru.shirobokov.reanimation.domain

import ru.shirobokov.reanimation.data.RemoteConfig
import ru.shirobokov.reanimation.data.SharedPreferencesRepository

class ServicesDataInteractor(
    private val sharedPreferencesRepository: SharedPreferencesRepository,
    private val remoteConfig: RemoteConfig
) {

    var isWarningAgree: Boolean
        get() = sharedPreferencesRepository.isWarningAgree
        set(value) {
            sharedPreferencesRepository.isWarningAgree = value
        }

    fun fetchRemoteConfig() = remoteConfig.fetch()
    fun getVideoInstructionUrl() = remoteConfig.getVideoInstructionUrl()
}
