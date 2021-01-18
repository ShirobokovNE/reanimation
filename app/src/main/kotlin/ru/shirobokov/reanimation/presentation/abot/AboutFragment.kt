package ru.shirobokov.reanimation.presentation.abot

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode.OK
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.shirobokov.reanimation.R
import ru.shirobokov.reanimation.databinding.FragmentAboutBinding

class AboutFragment : Fragment(R.layout.fragment_about) {

    private val binding: FragmentAboutBinding by viewBinding()
    private val viewModel: AboutViewModel by viewModel()

    private var connectCount = 0
    private val skuDetailsMap = hashMapOf<String, SkuDetails>()
    private val billingClient by lazy {
        BillingClient.newBuilder(requireActivity())
            .setListener { billingResult, purchases ->
                if (billingResult.responseCode == OK && purchases != null) {
                    Toast.makeText(requireActivity(), R.string.thanks_text, Toast.LENGTH_SHORT).show()
                }
            }
            .enablePendingPurchases()
            .build()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        connectBilling()

        binding.videoInstructionButton.isVisible = viewModel.videoInstructionUrl.isNotBlank()
        binding.videoInstructionDivider.isVisible = viewModel.videoInstructionUrl.isNotBlank()
        binding.videoInstructionButton.setOnClickListener {
            requireContext().startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(viewModel.videoInstructionUrl)))
        }
        val versionText = getString(
            R.string.version_text,
            requireContext().packageManager.getPackageInfo(requireContext().packageName, 0).versionName
        )
        binding.version.text = versionText
        binding.newButton.setOnClickListener {
            requireContext().startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(NEW_URL)))
        }
        binding.estimateButton.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(MARKET_URL)))
            } catch (exception: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(BROWSER_MARKET_URL)))
            }
        }
        binding.donateButton.setOnClickListener {
            skuDetailsMap[DONATE_ID]?.let { details ->
                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(details)
                    .build()
                billingClient.launchBillingFlow(requireActivity(), billingFlowParams)
            }
        }
    }

    private fun connectBilling() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == OK) {
                    lifecycleScope.launchWhenCreated {
                        val skuDetails = SkuDetailsParams.newBuilder()
                            .setSkusList(mutableListOf(DONATE_ID))
                            .setType(BillingClient.SkuType.INAPP)
                            .build()
                        withContext(Dispatchers.IO) {
                            billingClient.querySkuDetailsAsync(skuDetails) { billingResult, skuDetailsList ->
                                if (billingResult.responseCode == OK) {
                                    launch {
                                        skuDetailsList?.let {
                                            withContext(Dispatchers.Main) {
                                                binding.donateButton.isVisible = skuDetailsList.isNotEmpty()
                                                binding.donateDivider.isVisible = skuDetailsList.isNotEmpty()
                                                for (details in skuDetailsList) skuDetailsMap[details.sku] = details
                                            }
                                        }
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
        private const val NEW_URL = "https://www.youtube.com/c/ПроСМП/featured"
        private const val MARKET_URL = "market://details?id=ru.shirobokov.reanimation"
        private const val BROWSER_MARKET_URL = "https://play.google.com/store/apps/details?id=ru.shirobokov.reanimation"
        private const val DONATE_ID = "donate"

        fun newInstance() = AboutFragment()
    }
}
