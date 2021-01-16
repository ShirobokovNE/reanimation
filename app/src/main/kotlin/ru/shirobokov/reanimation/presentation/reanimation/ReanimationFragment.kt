package ru.shirobokov.reanimation.presentation.reanimation

import android.os.Bundle
import android.view.View
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.shirobokov.reanimation.R
import ru.shirobokov.reanimation.databinding.FragmentReanimationBinding
import ru.shirobokov.reanimation.presentation.*
import ru.shirobokov.reanimation.presentation.helplist.HelpListFragment
import ru.shirobokov.reanimation.presentation.reanimation.model.ReanimationModel.Companion.NONE_AUDIO_FILE
import ru.shirobokov.reanimation.presentation.reanimation.store.EventType

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class ReanimationFragment : Fragment(R.layout.fragment_reanimation) {

    private val zmsType by lazy {
        MetronomeType.valueOf(requireArguments().getString(METRONOME_TYPE, MetronomeType.ZMS_ADULT.name))
    }

    private val viewModel: ReanimationViewModel by viewModel { parametersOf(zmsType) }
    private val hostViewModel: HostViewModel by sharedViewModel()
    private val binding: FragmentReanimationBinding by viewBinding()

    private var viewUpdateJob: Job? = null
    private var newbornHelpJob: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hostViewModel.metronomeType = zmsType

        viewUpdateJob = lifecycleScope.launch {
            viewModel.viewState.collect { state ->
                binding.zmsCardTimer.text = state.zmsCardTimer
                if (binding.zmsCardText.text != getString(state.zmsCardText)) {
                    when (state.zmsCardText) {
                        R.string.reanimation_zms_text -> {
                            binding.soundIcon.setMinAndMaxProgress(0.5f, 1f)
                            binding.soundIcon.playAnimation()
                            hostViewModel.metronomeType = zmsType
                        }
                        R.string.reanimation_ivl_text -> {
                            if (binding.zmsCardText.text == getString(R.string.reanimation_lukas_text)) {
                                binding.soundIcon.setMinAndMaxProgress(0.5f, 1f)
                                binding.soundIcon.playAnimation()
                            }
                            hostViewModel.metronomeType = MetronomeType.IVL
                        }
                        else -> {
                            binding.soundIcon.setMinAndMaxProgress(0.0f, 0.5f)
                            binding.soundIcon.playAnimation()
                            hostViewModel.stopMetronome()
                        }
                    }
                    binding.zmsCardText.text = getString(state.zmsCardText)
                }
                binding.soundIcon.isVisible = state.soundIconVisibility

                binding.adrenalinCard.background = getDrawable(requireContext(), state.adrenalinCardColor)
                binding.adrenalinCardTimer.text = state.adrenalinCardTimer
                binding.adrenalinInjectCard.setCardColor(state.adrenalinInjectCardColor)
                binding.adrenalinInjectCard.setSubtitle(getString(state.adrenalinInjectCardSubtitle))

                if (state.defibrillationCardVisibility) {
                    binding.defibrillationCardTimer.text = state.defibrillationCardTimer
                    binding.defibrillationCard.background = getDrawable(requireContext(), state.defibrillationCardColor)
                    binding.doDefibrillationCard.setCardColor(state.doDefibrillationCardColor)
                    binding.doDefibrillationCard.setTitle(
                        getString(
                            R.string.reanimation_do_defibrillation_title, getString(state.doDefibrillationCardPower)
                        )
                    )
                    binding.doDefibrillationCard.setSubtitle(
                        getString(R.string.reanimation_do_defibrillation_count, state.doDefibrillationCardCount)
                    )
                } else {
                    binding.defibrillationCard.isVisible = false
                    binding.doDefibrillationCard.isVisible = false
                }

                if (state.additionalDataCardsVisibility) {
                    binding.additionalDataCardTitle.text = getString(state.additionalDataCardTitle)
                    binding.additionalDataCard1.background = getDrawable(
                        requireContext(), state.additionalDataCard1Color
                    )
                    binding.additionalDataCard1.text = getString(state.additionalDataCard1Text)
                    binding.additionalDataCard2.background = getDrawable(
                        requireContext(), state.additionalDataCard2Color
                    )
                    binding.additionalDataCard2.text = getString(state.additionalDataCard2Text)
                } else {
                    binding.additionalDataCard.isVisible = false
                }

                binding.doIntubationCard.isVisible = state.doIntubationCardVisibility

                if (state.firstAudioFile != NONE_AUDIO_FILE) hostViewModel.audioQueue.add(state.firstAudioFile)
                if (state.secondAudioFile != NONE_AUDIO_FILE) hostViewModel.audioQueue.add(state.secondAudioFile)
                if (state.thirdAudioFile != NONE_AUDIO_FILE) hostViewModel.audioQueue.add(state.thirdAudioFile)
                if (state.fourthAudioFile != NONE_AUDIO_FILE) hostViewModel.audioQueue.add(state.fourthAudioFile)
            }
        }

        newbornHelpJob = lifecycleScope.launch {
            hostViewModel.newbornHelp.collect {
                if (it != NewbornHelp.NONE) viewModel.onNewEvent(EventType.SaveNewBornHelp(it))
            }
        }

        binding.zmsCard.setOnClickListener { viewModel.onNewEvent(EventType.ZmsCardClick) }
        binding.additionalDataCard1.setOnClickListener { viewModel.onNewEvent(EventType.AdditionalData1Click) }
        binding.additionalDataCard2.setOnClickListener { viewModel.onNewEvent(EventType.AdditionalData2Click) }
        binding.adrenalinInjectCard.setOnSwipeListener { viewModel.onNewEvent(EventType.InjectAdrenalinClick) }
        binding.doDefibrillationCard.setOnSwipeListener { viewModel.onNewEvent(EventType.DoDefibrillationClick) }
        binding.doIntubationCard.setOnSwipeListener { viewModel.onNewEvent((EventType.DoIntubationClick)) }
        binding.finishReanimationCard.setOnSwipeListener {
            viewModel.onNewEvent(EventType.ReanimationFinishClick)
            (requireActivity() as Navigable).navigateTo(
                HelpListFragment.newInstance(), NavigateBackBehavior.BackToSaveTransaction
            )
        }
    }

    override fun onStart() {
        super.onStart()
        view?.keepScreenOn = true
    }

    override fun onStop() {
        super.onStop()
        view?.keepScreenOn = false
    }

    override fun onDestroyView() {
        viewUpdateJob?.cancel()
        newbornHelpJob?.cancel()
        hostViewModel.stopMetronome()
        hostViewModel.audioQueue.clear()
        super.onDestroyView()
    }

    companion object {
        private const val METRONOME_TYPE = "METRONOME_TYPE"

        fun newInstance(metronomeType: MetronomeType) = ReanimationFragment().apply {
            arguments = bundleOf(METRONOME_TYPE to metronomeType.name)
        }
    }
}
