package ru.shirobokov.reanimation.presentation.history.adpter

import androidx.recyclerview.widget.DiffUtil
import ru.shirobokov.reanimation.domain.entity.presentation.ReanimationHelpPresentation

class DiffUtilCallback : DiffUtil.Callback() {

    var oldList = listOf<ReanimationHelpPresentation>()
    var newList = listOf<ReanimationHelpPresentation>()

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].startReanimation == newList[newItemPosition].startReanimation

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = false
}