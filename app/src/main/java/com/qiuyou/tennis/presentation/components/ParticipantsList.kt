package com.qiuyou.tennis.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.qiuyou.tennis.data.model.UserEntity

@Composable
fun ParticipantsList(
    participants: List<UserEntity>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "参与者 (${participants.size}人)",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        if (participants.isEmpty()) {
            Text(
                text = "暂无参与者",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(participants) { participant ->
                    ParticipantItem(participant)
                }
            }
        }
    }
}

@Composable
private fun ParticipantItem(participant: UserEntity) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(64.dp)
    ) {
        // Avatar
        AsyncImage(
            model = participant.avatar ?: "https://via.placeholder.com/64",
            contentDescription = participant.username,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Name
        Text(
            text = participant.username,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
