package ru.shirobokov.reanimation.presentation.history

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.shirobokov.reanimation.R
import ru.shirobokov.reanimation.databinding.FragmentReanimationHistoryBinding
import ru.shirobokov.reanimation.presentation.Navigable
import ru.shirobokov.reanimation.presentation.helplist.HelpListFragment
import ru.shirobokov.reanimation.presentation.history.adpter.DiffUtilCallback
import ru.shirobokov.reanimation.presentation.history.adpter.HistoryAdapter
import ru.shirobokov.reanimation.presentation.history.adpter.HistoryHolder

@ExperimentalCoroutinesApi
class HistoryFragment : Fragment(R.layout.fragment_reanimation_history) {

    private val viewModel: HistoryViewModel by viewModel()
    private val binding: FragmentReanimationHistoryBinding by viewBinding()
    private val adapter by lazy { HistoryAdapter(::onItemClick, viewModel::onDeleteClick, ::onSwipe) }
    private val diffUtil = DiffUtilCallback()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.historyList.adapter = adapter
        binding.historyList.addItemDecoration(DividerItemDecoration(requireContext(), VERTICAL))
        lifecycleScope.launchWhenCreated {
            viewModel.history.collect { newList ->
                if (newList != null) {
                    binding.emptyHistoryText.isVisible = newList.isEmpty()
                    diffUtil.oldList = adapter.data
                    diffUtil.newList = newList
                    DiffUtil.calculateDiff(diffUtil).dispatchUpdatesTo(adapter)
                    adapter.data = newList
                }
            }
        }
    }

    private fun onItemClick(startReanimation: Long) {
        (requireActivity() as Navigable).navigateTo(HelpListFragment.newInstance(startReanimation))
    }

    private fun onSwipe() {
        if (!isVisible) return
        adapter.data.find { it.isSwiped }?.let {
            it.isSwiped = false
            val position = adapter.data.indexOf(it)
            val viewHolder = binding.historyList.findViewHolderForAdapterPosition(position) as? HistoryHolder
            if (viewHolder == null) adapter.notifyItemChanged(position) else viewHolder.closeSwipe()
        }
    }

    companion object {
        fun newInstance() = HistoryFragment()
    }
}
