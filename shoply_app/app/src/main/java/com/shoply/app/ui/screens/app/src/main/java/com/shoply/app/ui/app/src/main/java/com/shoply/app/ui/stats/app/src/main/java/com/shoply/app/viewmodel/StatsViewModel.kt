package com.shoply.app.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.shoply.app.data.CompletedShoppingList
import com.shoply.app.data.repository.ShoppingRepository
import com.shoply.app.ui.stats.StatsData
import com.shoply.app.ui.state.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope

@OptIn(ExperimentalCoroutinesApi::class)
class StatsViewModel(
    private val repository: ShoppingRepository = ShoppingRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val currentUidFlow: Flow<String?> = flowOf(auth.currentUser?.uid)
    val statsUiState: StateFlow<UiState<StatsData>> =
        currentUidFlow.flatMapLatest { uid ->
            if (uid.isNullOrBlank()) {
                flowOf<UiState<StatsData>>(
                    UiState.Error("משתמש לא מחובר")
                )
            } else {
                repository.getCompletedShoppingListsFlow(uid)
                    .map< List<CompletedShoppingList>, UiState<StatsData> > { completedLists ->
                        UiState.Success(calculateStats(completedLists))
                    }
                    .catch { error ->
                        emit(
                            UiState.Error(
                                error.message ?: "שגיאה בטעינת נתוני הסטטיסטיקות"
                            )
                        )
                    }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )
    private fun calculateStats(completedLists: List<CompletedShoppingList>): StatsData {
        val totalSpent = completedLists.sumOf { it.totalAmount }

        val categoryTotals = completedLists
            .flatMap { list ->
                list.items.map { item ->
                    val quantity = item.quantity.toIntOrNull() ?: 1
                    val itemPrice = item.storePrices
                        .firstOrNull { it.storeName == list.selectedStore }
                        ?.price ?: 0.0

                    item.category to itemPrice * quantity
                }
            }
            .groupBy({ it.first }, { it.second })
            .mapValues { entry -> entry.value.sum() }

        val listTotals = completedLists.associate { list ->
            list.name to list.totalAmount
        }

        val topCategory = categoryTotals.maxByOrNull { it.value }?.key ?: ""

        val highestListName = completedLists
            .maxByOrNull { it.totalAmount }
            ?.name ?: ""

        return StatsData(
            totalSpent = totalSpent,
            completedListsCount = completedLists.size,
            categoryTotals = categoryTotals,
            listTotals = listTotals,
            topCategory = topCategory,
            highestListName = highestListName
        )
    }
}