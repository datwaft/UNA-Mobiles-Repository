package org.una.mobile.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Immutable
@Serializable
data class Purchase(
    val identifier: Long,
    @SerialName("timestamp")
    val rawTimestamp: String,
    val origin: String,
    val destination: String,
    @SerialName("outbound_date")
    val rawOutboundDate: String,
    @SerialName("inbound_date")
    val rawInboundDate: String? = null,
    @SerialName("ticket_amount")
    val ticketAmount: Int,
    @SerialName("total_cost")
    val totalCost: Double,
    @SerialName("has_been_reserved")
    val hasBeenReserved: Boolean,
    @SerialName("plane_rows")
    val planeRows: Int,
    @SerialName("plane_columns")
    val planeColumns: Int,
    val flight: Long,
) {
    @Transient
    private val currencyFormatter = DecimalFormat("#,###.##")

    val timestamp: LocalDateTime
        get() = LocalDateTime.parse(rawTimestamp)
    val outboundDate: LocalDate
        get() = LocalDate.parse(rawOutboundDate)
    val inboundDate: LocalDate?
        get() = rawInboundDate?.let { LocalDate.parse(rawInboundDate) }

    val route: String
        get() = "$origin - $destination"

    private val formattedOutboundDate: String
        get() = outboundDate.format(DateTimeFormatter.ISO_DATE)

    private val formattedInboundDate: String
        get() = inboundDate?.format(DateTimeFormatter.ISO_DATE) ?: "N/A"

    val formattedDate: String
        get() = "$formattedOutboundDate - $formattedInboundDate"

    val formattedTicketAmount: String
        get() = "$ticketAmount tickets"

    val formattedTotalCost: String
        get() = "$${currencyFormatter.format(totalCost)} in total"

    fun contains(query: String): Boolean =
        route.contains(query, true) || formattedDate.contains(query, true) ||
                formattedTicketAmount.contains(query, true) ||
                formattedTotalCost.contains(query, true)
}