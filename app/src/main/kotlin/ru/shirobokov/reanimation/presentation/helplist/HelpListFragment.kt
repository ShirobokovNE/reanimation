package ru.shirobokov.reanimation.presentation.helplist

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.shirobokov.reanimation.R
import ru.shirobokov.reanimation.databinding.FragmentHelpListBinding
import ru.shirobokov.reanimation.presentation.helplist.adapter.HelpListAdapter

@ExperimentalCoroutinesApi
class HelpListFragment : Fragment(R.layout.fragment_help_list) {

    private val viewModel: HelpListViewModel by viewModel()
    private val binding: FragmentHelpListBinding by viewBinding()
    private val adapter = HelpListAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getHelpList(requireArguments().getLong(START_REANIMATION_KEY))

        binding.helpList.adapter = adapter
        binding.copyButton.setOnClickListener {
            val text = StringBuilder().apply { viewModel.helpList.value.forEach { append("$it\n") } }
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText(LABEL, text))
            Toast.makeText(requireContext(), R.string.reanimation_finish_copy, Toast.LENGTH_SHORT).show()
        }

        lifecycleScope.launchWhenCreated { viewModel.helpList.collect { adapter.data = it } }
    }

    companion object {
        private const val LABEL = "label"
        private const val START_REANIMATION_KEY = "START_REANIMATION_KEY"

        fun newInstance(startReanimation: Long? = null) = HelpListFragment().apply {
            arguments = bundleOf(START_REANIMATION_KEY to startReanimation)
        }
    }
}