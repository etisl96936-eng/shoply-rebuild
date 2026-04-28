package com.shoply.app.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.shoply.app.data.ProductUiModel
import com.shoply.app.data.ShoppingItem
import com.shoply.app.data.ShoppingList
import com.shoply.app.ui.components.ErrorView
import com.shoply.app.ui.components.LoadingView
import com.shoply.app.ui.components.ProductPriceCard
import com.shoply.app.ui.components.ShoplyAlertDialog
import com.shoply.app.ui.components.ShoplyButton
import com.shoply.app.ui.components.ShoplyTextField
import com.shoply.app.ui.navigation.Screen
import com.shoply.app.ui.notifications.NotificationViewModel
import com.shoply.app.ui.notifications.NotificationsBell
import com.shoply.app.ui.state.UiState
import com.shoply.app.ui.theme.ShoplyResponsive
import com.shoply.app.ui.theme.ShoplySpacing
import com.shoply.app.ui.theme.rememberShoplyWindowSize
import com.shoply.app.viewmodel.AuthViewModel
import com.shoply.app.viewmodel.ShoppingViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.grid.GridItemSpan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: ShoppingViewModel,
    authViewModel: AuthViewModel,
    isAdmin: Boolean = false,
    displayName: String = "",
    activity: Activity
) {
    val catalogState by viewModel.catalogUiState.collectAsStateWithLifecycle()
    val shoppingListsState by viewModel.shoppingListsUiState.collectAsStateWithLifecycle()
    val activeListItemsState by viewModel.shoppingListItemsUiState.collectAsStateWithLifecycle()
    val comparisonStores by viewModel.comparisonStores.collectAsStateWithLifecycle()

    val notificationViewModel: NotificationViewModel = viewModel()
    val notificationsState by notificationViewModel.notificationsUiState.collectAsStateWithLifecycle()

    val unreadNotificationsCount = when (val state = notificationsState) {
        is UiState.Success -> state.data.count { !it.read }
        else -> 0
    }

    val windowSize = rememberShoplyWindowSize(activity)
    val gridColumns = ShoplyResponsive.gridColumns(windowSize)

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var showAddDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<ShoppingItem?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    var isActiveListHeaderExpanded by remember { mutableStateOf(true) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var visibleItemsCount by remember { mutableStateOf(15) }

    var activeListId by remember { mutableStateOf<String?>(null) }
    var activeListName by remember { mutableStateOf("") }

    var showChooseActiveListDialog by remember { mutableStateOf(false) }
    var catalogItemToAdd by remember { mutableStateOf<ShoppingItem?>(null) }
    var showAddToListDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadActiveShoppingLists()
        viewModel.loadComparisonStores()
        notificationViewModel.loadNotifications()
    }
    LaunchedEffect(navController.currentBackStackEntry) {
        notificationViewModel.loadNotifications()
    }

    LaunchedEffect(activeListId) {
        activeListId?.let { viewModel.setCurrentShoppingList(it) }
    }

    val closeDrawerAnd: (action: () -> Unit) -> Unit = { action ->
        scope.launch { drawerState.close() }
        action()
    }

    val activeListItemIds = when (val state = activeListItemsState) {
        is UiState.Success -> state.data.map { it.id }.toSet()
        else -> emptySet()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ShoplyDrawerContent(
                displayName = displayName,
                isAdmin = isAdmin,
                onHomeClick = { closeDrawerAnd { } },
                onProfileClick = {
                    closeDrawerAnd { navController.navigate("profile") }
                },
                onStatsClick = {
                    closeDrawerAnd {
                        navController.navigate(Screen.Stats.route + "/$isAdmin")
                    }
                },
                onCompletedListsClick = {
                    closeDrawerAnd {
                        navController.navigate("completed_lists")
                    }
                },
                onMyListsClick = {
                    closeDrawerAnd {
                        navController.navigate(Screen.MyLists.route)
                    }
                },
                onSettingsClick = {
                    closeDrawerAnd {
                        // TODO: מסך הגדרות
                    }
                },

                onAdminUsersClick = {
                    closeDrawerAnd {
                        navController.navigate("admin_users")
                    }
                },
                onLogoutClick = {
                    closeDrawerAnd { showLogoutDialog = true }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "תפריט"
                            )
                        }
                    },
                    title = {
                        val title = if (displayName.isNotBlank()) {
                            "Shoply - $displayName"
                        } else {
                            "Shoply"
                        }
                        Text(title)
                    },
                    actions = {
                        NotificationsBell(
                            unreadCount = unreadNotificationsCount,
                            onClick = {
                                navController.navigate("notifications")
                            }
                        )

                        IconButton(onClick = { navController.navigate(Screen.MyLists.route) }) {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = "הרשימות שלי"
                            )
                        }

                        IconButton(onClick = { navController.navigate("profile") }) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "פרופיל"
                            )
                        }

                        IconButton(onClick = { showLogoutDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "התנתקות"
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                if (isAdmin) {
                    FloatingActionButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "הוסף מוצר")
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                when (val s = catalogState) {
                    is UiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            LoadingView(
                                message = "טוען את הקטלוג...",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }

                    is UiState.Error -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            ErrorView(
                                message = s.message,
                                onRetry = { },
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }

                    is UiState.Success -> {
                        val baseItems = s.data

                        val filteredItems = if (searchQuery.isBlank()) {
                            baseItems.filter { item ->
                                selectedCategory == null || item.category == selectedCategory
                            }
                        } else {
                            val query = searchQuery.trim()

                            val categoryFiltered = baseItems.filter { item ->
                                selectedCategory == null || item.category == selectedCategory
                            }

                            val startsWithMatches = categoryFiltered.filter { item ->
                                item.title.startsWith(query, ignoreCase = true)
                            }

                            val containsMatches = categoryFiltered.filter { item ->
                                !item.title.startsWith(query, ignoreCase = true) &&
                                        (
                                                item.title.contains(query, ignoreCase = true) ||
                                                        item.description.contains(query, ignoreCase = true)
                                                )
                            }

                            startsWithMatches + containsMatches
                        }

                        val visibleItems = filteredItems.take(visibleItemsCount)

                        LaunchedEffect(selectedCategory, searchQuery) {
                            visibleItemsCount = 15
                        }

                        val listState = rememberLazyListState()
                        val gridState = rememberLazyGridState()

                        LaunchedEffect(selectedCategory) {
                            listState.scrollToItem(0)
                            gridState.scrollToItem(0)
                        }

                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = ShoplySpacing.medium)
                                .padding(top = ShoplySpacing.small),
                            placeholder = { Text("חפש מוצר בקטלוג...") },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = "חיפוש")
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "נקה")
                                    }
                                }
                            },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium
                        )

                        Spacer(modifier = Modifier.height(ShoplySpacing.small))

                        CategoryChips(
                            allItems = baseItems,
                            selectedCategory = selectedCategory,
                            onCategorySelected = { selectedCategory = it }
                        )

                        Spacer(modifier = Modifier.height(ShoplySpacing.small))

                        ActiveListHeader(
                            shoppingListsState = shoppingListsState,
                            activeListName = activeListName,
                            isExpanded = isActiveListHeaderExpanded || activeListName.isBlank(),
                            onChooseListClick = {
                                viewModel.loadActiveShoppingLists()
                                showChooseActiveListDialog = true
                            },
                            onGoToMyListsClick = { navController.navigate(Screen.MyLists.route) }
                        )

                        Spacer(modifier = Modifier.height(ShoplySpacing.small))

                        if (filteredItems.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Text(
                                    text = if (baseItems.isEmpty()) {
                                        "אין פריטים בקטלוג. לחץ על ה־+ להוספה"
                                    } else {
                                        "לא נמצאו פריטים התואמים לחיפוש"
                                    },
                                    modifier = Modifier.align(Alignment.Center),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            if (windowSize.isCompact) {
                                LazyColumn(
                                    state = listState,
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(ShoplySpacing.medium),
                                    verticalArrangement = Arrangement.spacedBy(ShoplySpacing.small)
                                ) {
                                    items(visibleItems, key = { it.id }) { item ->
                                        ProductPriceCard(
                                            product = item.toProductUiModel(
                                                isInMyList = activeListItemIds.contains(item.id),
                                                comparisonStores = comparisonStores
                                            ),
                                            isAdmin = isAdmin,
                                            onToggleInList = {
                                                if (activeListId == null) {
                                                    viewModel.loadActiveShoppingLists()
                                                    showChooseActiveListDialog = true
                                                } else {
                                                    catalogItemToAdd = item
                                                    showAddToListDialog = true
                                                }
                                            },
                                            onDelete = { itemToDelete = item }
                                        )
                                    }

                                    if (visibleItemsCount < filteredItems.size) {
                                        item {
                                            Button(
                                                onClick = { visibleItemsCount += 15 },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = ShoplySpacing.small)
                                            ) {
                                                Text("טען עוד")
                                            }
                                        }
                                    }
                                }
                            } else {
                                LazyVerticalGrid(
                                    state = gridState,
                                    columns = GridCells.Fixed(gridColumns),
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(ShoplySpacing.medium),
                                    verticalArrangement = Arrangement.spacedBy(ShoplySpacing.small),
                                    horizontalArrangement = Arrangement.spacedBy(ShoplySpacing.small)
                                ) {
                                    items(visibleItems, key = { it.id }) { item ->
                                        ProductPriceCard(
                                            product = item.toProductUiModel(
                                                isInMyList = activeListItemIds.contains(item.id),
                                                comparisonStores = comparisonStores
                                            ),
                                            isAdmin = isAdmin,
                                            onToggleInList = {
                                                if (activeListId == null) {
                                                    showChooseActiveListDialog = true
                                                } else {
                                                    catalogItemToAdd = item
                                                    showAddToListDialog = true
                                                }
                                            },
                                            onDelete = { itemToDelete = item }
                                        )
                                    }

                                    if (visibleItemsCount < filteredItems.size) {
                                        item(span = { GridItemSpan(maxLineSpan) }) {
                                            Button(
                                                onClick = { visibleItemsCount += 15 },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = ShoplySpacing.small)
                                            ) {
                                                Text("טען עוד")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (showAddDialog) {
                AddItemDialog(
                    onDismiss = { showAddDialog = false },
                    onConfirm = { name, quantity, description, category ->
                        viewModel.addItem(name, quantity, description, category)
                        showAddDialog = false
                    }
                )
            }

            if (showChooseActiveListDialog) {
                ChooseActiveListDialog(
                    shoppingListsState = shoppingListsState,
                    currentUserId = viewModel.getCurrentUserId(),
                    currentActiveListId = activeListId,
                    onDismiss = { showChooseActiveListDialog = false },
                    onSelectList = { list ->
                        activeListId = list.id
                        activeListName = list.name
                        isActiveListHeaderExpanded = false
                        showChooseActiveListDialog = false
                    },
                    onGoToMyLists = {
                        showChooseActiveListDialog = false
                        navController.navigate(Screen.MyLists.route)
                    }
                )
            }

            if (showAddToListDialog && catalogItemToAdd != null && activeListId != null) {
                AddCatalogItemToListDialog(
                    itemTitle = catalogItemToAdd!!.title,
                    listName = activeListName,
                    onDismiss = {
                        showAddToListDialog = false
                        catalogItemToAdd = null
                    },
                    onConfirm = { quantity ->
                        viewModel.addCatalogItemToShoppingList(
                            listId = activeListId!!,
                            item = catalogItemToAdd!!,
                            quantity = quantity
                        )
                        showAddToListDialog = false
                        catalogItemToAdd = null
                    }
                )
            }

            itemToDelete?.let { item ->
                ShoplyAlertDialog(
                    title = "מחיקת מוצר",
                    message = "האם את בטוחה שברצונך למחוק את '${item.title}'?",
                    confirmText = "מחק",
                    dismissText = "ביטול",
                    onConfirm = {
                        viewModel.deleteItem(item.id)
                        itemToDelete = null
                    },
                    onDismiss = { itemToDelete = null }
                )
            }

            if (showLogoutDialog) {
                ShoplyAlertDialog(
                    title = "התנתקות",
                    message = "האם את מעוניינת לצאת מ-Shoply?",
                    confirmText = "כן",
                    dismissText = "לא",
                    onConfirm = {
                        showLogoutDialog = false
                        authViewModel.logout()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onDismiss = {
                        showLogoutDialog = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ActiveListHeader(
    shoppingListsState: UiState<List<ShoppingList>>,
    activeListName: String,
    isExpanded: Boolean,
    onChooseListClick: () -> Unit,
    onGoToMyListsClick: () -> Unit
) {
    when (shoppingListsState) {
        UiState.Loading -> {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ShoplySpacing.medium),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = "טוען רשימות...",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        is UiState.Error -> {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ShoplySpacing.medium),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.errorContainer
            ) {
                Text(
                    text = "לא ניתן לטעון רשימות",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        is UiState.Success -> {
            val activeLists = shoppingListsState.data.filter { list ->
                list.status == ShoppingList.STATUS_ACTIVE
            }
            if (activeLists.isEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ShoplySpacing.medium),
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "לא נבחרה עדיין רשימה פעילה",
                            style = MaterialTheme.typography.titleSmall
                        )

                        Text(
                            text = "הוספה מהקטלוג תתבצע לרשימה הפעילה.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = onChooseListClick) {
                                Text("בחרי רשימה")
                            }

                            OutlinedButton(onClick = onGoToMyListsClick) {
                                Text("ניהול רשימות")
                            }
                        }
                    }
                }
            } else if (isExpanded) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ShoplySpacing.medium),
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (activeListName.isBlank()) {
                                "לא נבחרה עדיין רשימה פעילה"
                            } else {
                                "רשימה פעילה: $activeListName"
                            },
                            style = MaterialTheme.typography.titleSmall
                        )

                        Text(
                            text = "הוספה מהקטלוג תתבצע לרשימה הפעילה.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = onChooseListClick) {
                                Text(if (activeListName.isBlank()) "בחרי רשימה" else "החליפי רשימה")
                            }

                            OutlinedButton(onClick = onGoToMyListsClick) {
                                Text("ניהול רשימות")
                            }
                        }
                    }
                }
            } else {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ShoplySpacing.medium),
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "רשימה פעילה:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        AssistChip(
                            onClick = onChooseListClick,
                            label = { Text(activeListName) }
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        TextButton(onClick = onChooseListClick) {
                            Text("החלפה")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChooseActiveListDialog(
    shoppingListsState: UiState<List<ShoppingList>>,
    currentUserId: String,
    currentActiveListId: String?,
    onDismiss: () -> Unit,
    onSelectList: (ShoppingList) -> Unit,
    onGoToMyLists: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("בחירת רשימה פעילה") },
        text = {
            when (shoppingListsState) {
                UiState.Loading -> {
                    CircularProgressIndicator()
                }

                is UiState.Error -> {
                    Text(shoppingListsState.message)
                }

                is UiState.Success -> {
                    val activeLists = shoppingListsState.data.filter { list ->
                        list.status == ShoppingList.STATUS_ACTIVE &&
                                (list.ownerUid == currentUserId || list.sharedWith.contains(currentUserId))
                    }
                    if (activeLists.isEmpty()) {
                        Text("אין עדיין רשימות פעילות")
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            activeLists.forEach { list ->
                                OutlinedButton(
                                    onClick = { onSelectList(list) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = if (currentActiveListId == list.id) {
                                            "✓ ${list.name}"
                                        } else {
                                            list.name
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("סגור")
            }
        },
        dismissButton = {
            TextButton(onClick = onGoToMyLists) {
                Text("לרשימות שלי")
            }
        }
    )
}

@Composable
private fun AddCatalogItemToListDialog(
    itemTitle: String,
    listName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var quantity by remember { mutableStateOf("1") }

    fun quantityAsInt(): Int =
        quantity.toIntOrNull()?.coerceAtLeast(1) ?: 1

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("הוספה לרשימה") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("מוצר: $itemTitle")
                Text("רשימה: $listName")

                Text(
                    text = "כמות",
                    style = MaterialTheme.typography.labelLarge
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            quantity = (quantityAsInt() - 1)
                                .coerceAtLeast(1)
                                .toString()
                        }
                    ) {
                        Text("-")
                    }

                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { newValue ->
                            quantity = newValue
                                .filter { it.isDigit() }
                                .take(3)
                        },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        textStyle = MaterialTheme.typography.titleMedium.copy(
                            textAlign = TextAlign.Center
                        )
                    )

                    OutlinedButton(
                        onClick = {
                            quantity = (quantityAsInt() + 1).toString()
                        }
                    ) {
                        Text("+")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(quantityAsInt().toString())
                }
            ) {
                Text("הוסף")
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
private fun ShoplyDrawerContent(
    displayName: String,
    isAdmin: Boolean,
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit,
    onStatsClick: () -> Unit,
    onCompletedListsClick: () -> Unit,
    onMyListsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onAdminUsersClick: () -> Unit,
) {
    ModalDrawerSheet {
        Column(
            modifier = Modifier.padding(ShoplySpacing.medium)
        ) {
            Text(
                text = "Shoply",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(
                    horizontal = ShoplySpacing.medium,
                    vertical = ShoplySpacing.small
                )
            )

            if (displayName.isNotBlank()) {
                Text(
                    text = "שלום, $displayName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(
                        horizontal = ShoplySpacing.medium,
                        vertical = ShoplySpacing.extraSmall
                    )
                )
            }

            if (isAdmin) {
                Text(
                    text = "מנהל מערכת",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(
                        horizontal = ShoplySpacing.medium,
                        vertical = ShoplySpacing.extraSmall
                    )
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = ShoplySpacing.small)
            )

            DrawerItem(
                icon = Icons.Default.Home,
                label = "דף הבית",
                onClick = onHomeClick
            )

            DrawerItem(
                icon = Icons.Default.Person,
                label = "פרופיל",
                onClick = onProfileClick
            )

            DrawerItem(
                icon = Icons.Default.BarChart,
                label = "סטטיסטיקות",
                onClick = onStatsClick
            )

            DrawerItem(
                icon = Icons.Default.History,
                label = "ארכיון רשימות",
                onClick = onCompletedListsClick
            )

            DrawerItem(
                icon = Icons.Default.List,
                label = "הרשימות שלי",
                onClick = onMyListsClick
            )

            DrawerItem(
                icon = Icons.Default.Settings,
                label = "הגדרות",
                onClick = onSettingsClick
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = ShoplySpacing.small)
            )

            DrawerItem(
                icon = Icons.Default.Logout,
                label = "התנתקות",
                onClick = onLogoutClick
            )

            if (isAdmin) {
                DrawerItem(
                    icon = Icons.Default.Person,
                    label = "ניהול משתמשים",
                    onClick = onAdminUsersClick
                )
            }
        }
    }
}

@Composable
private fun DrawerItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label) },
        selected = false,
        onClick = onClick,
        modifier = Modifier.padding(horizontal = ShoplySpacing.small)
    )
}

private fun ShoppingItem.toProductUiModel(
    isInMyList: Boolean,
    comparisonStores: List<String>
): ProductUiModel {
    return ProductUiModel(
        id = id,
        title = title,
        category = category,
        quantityLabel = quantity,
        storePrices = storePrices.filter { it.storeName in comparisonStores },
        isInMyList = isInMyList
    )
}

@Composable
fun CategoryChips(
    allItems: List<ShoppingItem>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit
) {
    val availableCategories = allItems
        .map { it.category }
        .distinct()
        .sorted()

    if (availableCategories.isNotEmpty()) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = ShoplySpacing.medium),
            horizontalArrangement = Arrangement.spacedBy(ShoplySpacing.small)
        ) {
            item {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { onCategorySelected(null) },
                    label = { Text("הכל") }
                )
            }

            items(availableCategories) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = {
                        onCategorySelected(if (selectedCategory == category) null else category)
                    },
                    label = { Text(category) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var description by remember { mutableStateOf("") }

    val categories = listOf(
        "פירות וירקות",
        "מוצרי חלב וביצים",
        "ניקיון והיגיינה",
        "מאפה ודגנים",
        "שימורים ומזווה",
        "בשר ודגים",
        "מוצרי מקפיא",
        "אחר"
    )
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(categories[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("הוספת מוצר חדש", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                ShoplyTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "שם המוצר *"
                )
                Spacer(modifier = Modifier.height(ShoplySpacing.small))

                ShoplyTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = "כמות"
                )
                Spacer(modifier = Modifier.height(ShoplySpacing.medium))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("בחר קטגוריה") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(ShoplySpacing.small))
                ShoplyTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "תיאור (אופציונלי)"
                )
            }
        },
        confirmButton = {
            ShoplyButton(
                text = "הוסף",
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, quantity, description, selectedCategory)
                    }
                }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ביטול")
            }
        }
    )
}