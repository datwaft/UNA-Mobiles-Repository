package org.una.mobile.ui.screen

import android.util.Log
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import org.una.mobile.R
import org.una.mobile.model.Ticket
import org.una.mobile.ui.screen.SeatState.*
import org.una.mobile.ui.theme.Theme
import org.una.mobile.viewmodel.TicketViewModel
import org.una.mobile.viewmodel.form.ReservationFormViewModel

@Composable
fun ReservationScreen(
    onReturn: () -> Unit,
    modifier: Modifier = Modifier,
    reservationFormViewModel: ReservationFormViewModel = viewModel(),
    ticketViewModel: TicketViewModel = viewModel(),
) {
    val occupied: List<Ticket> by ticketViewModel.flightItems.observeAsState(emptyList())
    val tickets: List<Ticket> by ticketViewModel.purchaseItems.observeAsState(emptyList())
    LaunchedEffect(occupied) {
        reservationFormViewModel.clear(occupied)
    }

    if (reservationFormViewModel.purchase != null) {
        ReservationScreen(
            ticketNumber = reservationFormViewModel.purchase!!.ticketAmount,
            rowNumber = reservationFormViewModel.purchase!!.planeRows,
            columnNumber = reservationFormViewModel.purchase!!.planeColumns,
            occupied = occupied.map { it.row to it.column },
            selected = if (reservationFormViewModel.readOnly) tickets.map { it.row to it.column } else reservationFormViewModel.selected,
            onSelectedChange = { reservationFormViewModel.selected = it },
            onConfirm = {
                if (reservationFormViewModel.purchase != null) {
                    ticketViewModel.create(reservationFormViewModel.purchase!!.identifier,
                        reservationFormViewModel.purchase!!.flight,
                        it)
                }
                onReturn()
            },
            onReturn = onReturn,
            readOnly = reservationFormViewModel.readOnly,
            modifier = modifier)
    }
}

@Composable
fun ReservationScreen(
    ticketNumber: Int, // How many tickets must be bought
    rowNumber: Int, columnNumber: Int,
    occupied: List<Pair<Int, Int>>,
    selected: List<Pair<Int, Int>>,
    onSelectedChange: (List<Pair<Int, Int>>) -> Unit,
    onConfirm: (selected: List<Pair<Int, Int>>) -> Unit,
    onReturn: () -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
) {
    val coroutineScope = rememberCoroutineScope()
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    Column(modifier
        .verticalScroll(verticalScrollState)
        .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {

        Card(modifier
            .padding(top = 16.dp)
            .padding(horizontal = 16.dp)) {
            Column(Modifier
                .padding(16.dp)
                .horizontalScroll(horizontalScrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)) {

                (1..columnNumber).forEach { col ->
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        (1..rowNumber).forEach { row ->
                            Seat(
                                state = when {
                                    selected.contains(row to col) -> SELECTED
                                    occupied.contains(row to col) -> OCCUPIED
                                    else -> SELECTABLE
                                },
                                onPress = {
                                    onSelectedChange(when {
                                        selected.contains(row to col) -> selected.filter { it != (row to col) }
                                        else -> if (selected.size < ticketNumber) selected + (row to col) else selected
                                    })
                                },
                                readOnly = readOnly,
                            )
                        }
                    }
                    if ((columnNumber % 2 == 0 && col == columnNumber / 2) || (columnNumber % 2 == 1 && col % 3 == 0 && col != columnNumber)) {
                        Spacer(modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp))
                    }
                }

            }
        }

        if (!readOnly) {
            Text("${selected.size} of $ticketNumber seats")
        }

        Row(Modifier
            .padding(bottom = 16.dp)
            .padding(horizontal = 16.dp)
            .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround) {
            OutlinedButton(onClick = onReturn) {
                Text(stringResource(when {
                    readOnly -> R.string.action_return_label
                    else -> R.string.action_cancel_label
                }))
            }
            if (!readOnly) {
                Button(onClick = {
                    onConfirm(selected)
                    onSelectedChange(emptyList())
                    coroutineScope.launch {
                        verticalScrollState.animateScrollTo(0)
                        horizontalScrollState.animateScrollTo(0)
                    }
                }, enabled = selected.size == ticketNumber) {
                    Text(stringResource(R.string.action_purchase_label))
                }
            }
        }
    }
}

@Composable
private fun Seat(
    state: SeatState,
    onPress: () -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
) {
    val transition = updateTransition(state, label = "SEAT")
    val color: Color by transition.animateColor(label = "COLOR") {
        when (it) {
            SELECTABLE -> Color.Gray
            OCCUPIED -> MaterialTheme.colors.error
            SELECTED -> MaterialTheme.colors.primary
        }
    }
    val contentColor: Color by transition.animateColor(label = "CONTENT_COLOR") {
        when (it) {
            SELECTABLE -> contentColorFor(Color.Gray)
            OCCUPIED -> contentColorFor(MaterialTheme.colors.error)
            SELECTED -> contentColorFor(MaterialTheme.colors.primary)
        }
    }

    Surface(
        modifier = modifier.size(48.dp),
        elevation = 8.dp,
        color = color,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.small
    ) {
        IconButton(onPress, Modifier.fillMaxSize(), enabled = state != OCCUPIED && !readOnly) {
            Icon(when (state) {
                SELECTABLE -> Icons.Filled.RadioButtonUnchecked
                OCCUPIED -> Icons.Filled.Close
                SELECTED -> Icons.Filled.Done
            }, stringResource(R.string.action_selectSeat_description))
        }
    }
}

private enum class SeatState {
    SELECTABLE, OCCUPIED, SELECTED
}

@Preview(showBackground = true)
@Composable
private fun SeatPreview() {
    var selected: Boolean by remember { mutableStateOf(false) }

    Theme {
        Card(Modifier.padding(16.dp)) {
            Column(Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Row {
                    Seat(SELECTABLE,
                        { Log.d("PREVIEW", "Emitted onPress event") },
                        Modifier.padding(16.dp))
                    Seat(OCCUPIED,
                        { Log.d("PREVIEW", "Emitted onPress event") },
                        Modifier.padding(16.dp))
                    Seat(SELECTED,
                        { Log.d("PREVIEW", "Emitted onPress event") },
                        Modifier.padding(16.dp))
                }
                Row {
                    Seat(SELECTABLE,
                        { Log.d("PREVIEW", "Emitted onPress event") },
                        Modifier.padding(16.dp), readOnly = true)
                    Seat(OCCUPIED,
                        { Log.d("PREVIEW", "Emitted onPress event") },
                        Modifier.padding(16.dp), readOnly = true)
                    Seat(SELECTED,
                        { Log.d("PREVIEW", "Emitted onPress event") },
                        Modifier.padding(16.dp), readOnly = true)
                }
                Seat(if (selected) SELECTED else SELECTABLE,
                    { selected = !selected },
                    Modifier.padding(16.dp))
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    val cols = 30
    val rows = 9
    val occupied: List<Pair<Int, Int>> = listOf(
        3 to 3,
        4 to 4,
    )
    var selected: List<Pair<Int, Int>> by remember { mutableStateOf(emptyList()) }

    Theme {
        Surface(color = MaterialTheme.colors.background) {
            ReservationScreen(
                ticketNumber = 12,
                rowNumber = rows,
                columnNumber = cols,
                selected = selected, onSelectedChange = { selected = it },
                occupied = occupied,
                onConfirm = { Log.d("PREVIEW", "Emitted confirm event") },
                onReturn = { Log.d("PREVIEW", "Emitted cancel event") }
            )
        }
    }
}

@Preview
@Composable
private fun ReadOnlyPreview() {
    val cols = 30
    val rows = 9
    val occupied: List<Pair<Int, Int>> = listOf(
        3 to 3,
        4 to 4,
    )
    var selected: List<Pair<Int, Int>> = listOf(
        2 to 2,
        2 to 3,
        3 to 2,
        5 to 5,
    )

    Theme {
        Surface(color = MaterialTheme.colors.background) {
            ReservationScreen(
                ticketNumber = 12,
                rowNumber = rows,
                columnNumber = cols,
                selected = selected, onSelectedChange = { selected = it },
                occupied = occupied,
                onConfirm = { Log.d("PREVIEW", "Emitted confirm event") },
                onReturn = { Log.d("PREVIEW", "Emitted cancel event") },
                readOnly = true,
            )
        }
    }
}