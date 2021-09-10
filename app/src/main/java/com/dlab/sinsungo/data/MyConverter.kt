package com.dlab.sinsungo.data

import androidx.databinding.InverseMethod
import java.lang.Exception

object MyConverter {
    @InverseMethod("stringToInt")
    @JvmStatic
    fun intToString(value: Int): String {
        return try {
            value.toString()
        } catch (e: Exception) {
            ""
        }
    }

    @JvmStatic
    fun stringToInt(value: String): Int {
        return try {
            value.toInt()
        } catch (e: Exception) {
            0
        }
    }
}
