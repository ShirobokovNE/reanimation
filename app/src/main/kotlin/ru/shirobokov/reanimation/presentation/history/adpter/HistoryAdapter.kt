package ru.shirobokov.reanimation.presentation.history.adpter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.shirobokov.reanimation.R
import ru.shirobokov.reanimation.domain.entity.presentation.ReanimationHelpPresentation

class HistoryAdapter(
    private val onItemClick: (Long) -> Unit,
    private val onDeleteClick: (Long) -> Unit,
    private val onSwipe: () -> Unit
) : RecyclerView.Adapter<HistoryHolder>() {

    var data: List<ReanimationHelpPresentation> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        HistoryHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.li_history, parent, false),
            onItemClick,
            onDeleteClick,
            onSwipe
        )

    override fun onBindViewHolder(holder: HistoryHolder, position: Int) =
        holder.bind(data[position])

    override fun getItemCount() = data.size

}