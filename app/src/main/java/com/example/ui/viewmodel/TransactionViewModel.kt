package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.database.TransactionEntity
import com.example.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class FilterType {
    ALL, INCOME, EXPENSE
}

enum class SortType {
    NEWEST, OLDEST, HIGHEST_AMOUNT, LOWEST_AMOUNT
}

class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow(FilterType.ALL)
    val selectedFilter: StateFlow<FilterType> = _selectedFilter.asStateFlow()

    private val _selectedSort = MutableStateFlow(SortType.NEWEST)
    val selectedSort: StateFlow<SortType> = _selectedSort.asStateFlow()

    private val _allTransactions = repository.allTransactions

    val uiTransactions: StateFlow<List<TransactionEntity>> = combine(
        _allTransactions,
        _searchQuery,
        _selectedFilter,
        _selectedSort
    ) { transactions, query, filter, sort ->
        var list = transactions

        if (query.isNotBlank()) {
            list = list.filter { it.title.contains(query, ignoreCase = true) }
        }

        list = when (filter) {
            FilterType.ALL -> list
            FilterType.INCOME -> list.filter { it.isIncome }
            FilterType.EXPENSE -> list.filter { !it.isIncome }
        }

        list = when (sort) {
            SortType.NEWEST -> list.sortedByDescending { it.timestamp }
            SortType.OLDEST -> list.sortedBy { it.timestamp }
            SortType.HIGHEST_AMOUNT -> list.sortedByDescending { it.amount }
            SortType.LOWEST_AMOUNT -> list.sortedBy { it.amount }
        }

        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val dashboardTotals: StateFlow<DashboardTotals> = combine(_allTransactions, _allTransactions) { transactions, _ ->
        var totalIncome = 0L
        var totalExpense = 0L
        for (t in transactions) {
            if (t.isIncome) {
                totalIncome += t.amount
            } else {
                totalExpense += t.amount
            }
        }
        DashboardTotals(
            totalBalance = totalIncome - totalExpense,
            totalIncome = totalIncome,
            totalExpense = totalExpense
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardTotals()
    )

    val transactionTitle = MutableStateFlow("")
    val transactionAmount = MutableStateFlow("")
    val isIncomeInput = MutableStateFlow(false)

    private val _validationError = MutableStateFlow<String?>(null)
    val validationError: StateFlow<String?> = _validationError.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilter(filter: FilterType) {
        _selectedFilter.value = filter
    }

    fun setSort(sort: SortType) {
        _selectedSort.value = sort
    }

    fun clearValidationError() {
        _validationError.value = null
    }

    fun addTransaction(): Boolean {
        val title = transactionTitle.value.trim()
        val amountStr = transactionAmount.value.trim()
        val isIncome = isIncomeInput.value

        if (title.isEmpty()) {
            _validationError.value = "لطفاً عنوان تراکنش را وارد کنید."
            return false
        }

        if (amountStr.isEmpty()) {
            _validationError.value = "لطفاً مبلغ تراکنش را وارد کنید."
            return false
        }

        val normalizedAmountStr = amountStr.replace('۰', '0')
            .replace('۱', '1')
            .replace('۲', '2')
            .replace('۳', '3')
            .replace('۴', '4')
            .replace('۵', '5')
            .replace('۶', '6')
            .replace('۷', '7')
            .replace('۸', '8')
            .replace('۹', '9')
            .replace('٠', '0')
            .replace('١', '1')
            .replace('٢', '2')
            .replace('٣', '3')
            .replace('٤', '4')
            .replace('٥', '5')
            .replace('٦', '6')
            .replace('٧', '7')
            .replace('٨', '8')
            .replace('٩', '9')

        val amount = normalizedAmountStr.toLongOrNull()
        if (amount == null || amount <= 0L) {
            _validationError.value = "مبلغ وارد شده باید معتبر و بزرگتر از صفر باشد."
            return false
        }

        viewModelScope.launch {
            repository.insert(
                TransactionEntity(
                    title = title,
                    amount = amount,
                    isIncome = isIncome
                )
            )
            transactionTitle.value = ""
            transactionAmount.value = ""
            isIncomeInput.value = false
            _validationError.value = null
        }
        return true
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            repository.delete(id)
        }
    }
}

data class DashboardTotals(
    val totalBalance: Long = 0L,
    val totalIncome: Long = 0L,
    val totalExpense: Long = 0L
)

class TransactionViewModelFactory(private val repository: TransactionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
