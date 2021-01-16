package ru.shirobokov.reanimation.presentation.history.adpter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.shirobokov.reanimation.databinding.LiHistoryBinding
import ru.shirobokov.reanimation.domain.entity.presentation.ReanimationHelpPresentation
import ru.shirobokov.reanimation.utils.px
import java.text.SimpleDateFormat
import java.util.*

class HistoryHolder(
    itemView: View,
    private val onItemClick: (Long) -> Unit,
    private val onDeleteClick: (Long) -> Unit,
    private val onSwipe: () -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val binding: LiHistoryBinding by viewBinding()
    private val dateFormatter = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
    private val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    init {
        binding.historyItem.menuViewWidth = 80.px
    }

    fun bind(data: ReanimationHelpPresentation) {
        binding.historyItem.updateSwipedState(data.isSwiped)
        binding.historyItem.onClickListener = { onItemClick(data.startReanimation) }
        binding.historyItem.onSwipeListener = { isSwiped ->
            if (isSwiped && !data.isSwiped) onSwipe()
            data.isSwiped = isSwiped
        }
        binding.deleteButton.setOnClickListener { onDeleteClick(data.startReanimation) }
        binding.historyText.text = StringBuilder().apply {
            append("${dateFormatter.format(data.startReanimation)} ")
            append("c ${timeFormatter.format(data.startReanimation)} ")
            append("до ${timeFormatter.format(data.endReanimation)} ")
        }
    }

    fun closeSwipe() {
        binding.historyItem.closeSwipe()
    }
}