package com.shoply.app.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.shoply.app.ui.state.UiState
import com.shoply.app.viewmodel.ShoppingViewModel
import com.shoply.app.data.ShoppingItem
import com.shoply.app.ui.components.*
import com.shoply.app.ui.theme.ShoplyResponsive
import com.shoply.app.ui.theme.ShoplySpacing
import com.shoply.app.ui.theme.rememberShoplyWindowSize
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.Logout
import com.shoply.app.viewmodel.AuthViewModel

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
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // אבן דרך 4.3 - Responsive Design
    val windowSize = rememberShoplyWindowSize(activity)
    val gridColumns = ShoplyResponsive.gridColumns(windowSize)

    // מצבי ניהול הדיאלוגים
    var showAddDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<ShoppingItem?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // חיפוש וסינון
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    // טאב נוכחי: 0 = כל המוצרים, 1 = הרשימה שלי
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = if (displayName.isNotBlank()) {
                        "Shoply - $displayName"
                    } else {
                        "Shoply"
                    }

                    Text(title)
                },
                actions = {
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
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text("כל המוצרים") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = {
                        BadgedBox(
                            badge = {
                                when (val s = state) {
                                    is UiState.Success -> {
                                        val checkedCount = s.data.count { it.isChecked }
                                        if (checkedCount > 0) {
                                            Badge { Text("$checkedCount") }
                                        }
                                    }
                                    else -> {}
                                }
                            }
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null)
                        }
                    },
                    label = { Text("הרשימה שלי") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
            }
        },
        floatingActionButton = {
            // הכפתור + מופיע רק בטאב "כל המוצרים"
            if (selectedTab == 0) {
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
            when (val s = state) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        LoadingView(
                            message = "טוען את הרשימה שלך...",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
                is UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        ErrorView(
                            message = s.message,
                            onRetry = { /* אופציונלי: קריאה לטעינה מחדש */ },
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
                is UiState.Success -> {
                    val allItems = s.data

                    // בחירת המוצרים לפי הטאב
                    val baseItems = if (selectedTab == 0) {
                        allItems // כל המוצרים
                    } else {
                        allItems.filter { it.isChecked } // רק מסומנים
                    }

                    // סינון הפריטים לפי חיפוש וקטגוריה
                    val filteredItems = baseItems.filter { item ->
                        val matchesSearch = if (searchQuery.isBlank()) {
                            true
                        } else {
                            item.title.contains(searchQuery, ignoreCase = true) ||
                                    item.description.contains(searchQuery, ignoreCase = true)
                        }

                        val matchesCategory = if (selectedCategory == null) {
                            true
                        } else {
                            item.category == selectedCategory
                        }

                        matchesSearch && matchesCategory
                    }

                    // שורת חיפוש
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = ShoplySpacing.medium)
                            .padding(top = ShoplySpacing.small),
                        placeholder = { Text("חפש מוצר...") },
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

                    // סינון קטגוריות
                    CategoryChips(
                        allItems = baseItems, // רק מהקטגוריות הרלוונטיות לטאב
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it }
                    )

                    Spacer(modifier = Modifier.height(ShoplySpacing.small))

                    // תצוגת הרשימה
                    if (filteredItems.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = when {
                                    selectedTab == 1 && baseItems.isEmpty() ->
                                        "הרשימה שלך ריקה.\nסמני מוצרים מ'כל המוצרים' כדי להוסיף אותם לכאן"
                                    allItems.isEmpty() ->
                                        "אין פריטים ברשימה. לחצי על ה-+ להוספה"
                                    else ->
                                        "לא נמצאו פריטים התואמים לחיפוש"
                                },
                                modifier = Modifier.align(Alignment.Center),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    } else {
                        // אבן דרך 4.3 - בטלפון (Compact) מציגים רשימה רגילה,
                        // בטאבלט (Medium/Expanded) מציגים רשת של 2-3 עמודות
                        if (windowSize.isCompact) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(ShoplySpacing.medium),
                                verticalArrangement = Arrangement.spacedBy(ShoplySpacing.small)
                            ) {
                                items(filteredItems) { item ->
                                    ShoppingItemCard(
                                        item = item,
                                        isAdmin = isAdmin,
                                        selectedTab = selectedTab,
                                        onToggleChecked = { viewModel.toggleItemChecked(item) },
                                        onDelete = { itemToDelete = item }
                                    )
                                }
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(gridColumns),
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(ShoplySpacing.medium),
                                verticalArrangement = Arrangement.spacedBy(ShoplySpacing.small),
                                horizontalArrangement = Arrangement.spacedBy(ShoplySpacing.small)
                            ) {
                                items(filteredItems, key = { it.id }) { item ->
                                    ShoppingItemCard(
                                        item = item,
                                        isAdmin = isAdmin,
                                        selectedTab = selectedTab,
                                        onToggleChecked = { viewModel.toggleItemChecked(item) },
                                        onDelete = { itemToDelete = item }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // דיאלוג הוספת פריט
        if (showAddDialog) {
            AddItemDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name, quantity, description, category ->
                    viewModel.addItem(name, quantity, description, category)
                    showAddDialog = false
                }
            )
        }

        // דיאלוג אישור מחיקה
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

/**
 * כרטיס פריט בודד - משותף ל-LazyColumn וגם ל-LazyVerticalGrid
 */
@Composable
private fun ShoppingItemCard(
    item: ShoppingItem,
    isAdmin: Boolean,
    selectedTab: Int,
    onToggleChecked: () -> Unit,
    onDelete: () -> Unit
) {
    ShoplyCard {
        ListItem(
            headlineContent = {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (item.isChecked) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            },
            supportingContent = {
                Text("${item.quantity} | ${item.category}")
            },
            leadingContent = {
                Checkbox(
                    checked = item.isChecked,
                    onCheckedChange = { onToggleChecked() }
                )
            },
            trailingContent = {
                if (isAdmin && selectedTab == 0) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "מחק",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            colors = ListItemDefaults.colors(
                containerColor = androidx.compose.ui.graphics.Color.Transparent
            )
        )
    }
}

/**
 * קומפוננטת Chips לסינון קטגוריות
 */
@Composable
fun CategoryChips(
    allItems: List<ShoppingItem>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit
) {
    // קטגוריות שבאמת קיימות ברשימה
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
            // Chip "הכל"
            item {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { onCategorySelected(null) },
                    label = { Text("הכל") }
                )
            }

            // Chips לכל קטגוריה
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
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
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