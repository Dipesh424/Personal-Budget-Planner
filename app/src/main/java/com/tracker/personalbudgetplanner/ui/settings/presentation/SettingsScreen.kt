package com.tracker.personalbudgetplanner.ui.settings.presentation

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var showImportDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    
    val context = LocalContext.current

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Section
            item {
                ProfileHeader(
                    name = "John Doe",
                    email = "johndoe@example.com"
                )
            }

            // General Settings
            item { SettingCategoryHeader(title = "General") }
            item {
                SettingItem(
                    icon = Icons.Default.CurrencyExchange,
                    title = "Currency",
                    subtitle = "INR (₹)",
                    onClick = {}
                )
            }
            item {
                SettingItem(
                    icon = Icons.Default.Palette,
                    title = "Theme",
                    subtitle = "System Default",
                    onClick = {}
                )
            }

            // Account & Security
            item { SettingCategoryHeader(title = "Account & Security") }
            item {
                SettingItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    subtitle = "Budget alerts & reminders",
                    onClick = {}
                )
            }
            item {
                SettingItem(
                    icon = Icons.Default.Lock,
                    title = "App Lock",
                    subtitle = "Fingerprint or PIN",
                    onClick = {}
                )
            }

            // Data
            item { SettingCategoryHeader(title = "Data Management") }
            item {
                SettingItem(
                    icon = Icons.Default.UploadFile,
                    title = "Import your Statement",
                    subtitle = "Upload Excel files to import records",
                    onClick = { showImportDialog = true }
                )
            }
            item {
                SettingItem(
                    icon = Icons.Default.FileDownload,
                    title = "Export Data",
                    subtitle = "CSV, PDF, Excel",
                    onClick = {}
                )
            }
            item {
                SettingItem(
                    icon = Icons.Default.CloudUpload,
                    title = "Backup & Restore",
                    subtitle = "Google Drive sync",
                    onClick = {}
                )
            }

            // About
            item { SettingCategoryHeader(title = "About") }
            item {
                SettingItem(
                    icon = Icons.Default.Info,
                    title = "App Info",
                    subtitle = "Version 1.0.0",
                    onClick = {}
                )
            }
            item {
                SettingItem(
                    icon = Icons.Default.Star,
                    title = "Rate Us",
                    onClick = {}
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    if (showImportDialog) {
        ImportStatementDialog(
            selectedFileUri = selectedFileUri,
            onDismiss = { 
                showImportDialog = false
                selectedFileUri = null
            },
            onPickFile = {
                // xlsx and xls mime types
                filePickerLauncher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            },
            onUpload = {
                if (selectedFileUri != null) {
                    showImportDialog = false
                    showSuccessDialog = true
                    selectedFileUri = null
                }
            }
        )
    }

    if (showSuccessDialog) {
        SuccessImportDialog(
            onDismiss = { showSuccessDialog = false }
        )
    }
}

@Composable
fun ImportStatementDialog(
    selectedFileUri: Uri?,
    onDismiss: () -> Unit, 
    onPickFile: () -> Unit,
    onUpload: () -> Unit
) {
    val context = LocalContext.current
    val fileName = remember(selectedFileUri) {
        selectedFileUri?.let { uri ->
            var name: String? = null
            if (uri.scheme == "content") {
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (index != -1) name = it.getString(index)
                    }
                }
            }
            name ?: uri.path?.substringAfterLast('/') ?: "Selected File"
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Import Statement",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Upload your bank statement in Excel (.xlsx) format to automatically sync your transactions.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clickable { onPickFile() },
                    shape = RoundedCornerShape(16.dp),
                    color = if (selectedFileUri != null) 
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) 
                    else 
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (selectedFileUri != null)
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        else
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(12.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (selectedFileUri == null) {
                            Icon(
                                imageVector = Icons.Default.FileUpload,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tap to select Excel file",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.TaskAlt,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = fileName ?: "File selected",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Tap to change file",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = onUpload,
                        modifier = Modifier.weight(1f),
                        enabled = selectedFileUri != null,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Upload")
                    }
                }
            }
        }
    }
}

@Composable
fun SuccessImportDialog(onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4CAF50).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(48.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Text(
                    text = "Upload Successful!",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Your statement has been imported successfully. All transactions are now visible in your records.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Great!")
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(name: String, email: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.take(1),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SettingCategoryHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge.copy(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        ),
        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
    )
}

@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}
