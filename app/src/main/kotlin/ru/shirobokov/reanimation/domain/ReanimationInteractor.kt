package ru.shirobokov.reanimation.domain

import ru.shirobokov.reanimation.data.database.DataBase
import ru.shirobokov.reanimation.domain.entity.ReanimationHelp
import ru.shirobokov.reanimation.domain.entity.presentation.ReanimationHelpPresentation
import ru.shirobokov.reanimation.utils.ResourcesHandler
import java.text.SimpleDateFormat
import java.util.Locale

class ReanimationInteractor(
    resourcesHandler: ResourcesHandler,
    private val dataBase: DataBase
) : ResourcesHandler by resourcesHandler {

    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

    val helpList: MutableList<String> = mutableListOf()

    suspend fun saveReanimationHelp(
        startReanimation: Long,
        endReanimation: Long,
        list: List<Pair<Int, Long>>
    ) {
        val saveList = list
            .sortedBy { it.second }
            .map { getString(it.first, dateFormatter.format(it.second)) }
        helpList.clear()
        helpList.addAll(saveList)
        dataBase.getReanimationResultDataBase()
            .addReanimationHelp(ReanimationHelp(startReanimation, endReanimation, saveList))
    }

    suspend fun getReanimationHistory(): List<ReanimationHelpPresentation> =
        dataBase.getReanimationResultDataBase().getReanimationHelpList()
            .map { ReanimationHelpPresentation.map(it) }

    suspend fun getReanimationHelp(startReanimationTime: Long): ReanimationHelpPresentation =
        ReanimationHelpPresentation.map(
            dataBase.getReanimationResultDataBase().getReanimationHelp(startReanimationTime)
        )

    suspend fun deleteReanimationHelp(startReanimationTime: Long) {
        dataBase.getReanimationResultDataBase().deleteReanimationHelp(startReanimationTime)
    }
}
