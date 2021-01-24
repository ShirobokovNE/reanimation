package ru.shirobokov.reanimation.presentation.abot

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.android.billingclient.api.BillingFlowParams
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.shirobokov.reanimation.R
import ru.shirobokov.reanimation.databinding.FragmentAboutBinding
import ru.shirobokov.reanimation.presentation.HostActivity
import ru.shirobokov.reanimation.presentation.HostActivity.Companion.DONATE_ID

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class AboutFragment : Fragment(R.layout.fragment_about) {

    private val binding: FragmentAboutBinding by viewBinding()
    private val viewModel: AboutViewModel by viewModel()

    private val activity by lazy { requireActivity() as HostActivity }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        binding.donateDivider.isVisible = activity.skuDetailsMap.isNotEmpty()
        binding.donateButton.isVisible = activity.skuDetailsMap.isNotEmpty()
        binding.donateButton.setOnClickListener {
            activity.skuDetailsMap[DONATE_ID]?.let { details ->
                val billingFlowParams = BillingFlowParams.newBuilder().setSkuDetails(details).build()
                activity.billingClient.launchBillingFlow(requireActivity(), billingFlowParams)
            }
        }
    }

    companion object {
        private const val NEW_URL = "https://www.youtube.com/c/ПроСМП/featured"
        private const val MARKET_URL = "market://details?id=ru.shirobokov.reanimation"
        private const val BROWSER_MARKET_URL = "https://play.google.com/store/apps/details?id=ru.shirobokov.reanimation"

        fun newInstance() = AboutFragment()
    }
}
