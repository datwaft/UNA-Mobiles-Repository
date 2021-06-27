package org.una.mobile.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Immutable
@Serializable
data class Flight(
    val identifier: Long,
    val origin: String,
    val destination: String,
    @SerialName("outbound_date")
    val rawOutboundDate: String,
    @SerialName("inbound_date")
    val rawInboundDate: String? = null,
    @SerialName("passenger_amount")
    val passengerAmount: Int,
    @SerialName("passenger_total")
    val passengerTotal: Int,
    @SerialName("ticket_price")
    val ticketPrice: Double,
) {
    @Transient
    private val currencyFormatter = DecimalFormat("#,###.##")

    val outboundDate: LocalDate
        get() = LocalDate.parse(rawOutboundDate)
    val inboundDate: LocalDate?
        get() = rawInboundDate?.let { LocalDate.parse(rawInboundDate) }


    val availableTickets: Int
        get() = passengerTotal - passengerAmount

    val route: String
        get() = "$origin - $destination"

    private val formattedOutboundDate: String
        get() = outboundDate.format(DateTimeFormatter.ISO_DATE)

    private val formattedInboundDate: String
        get() = inboundDate?.format(DateTimeFormatter.ISO_DATE) ?: "N/A"

    val formattedDate: String
        get() = "$formattedOutboundDate - $formattedInboundDate"

    val formattedPassengers: String
        get() = "$passengerAmount/$passengerTotal"

    val formattedPrice: String
        get() = "$${currencyFormatter.format(ticketPrice)} per ticket"

    fun contains(query: String): Boolean =
        route.contains(query, true) || formattedDate.contains(query, true) ||
                formattedPassengers.contains(query, true) || formattedPrice.contains(query, true)
}