package com.example.uiapp.ui.advancedinput

/**
 * Tanggal Gregorian sederhana untuk lab picker. Perhitungan murni Kotlin
 * (algoritma epoch-day Howard Hinnant) sehingga jalan sama di Android dan iOS
 * tanpa `java.time` / `kotlinx-datetime`.
 */
internal data class CalDate(
    val year: Int,
    val month: Int,
    val day: Int,
) : Comparable<CalDate> {

    override fun compareTo(other: CalDate): Int = when {
        year != other.year -> year - other.year
        month != other.month -> month - other.month
        else -> day - other.day
    }
}

internal object CalendarMath {

    val monthNames = listOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember",
    )

    val monthShort = listOf(
        "Jan", "Feb", "Mar", "Apr", "Mei", "Jun",
        "Jul", "Agu", "Sep", "Okt", "Nov", "Des",
    )

    // Indeks 0 = Senin ... 6 = Minggu (mengikuti kebiasaan kalender Indonesia).
    val weekdayShort = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")

    val weekdayFull = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")

    fun isLeapYear(year: Int): Boolean =
        (year % 4 == 0 && year % 100 != 0) || year % 400 == 0

    fun daysInMonth(year: Int, month: Int): Int = when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        else -> if (isLeapYear(year)) 29 else 28
    }

    /** Hari sejak 1970-01-01 (boleh negatif). */
    fun epochDay(year: Int, month: Int, day: Int): Long {
        var y = year.toLong()
        var m = month.toLong()
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val era = (if (y >= 0) y else y - 399) / 400
        val yoe = y - era * 400
        val doy = (153 * (m - 3) + 2) / 5 + day - 1
        val doe = yoe * 365 + yoe / 4 - yoe / 100 + doy
        return era * 146097 + doe - 719468
    }

    fun epochDay(date: CalDate): Long = epochDay(date.year, date.month, date.day)

    fun fromEpochDay(epochDay: Long): CalDate {
        val z = epochDay + 719468
        val era = (if (z >= 0) z else z - 146096) / 146097
        val doe = z - era * 146097
        val yoe = (doe - doe / 1460 + doe / 36524 - doe / 146096) / 365
        val y = yoe + era * 400
        val doy = doe - (365 * yoe + yoe / 4 - yoe / 100)
        val mp = (5 * doy + 2) / 153
        val d = (doy - (153 * mp + 2) / 5 + 1).toInt()
        val m = (mp + if (mp < 10) 3 else -9).toInt()
        return CalDate(
            year = (if (m <= 2) y + 1 else y).toInt(),
            month = m,
            day = d,
        )
    }

    /** 0 = Senin ... 6 = Minggu. */
    fun weekdayIndex(date: CalDate): Int =
        (((epochDay(date) + 3) % 7 + 7) % 7).toInt()

    fun plusDays(date: CalDate, days: Long): CalDate = fromEpochDay(epochDay(date) + days)

    fun plusMonths(year: Int, month: Int, delta: Int): Pair<Int, Int> {
        val total = year * 12 + (month - 1) + delta
        return (total / 12) to (total % 12 + 1)
    }

    /** Selisih bulan antara dua tanggal (diabaikan harinya). */
    fun monthsBetween(from: CalDate, to: CalDate): Int =
        (to.year * 12 + to.month) - (from.year * 12 + from.month)

    fun daysBetween(from: CalDate, to: CalDate): Long = epochDay(to) - epochDay(from)

    fun formatLong(date: CalDate): String =
        "${weekdayFull[weekdayIndex(date)]}, ${date.day} ${monthNames[date.month - 1]} ${date.year}"

    fun formatShort(date: CalDate): String =
        "${date.day} ${monthShort[date.month - 1]} ${date.year}"

    fun formatMonthYear(year: Int, month: Int): String = "${monthNames[month - 1]} $year"

    /** 42 sel (6 baris x 7 kolom) untuk grid bulan, termasuk tanggal bulan tetangga. */
    fun monthGrid(year: Int, month: Int): List<CalDate> {
        val first = CalDate(year, month, 1)
        val lead = weekdayIndex(first)
        val start = plusDays(first, -lead.toLong())
        return (0 until 42).map { plusDays(start, it.toLong()) }
    }
}
