package com.cheq.common.utils

import com.cheq.common.extension.empty
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Created by Akash Khatkale on 18th May, 2023.
 * akash.k@cheq.one
 */
object DateUtils {

    private const val DATE_BACKEND_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
    private const val TIME_FORMAT = "hh:mma"
    private const val DATE_MONTH_FORMAT = "dd MMM"
    private const val DATE_MONTH_YEAR_FORMAT = "dd MMM yy"

    fun formattedTimeFromDate(serverDate: String): String {
        try {
            val parser = getSimpleDateFormat(DATE_BACKEND_FORMAT).parse(serverDate)
            val formatter = getSimpleDateFormat(DATE_MONTH_YEAR_FORMAT)
            return parser?.let { formatter.format(it) } ?: String.empty
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return String.empty
    }

    fun getFormattedTimeFromDate(serverDate: String): String {
        try {
            val parser = getSimpleDateFormat(DATE_BACKEND_FORMAT).parse(serverDate)
            val formatter = getSimpleDateFormat(TIME_FORMAT)
            return parser?.let { formatter.format(it) } ?: String.empty
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return String.empty
    }

    fun getFormattedDateFromDate(serverDate: String): String {
        try {
            val parser = getSimpleDateFormat(DATE_BACKEND_FORMAT).parse(serverDate)
            val formatter = getSimpleDateFormat(DATE_MONTH_FORMAT)
            return parser?.let { formatter.format(it) } ?: String.empty
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return String.empty
    }

    private fun getSimpleDateFormat(
        format: String,
        locale: Locale = Locale.ENGLISH
    ): SimpleDateFormat =
        SimpleDateFormat(format, locale)

}