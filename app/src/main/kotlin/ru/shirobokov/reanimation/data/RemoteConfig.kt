package ru.shirobokov.reanimation.data

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

class RemoteConfig() {

    private val defaults = hashMapOf<String, Any>(
        VIDEO_INSTRUCTION_URL_KEY to VIDEO_INSTRUCTION_URL_DEFAULT_VALUE
    )
    private val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    private val configSettings = FirebaseRemoteConfigSettings.Builder()
        .setMinimumFetchIntervalInSeconds(FETCH_INTERVAL)
        .build()

    fun fetch() {
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        firebaseRemoteConfig.setDefaultsAsync(defaults)
        firebaseRemoteConfig.fetchAndActivate()
    }

    fun getVideoInstructionUrl() = firebaseRemoteConfig.getString(VIDEO_INSTRUCTION_URL_KEY)

    companion object {
        private const val FETCH_INTERVAL = 3600L

        private const val VIDEO_INSTRUCTION_URL_KEY = "video_instruction_url"
        private const val VIDEO_INSTRUCTION_URL_DEFAULT_VALUE = ""
    }
}
