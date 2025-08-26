package com.towhid.billtracker.presentation.subscription.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.towhid.billtracker.UiEvent
import com.towhid.billtracker.UiState
import com.towhid.billtracker.domain.model.BillingCycleType
import com.towhid.billtracker.domain.model.Subscription
import com.towhid.billtracker.domain.usecase.GetAllSubscriptionsUseCase
import com.towhid.billtracker.domain.usecase.GetByIdUseCase
import com.towhid.billtracker.domain.usecase.UpsertSubscriptionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class EditViewModel(
    private val upsert: UpsertSubscriptionUseCase,
    private val getById: GetByIdUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(EditState())
    val state: StateFlow<EditState> = _state.asStateFlow()

    fun onEvent(event: EditEvent) {
        when (event) {
            is EditEvent.Load -> load(event.id)
            is EditEvent.NameChanged -> _state.update { it.copy(name = event.name) }
                .also { validate() }

            is EditEvent.AmountChanged -> _state.update { it.copy(amount = event.amount) }
                .also { validate() }

            is EditEvent.CurrencyChanged -> _state.update { it.copy(currency = event.currency) }
                .also { validate() }

            is EditEvent.CycleChanged -> _state.update { it.copy(cycle = event.cycle) }
                .also { validate() }

            is EditEvent.CustomDaysChanged -> _state.update { it.copy(customDays = event.day) }
                .also { validate() }

            is EditEvent.NextDueChanged -> _state.update { it.copy(nextDue = event.dueDate) }
                .also { validate() }

            is EditEvent.NotesChanged -> _state.update { it.copy(notes = event.note) }
            is EditEvent.Save -> save()
        }
    }

    private fun load(id: Long) {
        _state.update { it.copy(id = id) }
        if (id == -1L) return
        viewModelScope.launch {
            val existing =  getById(id)
            _state.update {
                it.copy(
                    id = existing?.id ?: 0L,
                    name = existing?.name?: "",
                    
                )
            }
        }
    }

    private fun validate() {
        val st = _state.value
        val valid = st.name.isNotBlank() && st.amount.toDoubleOrNull() != null && runCatching {
            LocalDate.parse(st.nextDue)
        }.isSuccess &&
                (st.cycle != BillingCycleType.CUSTOM || st.customDays.toIntOrNull()
                    ?.let { it > 0 } == true)
        _state.update { it.copy(isValid = valid) }
    }

    private fun save() {
        val st = _state.value
        if (!st.isValid) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            val item = Subscription(
                id = if (st.id == -1L) 0L else st.id,
                name = st.name,
                amount = st.amount.toDouble(),
                currency = st.currency.uppercase(),
                cycle = st.cycle,
                customDays = st.customDays.toIntOrNull(),
                nextDue = LocalDate.parse(st.nextDue),
                notes = st.notes
            )
            try {
                upsert(item)
                _state.update { it.copy(isSaving = false) }
            } catch (t: Throwable) {
                _state.update { it.copy(isSaving = false, error = t.message ?: "Failed to save") }
            }
        }
    }
}

sealed interface EditEvent : UiEvent {
    data class Load(val id: Long) : EditEvent
    data class NameChanged(val name: String) : EditEvent
    data class AmountChanged(val amount: String) : EditEvent
    data class CurrencyChanged(val currency: String) : EditEvent
    data class CycleChanged(val cycle: BillingCycleType) : EditEvent
    data class CustomDaysChanged(val day: String) : EditEvent
    data class NextDueChanged(val dueDate: String) : EditEvent
    data class NotesChanged(val note: String) : EditEvent
    object Save : EditEvent
}

data class EditState(
    val id: Long = -1L,
    val name: String = "",
    val amount: String = "",
    val currency: String = "USD",
    val cycle: BillingCycleType = BillingCycleType.MONTHLY,
    val customDays: String = "",
    val nextDue: String = LocalDate.now().toString(),
    val notes: String = "",
    val isValid: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
) : UiState
