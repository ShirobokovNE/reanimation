package ru.shirobokov.reanimation.presentation.helplist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.shirobokov.reanimation.domain.ReanimationInteractor

@ExperimentalCoroutinesApi
class HelpListViewModel(private val interactor: ReanimationInteractor) : ViewModel() {

    val helpList = MutableStateFlow(emptyList<String>())

    fun getHelpList(startReanimation: Long) {
        viewModelScope.launch {
            helpList.value = if (startReanimation != 0L) {
                interactor.getReanimationHelp(startReanimation).helpList
            } else {
                interactor.helpList
            }
        }
    }
}
