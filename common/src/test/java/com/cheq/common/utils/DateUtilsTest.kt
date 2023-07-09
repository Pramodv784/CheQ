package com.cheq.common.utils

import org.junit.Assert.*
import org.junit.Test

/**
 * Created by Akash Khatkale on 22nd May, 2023.
 * akash.k@cheq.one
 */
class DateUtilsTest {

    @Test
    fun `When date format is correct, then return date`() {
        val date = DateUtils.formattedTimeFromDate("2023-05-14T12:19:10.990Z")
        val date2 = DateUtils.formattedTimeFromDate("2023-06-7T12:19:10.990Z")

        assertEquals("14 May 23", date)
        assertEquals("07 Jun 23", date2)
    }

    @Test
    fun `When date format is incorrect, then return empty string`() {
        val date = DateUtils.formattedTimeFromDate("2023-0514T12:1910.990Z")
        val date2 = DateUtils.formattedTimeFromDate("")

        assertEquals("", date)
        assertEquals("", date2)
    }

    @Test
    fun `When time format is correct, then return time`() {
        val date = DateUtils.getFormattedTimeFromDate("2023-05-14T12:19:10.990Z")
        val date2 = DateUtils.getFormattedTimeFromDate("2023-06-7T13:19:10.990Z")

        assertEquals("12:19PM", date)
        assertEquals("01:19PM", date2)
    }

    @Test
    fun `When time format is incorrect, then return empty string`() {
        val date = DateUtils.getFormattedTimeFromDate("2023-0514T12:19:10.990Z")
        val date2 = DateUtils.getFormattedTimeFromDate("20236-7T13:19:10.990Z")
        val date3 = DateUtils.getFormattedTimeFromDate("")

        assertEquals("", date)
        assertEquals("", date2)
        assertEquals("", date3)
    }

    @Test
    fun `When date month format is correct, then return date`() {
        val date = DateUtils.getFormattedDateFromDate("2023-05-14T12:19:10.990Z")
        val date2 = DateUtils.getFormattedDateFromDate("2023-06-7T12:19:10.990Z")

        assertEquals("14 May", date)
        assertEquals("07 Jun", date2)
    }

    @Test
    fun `When date month format is incorrect, then return empty string`() {
        val date = DateUtils.getFormattedDateFromDate("2023-0514T12:1910.990Z")
        val date2 = DateUtils.getFormattedDateFromDate("")

        assertEquals("", date)
        assertEquals("", date2)
    }

}