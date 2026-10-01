package com.example.uiapp.ui.advancedinput

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Perhitungan kalender lab `advanced_input` dipakai oleh wheel picker, strip tanggal,
 * dan kalender inline. Karena murni Kotlin (tanpa `java.time`), hasilnya wajib sama di
 * Android dan iOS — test ini mengunci perilaku tersebut.
 */
class CalendarMathTest {

    @Test
    fun leapYearFollowsGregorianRule() {
        assertTrue(CalendarMath.isLeapYear(2024))
        assertTrue(CalendarMath.isLeapYear(2000))
        assertFalse(CalendarMath.isLeapYear(2100))
        assertFalse(CalendarMath.isLeapYear(2026))
    }

    @Test
    fun daysInMonthHandlesFebruaryAndLongMonths() {
        assertEquals(31, CalendarMath.daysInMonth(2026, 1))
        assertEquals(28, CalendarMath.daysInMonth(2026, 2))
        assertEquals(29, CalendarMath.daysInMonth(2024, 2))
        assertEquals(28, CalendarMath.daysInMonth(2100, 2))
        assertEquals(29, CalendarMath.daysInMonth(2000, 2))
        assertEquals(30, CalendarMath.daysInMonth(2026, 4))
        assertEquals(31, CalendarMath.daysInMonth(2026, 12))
    }

    @Test
    fun epochDayMatchesKnownAnchors() {
        assertEquals(0L, CalendarMath.epochDay(1970, 1, 1))
        assertEquals(10957L, CalendarMath.epochDay(2000, 1, 1))
    }

    @Test
    fun epochDayRoundTripsForSampleDates() {
        val samples = listOf(
            CalDate(1970, 1, 1),
            CalDate(2000, 2, 29),
            CalDate(2024, 2, 29),
            CalDate(2026, 1, 1),
            CalDate(2026, 12, 31),
            CalDate(2100, 3, 1),
        )
        samples.forEach { date ->
            assertEquals(date, CalendarMath.fromEpochDay(CalendarMath.epochDay(date)), "round-trip $date")
        }
    }

    @Test
    fun weekdayIndexMatchesKnownWeekdays() {
        // 0 = Senin ... 6 = Minggu.
        assertEquals(3, CalendarMath.weekdayIndex(CalDate(2026, 1, 1))) // Kamis
        assertEquals(5, CalendarMath.weekdayIndex(CalDate(2000, 1, 1))) // Sabtu
        assertEquals(0, CalendarMath.weekdayIndex(CalDate(2026, 6, 1))) // Senin
    }

    @Test
    fun monthGridAlwaysHas42CellsStartingOnMonday() {
        listOf(2026 to 1, 2026 to 2, 2026 to 6, 2024 to 2, 2100 to 2).forEach { (year, month) ->
            val grid = CalendarMath.monthGrid(year, month)
            assertEquals(42, grid.size, "ukuran grid $month/$year")
            assertEquals(0, CalendarMath.weekdayIndex(grid.first()), "sel pertama harus Senin")
            assertTrue(
                grid.contains(CalDate(year, month, 1)),
                "grid $month/$year harus memuat tanggal 1",
            )
        }
    }

    @Test
    fun monthGridStartsExactlyOnFirstDayWhenMonthBeginsOnMonday() {
        // 1 Juni 2026 jatuh pada hari Senin.
        assertEquals(CalDate(2026, 6, 1), CalendarMath.monthGrid(2026, 6).first())
    }

    @Test
    fun plusDaysCrossesMonthAndLeapDay() {
        assertEquals(CalDate(2024, 2, 29), CalendarMath.plusDays(CalDate(2024, 2, 28), 1))
        assertEquals(CalDate(2026, 3, 1), CalendarMath.plusDays(CalDate(2026, 2, 28), 1))
        assertEquals(CalDate(2026, 1, 1), CalendarMath.plusDays(CalDate(2025, 12, 31), 1))
        assertEquals(CalDate(2025, 12, 31), CalendarMath.plusDays(CalDate(2026, 1, 1), -1))
    }

    @Test
    fun plusMonthsWrapsAcrossYearBoundary() {
        assertEquals(2026 to 1, CalendarMath.plusMonths(2025, 12, 1))
        assertEquals(2025 to 12, CalendarMath.plusMonths(2026, 1, -1))
        assertEquals(2027 to 1, CalendarMath.plusMonths(2026, 3, 10))
        assertEquals(2026 to 6, CalendarMath.plusMonths(2026, 6, 0))
    }

    @Test
    fun monthsAndDaysBetweenCountCalendarDistance() {
        assertEquals(3, CalendarMath.monthsBetween(CalDate(2025, 11, 30), CalDate(2026, 2, 1)))
        assertEquals(30L, CalendarMath.daysBetween(CalDate(2026, 1, 1), CalDate(2026, 1, 31)))
        assertEquals(0L, CalendarMath.daysBetween(CalDate(2026, 1, 1), CalDate(2026, 1, 1)))
    }

    @Test
    fun formattingUsesIndonesianNames() {
        assertEquals("Kamis, 1 Januari 2026", CalendarMath.formatLong(CalDate(2026, 1, 1)))
        assertEquals("1 Jan 2026", CalendarMath.formatShort(CalDate(2026, 1, 1)))
        assertEquals("Januari 2026", CalendarMath.formatMonthYear(2026, 1))
        assertEquals("Desember 2026", CalendarMath.formatMonthYear(2026, 12))
    }

    @Test
    fun calDateOrdersByYearThenMonthThenDay() {
        assertTrue(CalDate(2026, 1, 2) > CalDate(2026, 1, 1))
        assertTrue(CalDate(2026, 2, 1) > CalDate(2026, 1, 31))
        assertTrue(CalDate(2027, 1, 1) > CalDate(2026, 12, 31))
        assertEquals(0, CalDate(2026, 5, 5).compareTo(CalDate(2026, 5, 5)))
    }
}
