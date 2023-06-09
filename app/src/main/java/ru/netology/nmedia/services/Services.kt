package ru.netology.nmedia.services

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.ln
import kotlin.math.pow

class Services {
    fun countWithSuffix(count: Int): String {
        val value = count.toDouble()
        val suffixChars = "KMGTPE"
        val formatter = DecimalFormat("##.#")
        formatter.roundingMode = RoundingMode.DOWN
        return if (value < 1000.0) formatter.format(value)
        else {
            val exp = (ln(value) / ln(1000.0)).toInt()
            val preFormat = value / 1000.0.pow(exp.toDouble())
            if (preFormat >= 10.0 || preFormat == 1.0) {
                preFormat.toInt().toString() + suffixChars[exp - 1]
            } else {
                formatter.format(preFormat) + suffixChars[exp - 1]
            }
        }
    }

     fun getDateTime(s: String): String? {
         return try {
             val sdf = SimpleDateFormat("dd MMMM yyyy",
                 Locale.getDefault())
             val netDate = Date(s.toLong() * 1000)
             sdf.format(netDate)
         } catch (e: Exception) {
             e.toString()
         }
    }


}
