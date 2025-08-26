package com.towhid.billtracker.presentation.subscription.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.towhid.billtracker.domain.model.BillingCycleType
import com.towhid.billtracker.domain.model.Subscription
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

    fun onAction(action: EditAction) {
        when (action) {
            is EditAction.Load -> load(action.id)
            is EditAction.NameChanged -> _state.update { it.copy(name = action.name) }
                .also { validate() }

            is EditAction.AmountChanged -> _state.update { it.copy(amount = action.amount) }
                .also { validate() }

            is EditAction.CurrencyChanged -> _state.update { it.copy(currency = action.currency) }
                .also { validate() }

            is EditAction.CycleChanged -> _state.update { it.copy(cycle = action.cycle) }
                .also { validate() }

            is EditAction.CustomDaysChanged -> _state.update { it.copy(customDays = action.day) }
                .also { validate() }

            is EditAction.NextDueChanged -> _state.update { it.copy(nextDue = action.dueDate) }
                .also { validate() }

            is EditAction.NotesChanged -> _state.update { it.copy(notes = action.note) }
            is EditAction.Save -> save()
        }
    }

    private fun load(id: Long) {
        _state.update { it.copy(id = id) }
        if (id == -1L) return
        viewModelScope.launch {
            val existing = getById(id)
            _state.update {
                it.copy(
                    id = existing?.id ?: 0L,
                    name = existing?.name ?: "",
                    amount = existing?.amount.toString()
                    )
            }
        }
    }

    private fun validate() {
        val item = _state.value
        val valid = item.name.isNotBlank() && item.amount.toDoubleOrNull() != null && runCatching {
            LocalDate.parse(item.nextDue)
        }.isSuccess &&
                (item.cycle != BillingCycleType.CUSTOM || item.customDays.toIntOrNull()
                    ?.let { it > 0 } == true)
        _state.update { it.copy(isValid = valid) }
    }

    private fun save() {
        val item = _state.value
        if (!item.isValid) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            val item = Subscription(
                id = if (item.id == -1L) 0L else item.id,
                name = item.name,
                amount = item.amount.toDouble(),
                currency = item.currency.uppercase(),
                cycle = item.cycle,
                customDays = item.customDays.toIntOrNull(),
                nextDue = LocalDate.parse(item.nextDue),
                notes = item.notes
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

sealed class EditAction {
    data class Load(val id: Long) : EditAction()
    data class NameChanged(val name: String) : EditAction()
    data class AmountChanged(val amount: String) : EditAction()
    data class CurrencyChanged(val currency: String) : EditAction()
    data class CycleChanged(val cycle: BillingCycleType) : EditAction()
    data class CustomDaysChanged(val day: String) : EditAction()
    data class NextDueChanged(val dueDate: String) : EditAction()
    data class NotesChanged(val note: String) : EditAction()
    object Save : EditAction()
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
)
