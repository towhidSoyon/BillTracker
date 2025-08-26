package com.towhid.billtracker.presentation.subscription.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.towhid.billtracker.presentation.subscription.components.SubscriptionItem
import org.koin.androidx.compose.getViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(onAdd: () -> Unit, onEdit: (Long) -> Unit) {
    val viewModel: ListViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.totalInPreferred) {
        viewModel.onEvent(
            ListEvent.Convert(
                "USD",
                "BDT",
                state.totalInPreferred
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Bill Tracker") }, actions = {})
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
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 12.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Total: ${state.totalInPreferred} USD",
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(12.dp)
                    )
                    AssistChip(onClick = {
                        viewModel.onEvent(
                            ListEvent.Convert(
                                "USD",
                                "BDT",
                                state.totalInPreferred
                            )
                        )
                    }, label = { Text("Convert to BDT") })
                    Text(
                        "Total: ${state.convertedTotal} BDT", modifier = Modifier.padding(12.dp)
                    )
                }
            }


            // filters
            Row(
                Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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
                }
            }

            val items = viewModel.visibleItems()
            if (items.isEmpty()) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { Text("No Items Here.") }
            } else {
                LazyColumn(Modifier.fillMaxSize().padding(horizontal = 4.dp)) {
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