package org.una.mobile.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.text.DecimalFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Immutable
@Serializable
data class Schedule(
    val identifier: Long,
    val origin: String,
    val destination: String,
    @SerialName("departure_time")
    val rawDepartureTime: String,
    val weekday: String,
    @SerialName("duration")
    val rawDuration: String,
    val price: Double,
    val discount: Double,
) {
    @Transient
    private val percentageFormatter = DecimalFormat("##")

    @Transient
    private val currencyFormatter = DecimalFormat("#,###.##")

    val departureTime: LocalTime
        get() = LocalTime.parse(rawDepartureTime)
    val duration: LocalTime
        get() = LocalTime.parse(rawDuration)

    val route: String
        get() = "$origin - $destination"

    private val capitalizedWeekday: String
        get() = weekday.replaceFirstChar { it.uppercase() }
    val formattedDepartureTime: String
        get() = "${departureTime.format(DateTimeFormatter.ofPattern("HH:mm"))} on ${capitalizedWeekday}s"

    val formattedDuration: String
        get() = "${duration.hour} ${if (duration.hour == 1) "hour" else "hours"}${if (duration.minute > 0) " and ${duration.minute} ${if (duration.minute == 1) "minute" else "minutes"}" else ""}"
    val formattedPrice: String
        get() = "$${currencyFormatter.format(price)} at ${percentageFormatter.format(discount * 100)}% discount"

    fun contains(query: String): Boolean =
        route.contains(query, true) || formattedDepartureTime.contains(query, true) ||
                formattedDuration.contains(query, true) || formattedPrice.contains(query, true)
}