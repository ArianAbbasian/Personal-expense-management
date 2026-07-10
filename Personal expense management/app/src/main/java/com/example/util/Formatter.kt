package com.example.util

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Formatter {
    private val decimalFormat = DecimalFormat("#,###")

    fun formatCurrency(amount: Long): String {
        val formattedNumber = decimalFormat.format(amount)
        return "${toPersianDigits(formattedNumber)} تومان"
    }

    fun formatDateTime(timestamp: Long): String {
        val date = Date(timestamp)
        val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.US)
        val formatted = sdf.format(date)
        
        val parts = formatted.split("/")
        if (parts.size == 3) {
            val gy = parts[0].toInt()
            val gm = parts[1].toInt()
            val gd = parts[2].toInt()
            val (jy, jm, jd) = gregorianToJalali(gy, gm, gd)
            return toPersianDigits(String.format(Locale.US, "%d/%02d/%02d", jy, jm, jd))
        }
        return toPersianDigits(formatted)
    }

    fun toPersianDigits(input: String): String {
        return input.replace('0', '۰')
            .replace('1', '۱')
            .replace('2', '۲')
            .replace('3', '۳')
            .replace('4', '۴')
            .replace('5', '۵')
            .replace('6', '۶')
            .replace('7', '۷')
            .replace('8', '۸')
            .replace('9', '۹')
    }

    private fun gregorianToJalali(gy: Int, gm: Int, gd: Int): Triple<Int, Int, Int> {
        val gDaysInMonth = intArrayOf(0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        val jDaysInMonth = intArrayOf(0, 31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

        val gy2 = if (gm > 2) gy else gy - 1
        var gDays = 365 * gy + gy / 4 - gy / 100 + gy / 400 + gd
        for (i in 1 until gm) {
            gDays += gDaysInMonth[i]
        }
        if (gm > 2 && ((gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0))) {
            gDays += 1
        }

        val jDays = gDays - 79

        val jNp = jDays / 12053
        var jDaysLeft = jDays % 12053

        var jy = 979 + 33 * jNp + 4 * (jDaysLeft / 1461)
        jDaysLeft %= 1461

        if (jDaysLeft >= 366) {
            jy += (jDaysLeft - 1) / 365
            jDaysLeft = (jDaysLeft - 1) % 365
        }

        var jm = 1
        for (i in 1..12) {
            val daysInThisMonth = if (i == 12 && (jy % 33) in intArrayOf(1, 5, 9, 13, 17, 22, 26, 30)) 30 else jDaysInMonth[i]
            if (jDaysLeft < daysInThisMonth) {
                jm = i
                break
            }
            jDaysLeft -= daysInThisMonth
        }
        val jd = jDaysLeft + 1
        return Triple(jy, jm, jd)
    }
}
