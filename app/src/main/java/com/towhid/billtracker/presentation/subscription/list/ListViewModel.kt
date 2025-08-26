package com.towhid.billtracker.presentation.subscription.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.towhid.billtracker.data.repository.ConverterRepository
import com.towhid.billtracker.domain.model.BillingCycleType
import com.towhid.billtracker.domain.model.Subscription
import com.towhid.billtracker.domain.usecase.DeleteSubscriptionUseCase
import com.towhid.billtracker.domain.usecase.GetAllSubscriptionsUseCase
import com.towhid.billtracker.domain.usecase.UpsertSubscriptionUseCase
import com.towhid.billtracker.prefs.UserPrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class ListViewModel(
    private val getAll: GetAllSubscriptionsUseCase,
    private val upsert: UpsertSubscriptionUseCase,
    private val delete: DeleteSubscriptionUseCase,
    private val prefs: UserPrefs,
    private val repository: ConverterRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ListState())
    val state: StateFlow<ListState> = _state


    init {
        viewModelScope.launch {
            prefs.preferredCurrency.collect { cur ->
                _state.update { it.copy(preferredCurrency = cur) }
                recalculateTotal()
            }
        }
        viewModelScope.launch {
            getAll().collectLatest { items ->
                _state.update { it.copy(items = items) }
                recalculateTotal()
            }
        }
    }

    fun onAction(action: ListAction) {
        when (action) {
            is ListAction.MarkPaid -> markPaid(action.id)
            is ListAction.Delete -> delete(action.id)
            is ListAction.ChangeFilter -> _state.update { it.copy(filter = action.filter) }
            is ListAction.ChangeSort -> _state.update { it.copy(sort = action.sortBy) }
            is ListAction.Convert -> {
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true, error = null) }
                    try {
                        val converted = repository.convert(action.from, action.to, action.amount)
                        _state.update {
                            it.copy(
                                isLoading = false,
                                convertedTotal = converted
                            )
                        }
                    } catch (t: Throwable) {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = "Conversion failed: ${t.message}"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun recalculateTotal() {
        val subtotal = _state.value
        val sum = subtotal.items.sumOf {  it.amount}
        _state.update { it.copy(totalInPreferred = sum) }
    }

    private fun markPaid(id: Long) {
        viewModelScope.launch {
            val item = _state.value.items.find { it.id == id } ?: return@launch
            val next = when (item.cycle) {
                BillingCycleType.WEEKLY -> item.nextDue.plusWeeks(1)
                BillingCycleType.MONTHLY -> item.nextDue.plusMonths(1)
                BillingCycleType.YEARLY -> item.nextDue.plusYears(1)
                BillingCycleType.CUSTOM -> item.nextDue.plusDays((item.customDays ?: 30).toLong())
            }
            upsert(item.copy(lastPaid = LocalDate.now(), nextDue = next))
        }
    }

    private fun delete(id: Long) {
        viewModelScope.launch {
            val item = _state.value.items.find { it.id == id } ?: return@launch
            delete(item)
        }
    }

    fun visibleItems(): List<Subscription> {
        val today = LocalDate.now()
        val list = when (_state.value.filter) {
            Filter.ALL -> _state.value.items
            Filter.DUE_SOON -> _state.value.items.filter { !it.nextDue.isBefore(today) && it.nextDue <= today.plusDays(7) }
            Filter.OVERDUE -> _state.value.items.filter { it.nextDue.isBefore(today) }
        }
        return when (_state.value.sort) {
            SortBy.NEXT_DUE_ASC -> list.sortedBy { it.nextDue }
            SortBy.AMOUNT_DESC -> list.sortedByDescending { it.amount }
        }
    }
}


enum class Filter { ALL, DUE_SOON, OVERDUE }
enum class SortBy { NEXT_DUE_ASC, AMOUNT_DESC }

sealed class ListAction {
    data class MarkPaid(val id: Long) : ListAction()
    data class Delete(val id: Long) : ListAction()
    data class ChangeFilter(val filter: Filter) : ListAction()
    data class ChangeSort(val sortBy: SortBy) : ListAction()
    data class Convert (val from: String, val to: String, val amount: Double): ListAction()
}

data class ListState(
    val items: List<Subscription> = emptyList(),
    val filter: Filter = Filter.ALL,
    val sort: SortBy = SortBy.NEXT_DUE_ASC,
    val preferredCurrency: String = "BDT",
    val totalInPreferred: Double = 0.0,
    val convertedTotal: Double = 0.0,
    val ratesDate: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)