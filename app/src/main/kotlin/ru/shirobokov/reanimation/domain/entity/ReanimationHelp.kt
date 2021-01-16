package ru.shirobokov.reanimation.domain.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.shirobokov.reanimation.data.DataBaseListConverter

@Entity
@TypeConverters(DataBaseListConverter::class)
class ReanimationHelp(
    @PrimaryKey val startReanimation: Long = 0,
    val endReanimation: Long = 0,
    val helpList: List<String> = emptyList()
)
