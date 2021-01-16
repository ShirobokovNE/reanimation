package ru.shirobokov.reanimation.presentation.reanimation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.shirobokov.reanimation.presentation.reanimation.store.EventType
import ru.shirobokov.reanimation.presentation.reanimation.model.ReanimationModel
import ru.shirobokov.reanimation.presentation.reanimation.store.ReanimationStore

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class ReanimationViewModel(
    private val reanimationStore: ReanimationStore,
    reanimationModel: ReanimationModel
) : ViewModel() {

    val viewState = MutableStateFlow(reanimationModel)

    init {
        viewModelScope.launch {
            ticker(delayMillis = 1000, initialDelayMillis = 1000).consumeEach {
                viewState.updateState(EventType.NewSecond)
            }
        }
    }

    fun onNewEvent(event: EventType) {
        viewModelScope.launch { viewState.updateState(event) }
    }

    private suspend fun MutableStateFlow<ReanimationModel>.updateState(event: EventType) {
        value = reanimationStore.onNewEvent(event, value)
    }
}