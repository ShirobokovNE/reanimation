package ru.shirobokov.reanimation.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.shirobokov.reanimation.domain.entity.ReanimationHelp

@Database(entities = [ReanimationHelp::class], version = 1)
abstract class DataBase : RoomDatabase() {

    abstract fun getReanimationResultDataBase(): ReanimationResultDataBase
}
