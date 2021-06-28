package org.una.mobile.viewmodel.form

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import org.una.mobile.R
import org.una.mobile.model.Flight

class PurchaseFormViewModel : ViewModel() {
    var flight: Flight? by mutableStateOf(null)
    var selectedPaymentMethod: String? by mutableStateOf("")
    var rawTicketNumber: String by mutableStateOf("")
    val ticketNumber: Int? by derivedStateOf { rawTicketNumber.toIntOrNull() }

    val isTicketNumberValid: Boolean by derivedStateOf {
        if (ticketNumber == null) true
        else if (flight == null) false
        else ticketNumber!! <= flight?.availableTickets ?: 0 && ticketNumber!! > 0
    }

    val ticketNumberErrorMessage: Int? by derivedStateOf {
        when {
            // No error message when ticketNumber is not a number or is valid
            flight == null || ticketNumber == null || isTicketNumberValid -> null
            // Error message when ticketNumber is bigger that the available seat number
            ticketNumber!! > flight!!.availableTickets -> R.string.field_ticketNumber_error_bigger
            // Error message when ticketNumber is zero
            ticketNumber!! == 0 -> R.string.field_ticketNumber_error_zero
            // Error message when ticketNumber is negative
            ticketNumber!! < 0 -> R.string.field_ticketNumber_error_negative
            // Any other case, no error message
            else -> null
        }
    }

    val isValid: Boolean by derivedStateOf {
        selectedPaymentMethod != null && ticketNumber != null && isTicketNumberValid
    }

    val totalCost: Double? by derivedStateOf {
        when {
            flight == null || ticketNumber == null -> null
            else -> flight!!.ticketPrice * ticketNumber!!
        }
    }

    fun clear() {
        rawTicketNumber = ""
        selectedPaymentMethod = null
    }
}