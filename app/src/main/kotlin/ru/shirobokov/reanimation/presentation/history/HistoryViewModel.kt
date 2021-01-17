package ru.shirobokov.reanimation.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.shirobokov.reanimation.domain.ReanimationInteractor
import ru.shirobokov.reanimation.domain.entity.presentation.ReanimationHelpPresentation

@ExperimentalCoroutinesApi
class HistoryViewModel(private val interactor: ReanimationInteractor) : ViewModel() {

    val history = MutableStateFlow<List<ReanimationHelpPresentation>?>(null)

    init {
        viewModelScope.launch { history.value = interactor.getReanimationHistory() }
    }

    fun onDeleteClick(startReanimationTime: Long) {
        viewModelScope.launch {
            interactor.deleteReanimationHelp(startReanimationTime)
            history.value = interactor.getReanimationHistory()
        }
    }
}