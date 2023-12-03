package com.example.innovehair.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

object UiEventBus {
    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents: SharedFlow<UiEvent> get() = _uiEvents

    suspend fun sendEvent(event: UiEvent) {
        _uiEvents.emit(event)
    }
}

sealed class UiEvent {
    object UserInteractionEvent : UiEvent()
    // Adicione mais tipos de eventos conforme necessário
}