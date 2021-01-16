package ru.shirobokov.reanimation.domain.entity.presentation

import ru.shirobokov.reanimation.domain.entity.ReanimationHelp

class ReanimationHelpPresentation(
    val startReanimation: Long = 0,
    val endReanimation: Long = 0,
    val helpList: List<String> = emptyList(),
    var isSwiped: Boolean = false
) {
    companion object {
        fun map(reanimationHelp: ReanimationHelp) = with(reanimationHelp) {
            ReanimationHelpPresentation(startReanimation, endReanimation, helpList, false)
        }
    }
}