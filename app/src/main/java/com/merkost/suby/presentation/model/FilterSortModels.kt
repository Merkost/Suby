package com.merkost.suby.presentation.model

enum class SortState {
    NONE, ASCENDING, DESCENDING
}

enum class SortDirection {
    ASCENDING, DESCENDING;

    val sortState: SortState
        get() = when (this) {
            ASCENDING -> SortState.ASCENDING
            DESCENDING -> SortState.DESCENDING
        }
}

data class SelectedSortState(
    val selectedOption: SortOption,
    val direction: SortDirection
)

enum class SortOption(val displayName: String) {
    NAME("Name"),
    PRICE("Price"),
    STATUS("Status"),
    PAYMENT_DATE("Payment Date");

    override fun toString() = displayName
}

enum class FilterOption(val displayName: String) {
    ALL("All"),
    ACTIVE("Active"),
    CANCELLED("Cancelled"),
    EXPIRED("Expired"),
    TRIAL("Trial");

    override fun toString() = displayName
} 