package com.towhid.billtracker.presentation.subscription.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.towhid.billtracker.domain.model.Subscription

@Composable
fun SubscriptionItem(subscription: Subscription, onMarkPaid: (Subscription) -> Unit, onDelete: (Subscription) -> Unit, onClick: (Subscription) -> Unit) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .clickable { onClick(subscription) }) {
        Column(Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(subscription.name)
                Text("%.2f %s".format(subscription.amount, subscription.currency))
            }
            Spacer(Modifier.height(4.dp))
            Text("Next due: ${subscription.nextDue}")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { onMarkPaid(subscription) }) { Text("Mark paid") }
                TextButton(onClick = { onDelete(subscription) }) { Text("Delete") }
            }
        }
    }
}