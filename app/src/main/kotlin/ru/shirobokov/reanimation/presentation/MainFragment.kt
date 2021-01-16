package ru.shirobokov.reanimation.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.shirobokov.reanimation.R
import ru.shirobokov.reanimation.databinding.FragmentMainBinding
import ru.shirobokov.reanimation.presentation.history.HistoryFragment
import ru.shirobokov.reanimation.presentation.reanimation.ReanimationFragment

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class MainFragment : Fragment(R.layout.fragment_main) {

    private val hostViewModel: HostViewModel by sharedViewModel()
    private val binding: FragmentMainBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.adultPatientCard.setOnClickListener {
            (requireActivity() as Navigable).navigateTo(
                ReanimationFragment.newInstance(MetronomeType.ZMS_ADULT),
                NavigateBackBehavior.TwoClickBack
            )
        }
        binding.childPatientCard.setOnClickListener {
            (requireActivity() as Navigable).navigateTo(
                ReanimationFragment.newInstance(MetronomeType.ZMS_CHILD),
                NavigateBackBehavior.TwoClickBack
            )
        }
        binding.newbornPatientCard.setOnClickListener {
            (requireActivity() as Navigable).navigateTo(
                ReanimationFragment.newInstance(MetronomeType.ZMS_NEWBORN),
                NavigateBackBehavior.TwoClickBack
            )
        }
        binding.historyCard.setOnClickListener {
            (requireActivity() as Navigable).navigateTo(HistoryFragment.newInstance())
            hostViewModel.stopMetronome()
            hostViewModel.audioQueue.clear()
        }
    }

    companion object {
        fun newInstance() = MainFragment()
    }
}