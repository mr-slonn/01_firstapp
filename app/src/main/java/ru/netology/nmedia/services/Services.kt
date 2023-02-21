package ru.netology.nmedia.services

import java.math.RoundingMode
import java.text.DecimalFormat
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


}
