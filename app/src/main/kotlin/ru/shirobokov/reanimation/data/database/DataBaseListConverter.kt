package ru.shirobokov.reanimation.data.database

import androidx.room.TypeConverter
import java.util.regex.Pattern

class DataBaseListConverter {

    @TypeConverter
    fun toString(dataList: List<String>): String = StringBuilder().apply {
        dataList.forEach { append("$it$SPLIT_STRING") }
    }.toString()

    @TypeConverter
    fun toList(data: String) = data.split(Pattern.quote(SPLIT_STRING).toRegex()).filter { it != "" }

    companion object {
        private const val SPLIT_STRING = "}}}"
    }
}