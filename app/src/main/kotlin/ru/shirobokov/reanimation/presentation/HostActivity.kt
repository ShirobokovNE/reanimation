package ru.shirobokov.reanimation.presentation

import android.media.*
import android.media.ToneGenerator.TONE_CDMA_CALLDROP_LITE
import android.media.ToneGenerator.TONE_PROP_ACK
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.android.billingclient.api.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.shirobokov.reanimation.R
import ru.shirobokov.reanimation.databinding.ActivityHostBinding
import ru.shirobokov.reanimation.presentation.reanimation.model.ReanimationModel
import ru.shirobokov.reanimation.presentation.reanimation.model.ReanimationModel.Companion.NONE_AUDIO_FILE

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class HostActivity : AppCompatActivity(R.layout.activity_host), Navigable by Navigator() {

    private val viewModel: HostViewModel by viewModel()
    private val binding: ActivityHostBinding by viewBinding()
    private var toneGenerator: ToneGenerator? = null
    private val audioPlayer = MediaPlayer()

    private var connectCount = 0
    val skuDetailsMap = hashMapOf<String, SkuDetails>()
    val billingClient by lazy {
        BillingClient.newBuilder(this)
            .setListener { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    Toast.makeText(this, R.string.thanks_text, Toast.LENGTH_SHORT).show()
                }
            }
            .enablePendingPurchases()
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectBilling()
        initNavigator(this, binding.fragmentContainer.id)

        if (!viewModel.isWarningAgree) {
            binding.warningLayout.isVisible = true
            binding.agreeButton.setOnClickListener {
                viewModel.isWarningAgree = true
                binding.warningLayout.isVisible = false
                startReanimation()
            }
        } else {
            startReanimation()
        }
    }

    private fun startReanimation() {
        try {
            if (toneGenerator == null) {
                toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME)
            }
        } catch (exception: Exception) {
            FirebaseCrashlytics.getInstance().recordException(exception)
        }

        navigateTo(MainFragment.newInstance(), NavigateBackBehavior.SaveTransaction)

        lifecycleScope.launchWhenCreated {
            viewModel.metronome.collect { tone ->
                when (tone) {
                    Metronome.NONE -> return@collect
                    Metronome.DEFAULT -> toneGenerator?.startTone(TONE_PROP_ACK, TONE_GENERATOR_DURATION)
                    Metronome.HIGH -> toneGenerator?.startTone(TONE_CDMA_CALLDROP_LITE, TONE_GENERATOR_DURATION)

                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.audioPlayer.collect { audioFile ->
                when (audioFile) {
                    NONE_AUDIO_FILE -> return@collect
                    else -> if (!audioPlayer.isPlaying || audioFile == ReanimationModel.BREATH_AUDIO_FILE) {
                        playAudio(audioFile)
                    }
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.audioPlayerCompletion.collect { completion ->
                when (completion) {
                    AudioPlayerCompletion.NONE -> {
                        audioPlayer.setOnCompletionListener { viewModel.audioPlayer.value = NONE_AUDIO_FILE }
                    }
                    AudioPlayerCompletion.LONG_DELAY_TIME -> {
                        audioPlayer.setOnCompletionListener {
                            viewModel.isLongDelayTime = true
                            viewModel.audioPlayer.value = NONE_AUDIO_FILE
                        }
                    }
                    AudioPlayerCompletion.PLAY_METRONOME -> {
                        audioPlayer.setOnCompletionListener {
                            viewModel.startDefaultMetronome()
                            viewModel.audioPlayer.value = NONE_AUDIO_FILE
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        navigateBack()
    }

    private fun playAudio(audioFile: String) {
        audioPlayer.reset()
        assets.openFd(audioFile).use { audioPlayer.setDataSource(it.fileDescriptor, it.startOffset, it.length) }
        audioPlayer.prepare()
        audioPlayer.start()
    }

    private fun connectBilling() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    lifecycleScope.launchWhenCreated {
                        val skuDetails = SkuDetailsParams.newBuilder()
                            .setSkusList(mutableListOf(DONATE_ID))
                            .setType(BillingClient.SkuType.INAPP)
                            .build()
                        billingClient.querySkuDetailsAsync(skuDetails) { billingResult, skuDetailsList ->
                            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                                skuDetailsList?.let {
                                    skuDetailsList.forEach { skuDetailsMap[it.sku] = it }
                                    billingClient.queryPurchases(BillingClient.SkuType.INAPP).purchasesList?.forEach {
                                        skuDetailsMap.remove(it.sku)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Suppress("ThrowableNotThrown")
            override fun onBillingServiceDisconnected() {
                connectCount++
                val error = RuntimeException("Нет соединения с биллингом. Повторное соединение, попытка: $connectCount")
                FirebaseCrashlytics.getInstance().recordException(error)
                if (connectCount < 3) connectBilling()
            }
        })
    }

    companion object {
        private const val TONE_GENERATOR_DURATION = 100

        const val DONATE_ID = "donate"
    }
}