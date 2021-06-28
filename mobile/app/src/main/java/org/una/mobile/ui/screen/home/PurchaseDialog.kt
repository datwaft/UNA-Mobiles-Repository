package org.una.mobile.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import org.una.mobile.R
import org.una.mobile.model.Flight
import org.una.mobile.ui.components.Dropdown
import org.una.mobile.ui.components.NumericTextField
import org.una.mobile.viewmodel.form.PurchaseFormViewModel
import java.text.NumberFormat
import java.util.*

@ExperimentalAnimationApi
@Composable
fun PurchaseDialog(
    isVisible: Boolean, onVisibilityChange: (Boolean) -> Unit,
    onSubmit: (ticketNumber: Int, flight: Flight) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PurchaseFormViewModel = viewModel(),
) {
    val ticketNumberErrorMessage: String? =
        viewModel.ticketNumberErrorMessage?.let {
            stringResource(it, viewModel.flight?.availableTickets ?: "N/A")
        }

    AnimatedVisibility(isVisible && viewModel.flight != null) {
        AlertDialog(
            onDismissRequest = { onVisibilityChange(false) },
            dismissButton = {
                OutlinedButton(onClick = { onVisibilityChange(false) }) {
                    Text(stringResource(R.string.action_cancel_label).uppercase())
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (!viewModel.isValid) return@Button
                    onSubmit(viewModel.ticketNumber!!, viewModel.flight!!)
                    viewModel.clear()
                }) {
                    Text(stringResource(R.string.action_purchase_label).uppercase())
                }
            },
            title = {
                Text(stringResource(R.string.dialog_purchase_title))
            },
            text = {
                Content(
                    viewModel.selectedPaymentMethod, { viewModel.selectedPaymentMethod = it },
                    viewModel.rawTicketNumber, { viewModel.rawTicketNumber = it },
                    viewModel.isTicketNumberValid, ticketNumberErrorMessage,
                    viewModel.totalCost,
                )
            },
            modifier = modifier,
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false))
    }
}

@ExperimentalAnimationApi
@Composable
private fun Content(
    paymentMethod: String?, onPaymentMethodChange: (String?) -> Unit,
    ticketNumber: String, onTicketNumberChange: (String) -> Unit,
    isTicketNumberValid: Boolean, ticketNumberErrorMessage: String?,
    totalCost: Double?,
    modifier: Modifier = Modifier,
) {
    Column(modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column {
            NumericTextField(ticketNumber, onTicketNumberChange,
                label = stringResource(R.string.field_ticketNumber_label),
                isError = !isTicketNumberValid)
            AnimatedVisibility(ticketNumberErrorMessage != null) {
                Text(ticketNumberErrorMessage ?: "",
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.error)
            }
        }
        Dropdown(label = stringResource(R.string.field_paymentMethod_label),
            items = paymentMethods,
            placeholder = stringResource(R.string.field_paymentMethod_placeholder),
            selected = paymentMethod, onSelected = onPaymentMethodChange)
        NumericTextField(totalCost?.let { currencyFormat.format(totalCost) } ?: "N/A", {},
            readOnly = true,
            label = stringResource(R.string.field_price_label))
    }
}

private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
private val paymentMethods: List<String> =
    listOf("Paypal", "American Express", "Visa", "Credomatic")