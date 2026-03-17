package com.tracker.personalbudgetplanner.core.presentation

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tracker.personalbudgetplanner.R

@Composable
fun AppAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    description: String,
    confirmText: String = stringResource(R.string.confirm),
    dismissText: String = stringResource(R.string.cancel),
    icon: ImageVector? = Icons.Default.Warning,
    isDestructive: Boolean = false // If true, the button turns red
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = if (isDestructive) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary
                )
            }
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDestructive) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(confirmText, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(dismissText, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        },
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    )
}