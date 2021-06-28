package org.una.mobile.viewmodel.form

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import org.una.mobile.model.Purchase
import org.una.mobile.model.Ticket

class ReservationFormViewModel : ViewModel() {
    var purchase: Purchase? by mutableStateOf(null)
    val readOnly: Boolean by derivedStateOf { purchase?.hasBeenReserved ?: true }
    var selected: List<Pair<Int, Int>> by mutableStateOf(listOf())

    fun clear(items: List<Ticket>) {
        selected =
            selected.filter { (row, col) -> !items.any { it.row == row && it.column == col } }
    }

    fun clear() {
        selected = emptyList()
    }
}