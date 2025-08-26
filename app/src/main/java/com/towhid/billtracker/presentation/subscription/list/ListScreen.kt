package com.towhid.billtracker.presentation.subscription.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.towhid.billtracker.presentation.subscription.components.SubscriptionItem
import org.koin.androidx.compose.getViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(onAdd: () -> Unit, onEdit: (Long) -> Unit) {
    val viewModel: ListViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Bill Tracker") }, actions = {
                IconButton(onClick = { viewModel.onEvent(ListEvent.RefreshRates) }) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Refresh"
                    )
                }
            })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add"
                )
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            if (state.error != null) AssistChip(onClick = {}, label = { Text(state.error!!) })
            Text(
                "Total: %.2f %s (rates: %s)".format(
                    state.totalInPreferred,
                    state.preferredCurrency,
                    state.ratesDate ?: "-"
                ), modifier = Modifier.padding(12.dp)
            )
            // filters
            Row(Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("All", "Due soon", "Overdue").forEachIndexed { idx, label ->
                    FilterChip(
                        selected = state.filter.ordinal == idx,
                        onClick = {
                            viewModel.onEvent(
                                ListEvent.ChangeFilter(
                                    when (idx) {
                                        0 -> Filter.ALL
                                        1 -> Filter.DUE_SOON
                                        else -> Filter.OVERDUE
                                    }
                                )
                            )
                        },
                        label = { Text(label) }
                    )
                    /*AssistChip(onClick = {
                        viewModel.onEvent(
                            ListEvent.ChangeFilter(
                                when (idx) {
                                    0 -> Filter.ALL;1 -> Filter.DUE_SOON;else -> Filter.OVERDUE
                                }
                            )
                        )
                    }, label = { Text(label) }, selected = state.filter.ordinal == idx)*/
                }
            }

            val items = viewModel.visibleItems()
            if (items.isEmpty()) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { Text("No subscriptions. Tap + to add.") }
            } else {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(items, key = { it.id }) { item ->
                        SubscriptionItem(
                            subscription = item,
                            onMarkPaid = { viewModel.onEvent(ListEvent.MarkPaid(it.id)) },
                            onDelete = { viewModel.onEvent(ListEvent.Delete(it.id)) },
                            onClick = { onEdit(it.id) })
                    }
                }
            }
        }
    }
}