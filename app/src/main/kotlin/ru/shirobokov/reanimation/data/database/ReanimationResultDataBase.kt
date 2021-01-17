package ru.shirobokov.reanimation.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.shirobokov.reanimation.domain.entity.ReanimationHelp

@Dao
interface ReanimationResultDataBase {

    @Insert
    suspend fun addReanimationHelp(ecgList: ReanimationHelp)

    @Query("SELECT * FROM reanimationhelp")
    suspend fun getReanimationHelpList(): List<ReanimationHelp>

    @Query("SELECT * FROM reanimationhelp WHERE startReanimation = :startReanimation")
    suspend fun getReanimationHelp(startReanimation: Long): ReanimationHelp

    @Query("DELETE FROM reanimationhelp WHERE startReanimation = :startReanimation")
    suspend fun deleteReanimationHelp(startReanimation: Long)
}
