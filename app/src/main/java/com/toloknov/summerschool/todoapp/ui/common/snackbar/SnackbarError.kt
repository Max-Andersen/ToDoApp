package com.toloknov.summerschool.todoapp.ui.common.snackbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.toloknov.summerschool.theme.theme.TodoRed
import com.toloknov.summerschool.todoapp.R

@Composable
fun SnackbarError(
    text: String,
    onClick: () -> Unit = {}
) {
    Snackbar(
        modifier = Modifier
            .padding(com.toloknov.summerschool.theme.theme.PADDING_MEDIUM)
            .shadow(elevation = 4.dp, shape = Shapes().large)
            .clip(Shapes().large)
            .clickable(onClick = onClick),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_circle_cancel_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.TodoRed
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 12.dp),
            )
        }
    }
}