package com.cheq.retail.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DateTimeUtils {

    companion object {
        fun getDaysBetweenDates(start: String?, end: String?): Long {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val startDate: Date
            val endDate: Date
            var numberOfDays: Long = 0

            try {
                startDate = dateFormat.parse(start)
                endDate = dateFormat.parse(end)
                numberOfDays = getUnitBetweenDates(startDate, endDate, TimeUnit.DAYS)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return numberOfDays
        }

        private fun getUnitBetweenDates(startDate: Date, endDate: Date, unit: TimeUnit): Long {
            val timeDiff = endDate.time - startDate.time
            return unit.convert(timeDiff, TimeUnit.MILLISECONDS)
        }
    }
}