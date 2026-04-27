package com.shoply.app.ui.screens

import androidx.compose.runtime.getValue
import com.shoply.app.ui.state.UiState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.shoply.app.data.ShoppingList
import com.shoply.app.ui.navigation.Screen
import com.shoply.app.viewmodel.ShoppingViewModel
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Delete

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyShoppingListScreen(
    navController: NavHostController,
    viewModel: ShoppingViewModel
) {
    val shoppingListsState by viewModel.shoppingListsUiState.collectAsState()
    val actionState by viewModel.shoppingListActionState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedListForMenu by remember { mutableStateOf<ShoppingList?>(null) }
    var listToRename by remember { mutableStateOf<ShoppingList?>(null) }
    var listToArchive by remember { mutableStateOf<ShoppingList?>(null) }
    var listToDelete by remember { mutableStateOf<ShoppingList?>(null) }
    var showShareDialog by remember { mutableStateOf(false) }
    var shareEmail by remember { mutableStateOf("") }
    var listToShare by remember { mutableStateOf<ShoppingList?>(null) }


    LaunchedEffect(Unit) {
        viewModel.loadActiveShoppingLists()
    }

    LaunchedEffect(actionState) {
        when (val state = actionState) {
            is UiState.Success -> {
                snackbarHostState.showSnackbar(state.data)
                viewModel.clearShoppingListActionState()
            }

            is UiState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.clearShoppingListActionState()
            }

            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("הרשימות שלי") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "חזרה"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "צור רשימה")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = "לחיצה לפתיחה • לחיצה ארוכה לניהול",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            when (val state = shoppingListsState) {
                UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }

                is UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                is UiState.Success -> {
                    if (state.data.isEmpty()) {
                        EmptyListsState(
                            onCreateClick = { showCreateDialog = true }
                        )
                    } else {
                        val currentUserId = viewModel.getCurrentUserId()

                        val myLists = state.data.filter { list ->
                            list.ownerUid == currentUserId
                        }

                        val sharedLists = state.data.filter { list ->
                            list.ownerUid != currentUserId && list.sharedWith.contains(currentUserId)
                        }
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            if (myLists.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "הרשימות שלי",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                items(myLists, key = { it.id }) { shoppingList ->
                                    ShoppingListCard(
                                        shoppingList = shoppingList,
                                        onClick = {
                                            navController.navigate(
                                                Screen.ListDetails.createRoute(shoppingList.id)
                                            )
                                        },
                                        onLongClick = { selectedListForMenu = shoppingList },
                                        onMenuClick = { selectedListForMenu = shoppingList }
                                    )
                                }
                            }

                            if (sharedLists.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "רשימות ששותפו איתי",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(top = 12.dp)
                                    )
                                }

                                items(sharedLists, key = { it.id }) { shoppingList ->
                                    ShoppingListCard(
                                        shoppingList = shoppingList,
                                        isShared = true,
                                        onClick = {
                                            navController.navigate(
                                                Screen.ListDetails.createRoute(shoppingList.id)
                                            )
                                        },
                                        onLongClick = { selectedListForMenu = shoppingList },
                                        onMenuClick = { selectedListForMenu = shoppingList }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateShoppingListDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { newName ->
                viewModel.createShoppingList(newName)
                showCreateDialog = false
            }
        )
    }

    selectedListForMenu?.let { shoppingList ->
        ShoppingListActionsDialog(
            shoppingList = shoppingList,
            onDismiss = { selectedListForMenu = null },
            onRename = {
                selectedListForMenu = null
                listToRename = shoppingList
            },
            onShare = {
                listToShare = shoppingList
                selectedListForMenu = null
                showShareDialog = true
            },
            onArchive = {
                selectedListForMenu = null
                listToArchive = shoppingList
            },
            onDelete = {
                selectedListForMenu = null
                listToDelete = shoppingList
            }
        )
    }

    listToRename?.let { shoppingList ->
        RenameShoppingListDialog(
            currentName = shoppingList.name,
            onDismiss = { listToRename = null },
            onConfirm = { newName ->
                viewModel.updateShoppingListName(shoppingList.id, newName)
                listToRename = null
            }
        )
    }

    listToArchive?.let { shoppingList ->
        AlertDialog(
            onDismissRequest = { listToArchive = null },
            title = { Text("העברה לארכיון") },
            text = { Text("להעביר את '${shoppingList.name}' לארכיון?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.archiveShoppingList(shoppingList.id)
                        listToArchive = null
                    }
                ) {
                    Text("העבר")
                }
            },
            dismissButton = {
                TextButton(onClick = { listToArchive = null }) {
                    Text("ביטול")
                }
            }
        )
    }

    listToDelete?.let { shoppingList ->
        AlertDialog(
            onDismissRequest = { listToDelete = null },
            title = { Text("מחיקת רשימה") },
            text = { Text("האם למחוק את '${shoppingList.name}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteShoppingList(shoppingList.id)
                        listToDelete = null
                    }
                ) {
                    Text("מחק")
                }
            },
            dismissButton = {
                TextButton(onClick = { listToDelete = null }) {
                    Text("ביטול")
                }
            }
        )
    }

    if (showShareDialog && listToShare != null) {
        AlertDialog(
            onDismissRequest = {
                showShareDialog = false
                listToShare = null
            },
            title = { Text("שיתוף רשימה") },
            text = {
                OutlinedTextField(
                    value = shareEmail,
                    onValueChange = { shareEmail = it },
                    label = { Text("אימייל משתמש") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.shareListWithUser(
                        listToShare!!.id,
                        shareEmail
                    )
                    showShareDialog = false
                    shareEmail = ""
                    listToShare = null
                }) {
                    Text("שתף")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showShareDialog = false
                    listToShare = null
                }) {
                    Text("ביטול")
                }
            }
        )
    }
}

@Composable
private fun EmptyListsState(
    onCreateClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "עדיין אין לך רשימות פעילות",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "צרי רשימה חדשה כדי להתחיל לנהל את הקניות שלך בצורה מסודרת.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(onClick = onCreateClick) {
                Text("צור רשימה חדשה")
            }
        }
    }
}

@Composable
private fun StatusMessageCard(
    text: String,
    isError: Boolean
) {
    val containerColor = if (isError) {
        MaterialTheme.colorScheme.errorContainer
    } else {
        androidx.compose.ui.graphics.Color(0xFFDFF5E1)
    }

    val contentColor = if (isError) {
        MaterialTheme.colorScheme.onErrorContainer
    } else {
        androidx.compose.ui.graphics.Color(0xFF1B5E20)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = containerColor,
        contentColor = contentColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ShoppingListCard(
    shoppingList: ShoppingList,
    isShared: Boolean = false,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isShared) {
                androidx.compose.ui.graphics.Color(0xFFE6F4EA)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = shoppingList.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (isShared && shoppingList.sharedByEmail.isNotBlank()) {
                        Text(
                            text = "שותף ע״י: ${shoppingList.sharedByEmail}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AssistChip(
                            onClick = { },
                            label = {
                                Text(
                                    text = when (shoppingList.status) {
                                        ShoppingList.STATUS_ACTIVE -> "פעילה"
                                        ShoppingList.STATUS_ARCHIVED -> "בארכיון"
                                        ShoppingList.STATUS_COMPLETED -> "הושלמה"
                                        else -> shoppingList.status
                                    }
                                )
                            }
                        )

                        shoppingList.selectedStore?.let { store ->
                            AssistChip(
                                onClick = { },
                                label = { Text(store) }
                            )
                        }
                    }
                }

                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "פעולות"
                    )
                }
            }

            Text(
                text = "עודכנה לאחרונה: ${formatTimestamp(shoppingList.updatedAt)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ShoppingListActionsDialog(
    shoppingList: ShoppingList,
    onDismiss: () -> Unit,
    onRename: () -> Unit,
    onShare: () -> Unit,
    onArchive: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(shoppingList.name) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ActionRow(
                    icon = Icons.Default.Edit,
                    text = "עריכת שם",
                    onClick = onRename
                )
                ActionRow(
                    icon = Icons.Default.Share,
                    text = "שיתוף",
                    onClick = onShare
                )
                ActionRow(
                    icon = Icons.Default.Archive,
                    text = "העברה לארכיון",
                    onClick = onArchive
                )
                ActionRow(
                    icon = Icons.Default.Delete,
                    text = "מחיקה",
                    onClick = onDelete
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("סגור")
            }
        }
    )
}

@Composable
private fun ActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = text)
            Text(text = text, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun CreateShoppingListDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var newListName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("רשימה חדשה") },
        text = {
            OutlinedTextField(
                value = newListName,
                onValueChange = { newListName = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("שם הרשימה") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (newListName.isNotBlank()) {
                        onConfirm(newListName.trim())
                    }
                }
            ) {
                Text("צור")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ביטול")
            }
        }
    )
}

@Composable
private fun RenameShoppingListDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var newName by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("עריכת שם רשימה") },
        text = {
            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("שם חדש") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (newName.isNotBlank()) {
                        onConfirm(newName.trim())
                    }
                }
            ) {
                Text("שמור")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ביטול")
            }
        }
    )
}

private fun formatTimestamp(timestamp: Long): String {
    val formatter = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale("he"))
    return formatter.format(java.util.Date(timestamp))
}